from fastapi import FastAPI, UploadFile, File, Form, Header, HTTPException
from fastapi.responses import FileResponse
from pathlib import Path
from PIL import Image
import sqlite3, uuid, shutil, os, subprocess, threading, json

ROOT=Path(os.getenv('STUDIO_DATA','./studio_data')); ROOT.mkdir(parents=True,exist_ok=True)
DB=ROOT/'studio.db'; ADMIN=os.getenv('STUDIO_ADMIN_TOKEN','change-me')
app=FastAPI(title='Empire of Kings Private 3D Studio')

def db():
 c=sqlite3.connect(DB); c.row_factory=sqlite3.Row; c.execute('CREATE TABLE IF NOT EXISTS assets(id TEXT PRIMARY KEY,name TEXT,category TEXT,status TEXT,error TEXT,folder TEXT,created_at TEXT DEFAULT CURRENT_TIMESTAMP)'); c.commit(); return c

def auth(authorization: str|None):
 if authorization!=f'Bearer {ADMIN}': raise HTTPException(401,'Private access required')

def asset_row(i):
 c=db(); r=c.execute('SELECT * FROM assets WHERE id=?',(i,)).fetchone(); c.close();
 if not r: raise HTTPException(404,'Asset not found')
 return dict(r)

@app.get('/api/health')
def health(): return {'ok':True,'service':'private-3d-studio'}

@app.get('/api/assets')
def assets(authorization: str|None=Header(default=None)):
 auth(authorization); c=db(); rows=[dict(r) for r in c.execute('SELECT * FROM assets ORDER BY created_at DESC')]; c.close()
 for r in rows:
  p=Path(r['folder']); r['glb']=(p/'model.glb').exists(); r['thumbnail']=(p/'thumbnail.jpg').exists()
 return rows

@app.get('/api/assets/{asset_id}')
def get_asset(asset_id:str,authorization:str|None=Header(default=None)):
 auth(authorization); r=asset_row(asset_id); p=Path(r['folder']); r['glb']=(p/'model.glb').exists(); r['thumbnail']=(p/'thumbnail.jpg').exists(); return r

@app.get('/api/assets/{asset_id}/thumbnail')
def thumb(asset_id:str,authorization: str|None=Header(default=None)):
 auth(authorization); p=Path(asset_row(asset_id)['folder'])/'thumbnail.jpg';
 if not p.exists(): raise HTTPException(404,'Thumbnail unavailable')
 return FileResponse(p,media_type='image/jpeg')

@app.get('/api/assets/{asset_id}/glb')
def glb(asset_id:str,authorization: str|None=Header(default=None)):
 auth(authorization); p=Path(asset_row(asset_id)['folder'])/'model.glb';
 if not p.exists(): raise HTTPException(404,'GLB not ready')
 return FileResponse(p,media_type='model/gltf-binary',filename=f'{asset_id}.glb')

@app.post('/api/assets')
async def create_asset(name:str=Form(...),category:str=Form('Other'),images:list[UploadFile]=File(...),authorization:str|None=Header(default=None)):
 auth(authorization)
 if len(images)<2 or len(images)>20: raise HTTPException(400,'Upload between 2 and 20 images')
 aid='EOK3D-'+uuid.uuid4().hex[:10].upper(); folder=ROOT/'assets'/aid; src=folder/'images'; src.mkdir(parents=True)
 for n,img in enumerate(images,1):
  if not (img.content_type or '').startswith('image/'): raise HTTPException(400,'All inputs must be images')
  with (src/f'{n:02d}-{Path(img.filename or "image").name}').open('wb') as out: shutil.copyfileobj(img.file,out)
 Image.open(next(src.iterdir())).convert('RGB').thumbnail((800,800)); Image.open(next(src.iterdir())).convert('RGB').save(folder/'thumbnail.jpg',quality=82)
 c=db(); c.execute('INSERT INTO assets(id,name,category,status,folder) VALUES(?,?,?,?,?)',(aid,name,category,'queued',str(folder))); c.commit(); c.close()
 threading.Thread(target=reconstruct,args=(aid,),daemon=True).start(); return {'id':aid,'name':name,'status':'queued'}

def run(cmd,cwd,log):
 with open(log,'a') as f: return subprocess.run(cmd,cwd=cwd,stdout=f,stderr=subprocess.STDOUT,text=True).returncode

def reconstruct(aid):
 r=asset_row(aid); folder=Path(r['folder']); src=folder/'images'; dbx=folder/'colmap.db'; sparse=folder/'sparse'; dense=folder/'dense'; log=folder/'process.log'
 c=db(); c.execute('UPDATE assets SET status=? WHERE id=?',('processing',aid)); c.commit(); c.close()
 try:
  # Real image-based reconstruction: SfM -> dense MVS -> mesh -> GLB.
  cmds=[
   (['colmap','feature_extractor','--database_path',str(dbx),'--image_path',str(src),'--ImageReader.single_camera','1'],folder),
   (['colmap','exhaustive_matcher','--database_path',str(dbx)],folder),
   (['colmap','mapper','--database_path',str(dbx),'--image_path',str(src),'--output_path',str(sparse)],folder),
  ]
  for cmd,cwd in cmds:
   if run(cmd,cwd,log)!=0: raise RuntimeError(f'COLMAP failed: {cmd[1]}')
  models=list(sparse.glob('*/images.bin'))
  if not models: raise RuntimeError('No camera reconstruction was produced. Use more overlapping views.')
  model=models[0].parent
  if run(['colmap','image_undistorter','--image_path',str(src),'--input_path',str(model),'--output_path',str(dense),'--output_type','COLMAP'],folder,log)!=0: raise RuntimeError('Undistortion failed')
  if run(['colmap','patch_match_stereo','--workspace_path',str(dense),'--workspace_format','COLMAP','--PatchMatchStereo.geom_consistency','true'],folder,log)!=0: raise RuntimeError('Dense stereo failed')
  fused=dense/'fused.ply'
  if run(['colmap','stereo_fusion','--workspace_path',str(dense),'--workspace_format','COLMAP','--output_path',str(fused)],folder,log)!=0: raise RuntimeError('Stereo fusion failed')
  mesh=folder/'mesh.ply'
  if run(['colmap','poisson_mesher','--input_path',str(fused),'--output_path',str(mesh)],folder,log)!=0: raise RuntimeError('Meshing failed')
  # Convert the reconstructed mesh to a single portable GLB container.
  import trimesh
  scene=trimesh.load(mesh,force='scene'); scene.export(folder/'model.glb',file_type='glb')
  c=db(); c.execute('UPDATE assets SET status=?,error=NULL WHERE id=?',('ready',aid)); c.commit(); c.close()
 except Exception as e:
  c=db(); c.execute('UPDATE assets SET status=?,error=? WHERE id=?',('error',str(e),aid)); c.commit(); c.close()
