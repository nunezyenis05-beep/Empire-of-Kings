#!/usr/bin/env python3
"""Build visually distinct human variants from the supplied rigged Sophia base.

This is a corrective pass: male profiles change body/face geometry and texture
presentation, while every output keeps the original skin, joints and animation.
The base is still one scanned source; this does not claim ten independent scans.
"""
from pathlib import Path
import io, json, struct, sys, random, math
import numpy as np
from PIL import Image, ImageEnhance, ImageDraw

base = Path(sys.argv[1]); out = Path(sys.argv[2]); out.mkdir(parents=True, exist_ok=True)
data = base.read_bytes(); pos = 12; root = binary = None
while pos < len(data):
    size, typ = struct.unpack_from('<II', data, pos); pos += 8
    payload = data[pos:pos+size]; pos += size
    if typ == 0x4E4F534A: root = json.loads(payload.decode('utf-8'))
    elif typ == 0x004E4942: binary = payload
assert root is not None and binary is not None

prim = root['meshes'][0]['primitives'][0]
pos_accessor = root['accessors'][prim['attributes']['POSITION']]
pos_view = root['bufferViews'][pos_accessor['bufferView']]
pos_start = pos_view.get('byteOffset', 0) + pos_accessor.get('byteOffset', 0)
count = pos_accessor['count']
V = np.frombuffer(binary, dtype='<f4', count=count*3, offset=pos_start).reshape((-1,3)).copy()
uv_accessor = root['accessors'][prim['attributes']['TEXCOORD_0']]
uv_view = root['bufferViews'][uv_accessor['bufferView']]
uv_start = uv_view.get('byteOffset',0) + uv_accessor.get('byteOffset',0)
UV = np.frombuffer(binary, dtype='<f4', count=count*2, offset=uv_start).reshape((-1,2))

image_view_index = root['images'][0]['bufferView']; image_view = root['bufferViews'][image_view_index]
image_start = image_view.get('byteOffset',0); image_end = image_start + image_view['byteLength']
img = Image.open(io.BytesIO(binary[image_start:image_end])); img.draft('RGB',(2048,2048)); img = img.convert('RGB').resize((2048,2048), Image.Resampling.LANCZOS)
W,H=img.size
base_tex=np.asarray(img)

variants = {
 'maya': ('Maya','female',(0.98,1.00,0.98),(1.02,0.98,0.96),0),
 'sofia': ('Sofia','female',(1.00,1.01,1.00),(0.98,1.02,1.03),1),
 'amara': ('Amara','female',(1.04,0.99,1.02),(0.96,0.90,0.84),2),
 'elena': ('Elena','female',(0.97,1.03,0.99),(0.90,0.97,1.04),3),
 'nadia': ('Nadia','female',(1.06,1.00,1.04),(1.04,0.92,0.82),4),
 'leo': ('Leo','male',(1.14,1.04,1.09),(0.88,0.94,1.04),5),
 'mateo': ('Mateo','male',(1.20,1.03,1.13),(0.96,0.84,0.72),6),
 'karim': ('Karim','male',(1.10,1.07,1.10),(0.82,0.91,0.98),7),
 'daniel': ('Daniel','male',(1.16,1.02,1.08),(1.02,0.86,0.76),8),
 'isaac': ('Isaac','male',(1.08,1.06,1.12),(0.90,0.84,1.02),9),
}

def make_texture(gender, seed, tint):
    rng=random.Random(seed); im=ImageEnhance.Color(img).enhance(0.92 + (seed%4)*0.045)
    im=ImageEnhance.Contrast(im).enhance(0.98 + (seed%3)*0.035)
    px=np.asarray(im).astype(np.float32)
    # Unique skin grade, restrained so the source remains recognizable.
    skin = (px[:,:,0] > px[:,:,2]*1.05) & (px[:,:,1] > px[:,:,2]*0.93) & (px[:,:,0] > 65) & (px[:,:,0] < 245)
    for c in range(3): px[:,:,c][skin] *= (0.96 + tint[c]*0.035)
    im=Image.fromarray(np.clip(px,0,255).astype(np.uint8),'RGB')
    d=ImageDraw.Draw(im,'RGBA')
    # The face island is in the upper-right UV quadrant of this source texture.
    # Add a distinct, subtle male stubble/beard map and stronger brow definition.
    if gender=='male':
        beard=(28+seed*7,22+seed*3,25+seed*2,120)
        # Face UV island: a soft jaw shadow plus irregular stubble. The
        # moustache/goatee are intentionally visible in the mobile preview.
        d.ellipse((1775,150,2005,350), fill=(35+seed*4,27,25,42))
        d.rectangle((1830,208,1960,258), fill=(35+seed*4,27,25,70))
        d.ellipse((1870,245,1925,335), fill=(35+seed*4,27,25,78))
        for i in range(360 + seed*16):
            x=rng.randint(1775,2005); y=rng.randint(145,350)
            if y < 215 and rng.random() < .8: continue
            r=rng.choice([1,1,2,2,3]); d.ellipse((x-r,y-r,x+r,y+r),fill=beard)
        brow=(35+seed*4,25,24,155)
        d.line((1778,100,1870,82),fill=brow,width=12)
        d.line((1935,82,2020,103),fill=brow,width=12)
    else:
        # Small, different brow/eye color accents prevent identical facial reads.
        brow=(55+seed*4,32+seed*2,48+seed*3,80)
        d.line((1785,170,1865,155),fill=brow,width=6)
        d.line((1940,155,2010,170),fill=brow,width=6)
    # Darken the bright blouse on male profiles so the silhouette reads less like
    # the source styling; this is a texture treatment, not a claimed armor mesh.
    if gender=='male':
        a=np.asarray(im).copy(); bright=(a.mean(2)>150)&((a.max(2)-a.min(2))<55)
        # Avoid the face island and keep skin/teeth intact.
        yy,xx=np.mgrid[0:H,0:W]; body=~((xx>1680)&(yy<560))
        mask=bright&body
        navy=np.array([38+seed*2,48+seed,72+seed*2],dtype=np.uint8)
        a[mask]=(a[mask]*0.18+navy*0.82).astype(np.uint8)
        im=Image.fromarray(a,'RGB')
    b=io.BytesIO(); im.save(b,format='JPEG',quality=88,optimize=True); return b.getvalue()

