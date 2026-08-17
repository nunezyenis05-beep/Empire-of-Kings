import os
os.environ.setdefault('ATTN_BACKEND','xformers')
os.environ.setdefault('SPCONV_ALGO','native')
os.environ.setdefault('PYTORCH_CUDA_ALLOC_CONF','expandable_segments:True')
from fastapi import FastAPI, UploadFile, File, Form, Header, HTTPException
from fastapi.responses import FileResponse
from pathlib import Path
from PIL import Image
import sqlite3, uuid, shutil, threading, traceback

ROOT=Path(os.getenv('STUDIO_DATA','./studio_data')); ROOT.mkdir(parents=True,exist_ok=True)
DB=ROOT/'studio.db'; ADMIN=os.getenv('STUDIO_ADMIN_TOKEN','change-me'); MAX_IMAGES=20
app=FastAPI(title='Empire of Kings Private 3D Studio')
_engine=None; engine_lock=threading.Lock(); generation_lock=threading.Lock()

def db():
 c=sqlite3.connect(DB); c.row_factory=sqlite3.Row
 c.execute('CREATE TABLE IF NOT EXISTS assets(id TEXT PRIMARY KEY,name TEXT,category TEXT,status TEXT,error TEXT,folder TEXT,mode TEXT,created_at TEXT DEFAULT CURRENT_TIMESTAMP)'); c.commit(); return c

def auth(authorization):
 if authorization!=f'Bearer {ADMIN}': raise HTTPException(401,'Private access required')

def row(aid):
 c=db(); r=c.execute('SELECT * FROM assets WHERE id=?',(aid,)).fetchone(); c.close()
 if not r: raise HTTPException(404,'Asset not found')
 return dict(r)

def engine():
 global _engine
 if _engine is None:
  with engine_lock:
   if _engine is None:
    import torch
    if not torch.cuda.is_available(): raise RuntimeError('No NVIDIA CUDA GPU available')
    from trellis.pipelines import TrellisImageTo3DPipeline
    _engine=TrellisImageTo3DPipeline.from_pretrained('microsoft/TRELLIS-image-large'); _engine.cuda()
 return _engine

@app.get('/api/health')
def health():
 try:
  import torch
  return {'ok':True,'gpu':bool(torch.cuda.is_available()),'engine':'TRELLIS-image-large','max_images':MAX_IMAGES}
 except Exception as e: return {'ok':False,'error':str(e)}

@app.get('/api/assets')
def assets(authorization:str|None=Header(default=None)):
 auth(authorization); c=db(); out=[dict(x) for x in c.execute('SELECT * FROM assets ORDER BY created_at DESC')]; c.close()
 for x in out:
  p=Path(x['folder']); x['glb']=(p/'model.glb').exists(); x['thumbnail']=(p/'thumbnail.jpg').exists()
 return out

@app.get('/api/assets/{aid}')
def asset(aid:str,authorization:str|None=Header(default=None)):
 auth(authorization); x=row(aid); p=Path(x['folder']); x['glb']=(p/'model.glb').exists(); x['thumbnail']=(p/'thumbnail.jpg').exists(); return x

@app.get('/api/assets/{aid}/thumbnail')
def thumbnail(aid:str,authorization:str|None=Header(default=None)):
 auth(authorization); p=Path(row(aid)['folder'])/'thumbnail.jpg'
 if not p.exists(): raise HTTPException(404,'Thumbnail unavailable')
 return FileResponse(p,media_type='image/jpeg')

@app.get('/api/assets/{aid}/glb')
def glb(aid:str,authorization:str|None=Header(default=None)):
 auth(authorization); p=Path(row(aid)['folder'])/'model.glb'
 if not p.exists(): raise HTTPException(404,'GLB not ready')
 return FileResponse(p,media_type='model/gltf-binary',filename=f'{aid}.glb')

@app.post('/api/assets')
async def create_asset(name:str=Form(...),category:str=Form('Other'),images:list[UploadFile]=File(...),authorization:str|None=Header(default=None)):
 auth(authorization)
 if not 1<=len(images)<=MAX_IMAGES: raise HTTPException(400,f'Upload between 1 and {MAX_IMAGES} images')
 aid='EOK3D-'+uuid.uuid4().hex[:10].upper(); folder=ROOT/'assets'/aid; src=folder/'images'; src.mkdir(parents=True)
 for n,img in enumerate(images,1):
  if not (img.content_type or '').startswith('image/'): raise HTTPException(400,'All inputs must be images')
  with (src/f'{n:02d}-{Path(img.filename or "image").name}').open('wb') as out: shutil.copyfileobj(img.file,out)
 first=Image.open(sorted(src.iterdir())[0]).convert('RGB'); first.thumbnail((800,800)); first.save(folder/'thumbnail.jpg',quality=82)
 mode='single-image' if len(images)==1 else 'multi-image'
 c=db(); c.execute('INSERT INTO assets(id,name,category,status,folder,mode) VALUES(?,?,?,?,?,?)',(aid,name,category,'queued',str(folder),mode)); c.commit(); c.close()
 threading.Thread(target=generate,args=(aid,),daemon=True).start(); return {'id':aid,'name':name,'status':'queued','mode':mode}

def generate(aid):
 x=row(aid); folder=Path(x['folder']); src=folder/'images'; c=db(); c.execute('UPDATE assets SET status=? WHERE id=?',('processing',aid)); c.commit(); c.close()
 try:
  from trellis.utils import postprocessing_utils
  images=[Image.open(p).convert('RGBA') for p in sorted(src.iterdir())]
  with generation_lock:
   pipe=engine()
   if len(images)==1:
    prepared=pipe.preprocess_image(images[0])
    outputs=pipe.run(prepared,seed=0,formats=['gaussian','mesh'],preprocess_image=False,sparse_structure_sampler_params={'steps':12,'cfg_strength':7.5},slat_sampler_params={'steps':12,'cfg_strength':3.0})
   else:
    prepared=[pipe.preprocess_image(im) for im in images]
    outputs=pipe.run_multi_image(prepared,seed=0,formats=['gaussian','mesh'],preprocess_image=False,sparse_structure_sampler_params={'steps':12,'cfg_strength':7.5},slat_sampler_params={'steps':12,'cfg_strength':3.0},mode='stochastic')
   result=postprocessing_utils.to_glb(outputs['gaussian'][0],outputs['mesh'][0],simplify=0.95,texture_size=2048,verbose=False)
   result.export(folder/'model.glb'); del outputs,result
   import torch; torch.cuda.empty_cache()
  c=db(); c.execute('UPDATE assets SET status=?,error=NULL WHERE id=?',('ready',aid)); c.commit(); c.close()
 except Exception as e:
  traceback.print_exc(); c=db(); c.execute('UPDATE assets SET status=?,error=? WHERE id=?',('error',str(e),aid)); c.commit(); c.close()