def make_geometry(gender, scale, seed):
    X=V.copy(); y=X[:,1]; x=X[:,0]; z=X[:,2]
    # Distinct body proportions.
    if gender=='male':
        shoulder=np.clip((y-0.72)/0.60,0,1)
        hip=np.clip((0.78-y)/0.50,0,1)
        X[:,0] *= 1.0 + shoulder*(scale[0]-1.0) - hip*0.035
        # Stronger, visibly different jaw/face profiles for the male set.
        head=y>1.30; front=z>-.03
        jaw=np.clip((1.48-y)/0.20,0,1)
        face=head & (np.abs(x)<0.42)
        X[face,0] *= 1.07 + 0.018*((seed%3)-1)
        X[head,0] *= 1.04 + 0.07*jaw[head]
        X[head & front,2] += 0.010 + 0.004*jaw[head & front]
        X[head,1] += (seed-7)*0.0015
        # Compress the long-hair region into a short, masculine silhouette.
        # The source's upper UV islands identify the hair vertices; the rig and
        # animation stay untouched.
        uv_rgb=base_tex[np.clip(((1-UV[:,1])*(H-1)).astype(int),0,H-1), np.clip((UV[:,0]*(W-1)).astype(int),0,W-1)]
        hair=(y>1.27)&((uv_rgb[:,0]>uv_rgb[:,2]*1.15)&(uv_rgb[:,1]>uv_rgb[:,2]*1.05))
        old_y=X[hair,1]
        X[hair,1]=1.48 + np.clip(old_y-1.48,-0.08,0.16)
    else:
        X[:,0] *= scale[0]
        head=y>1.30; jaw=np.clip((1.48-y)/0.20,0,1)
        face=head & (np.abs(x)<0.42)
        X[face,0] *= 0.96 + 0.025*(seed%4)
        X[head,0] *= 1.0 + (scale[0]-1.0)*0.25*jaw[head]
        X[head,2] += (seed-2)*0.003
    X[:,1] *= scale[1]; X[:,2] *= scale[2]
    return X

for ident,(label,gender,scale,tint,seed) in variants.items():
    X=make_geometry(gender,scale,seed)
    b=bytearray(binary)
    b[pos_start:pos_start+X.nbytes]=X.astype('<f4').tobytes()
    # Build texture replacement after the position bytes have been updated.
    tex=make_texture(gender,seed,tint); delta=len(tex)-(image_end-image_start)
    b=bytes(b[:image_start])+tex+bytes(b[image_end:])
    r=json.loads(json.dumps(root)); r.setdefault('asset',{})['extras']={
      'eokAvatarVariant':ident,'displayName':label,'genderPresentation':gender,
      'baseModel':'renderpeople_sophia.glb','variantStyle':'distinct-proportion-texture-pass',
      'note':'Corrective derived human variant: geometry proportions and face texture differ; source remains one Sophia scan; license review required.'}
    # keep existing animation and skin; node scale is neutral after vertex pass
    r['nodes'][1]['scale']=[1.0,1.0,1.0]
    r['accessors'][prim['attributes']['POSITION']]['min']=X.min(0).tolist()
    r['accessors'][prim['attributes']['POSITION']]['max']=X.max(0).tolist()
    r['bufferViews'][image_view_index]['byteLength']=len(tex)
    for i,v in enumerate(r['bufferViews']):
        if i != image_view_index and v.get('byteOffset') is not None and v['byteOffset'] >= image_end:
            v['byteOffset'] += delta
    b += b'\0'*((4-len(b)%4)%4); r['buffers'][0]['byteLength']=len(b)
    js=json.dumps(r,separators=(',',':'),ensure_ascii=False).encode(); js+=b' '*((4-len(js)%4)%4)
    result=bytearray(struct.pack('<4sII',b'glTF',2,12+8+len(js)+8+len(b)))
    result+=struct.pack('<II',len(js),0x4E4F534A)+js
    result+=struct.pack('<II',len(b),0x004E4942)+b
    (out/f'{ident}.glb').write_bytes(result)
    print(ident,gender,len(result))
