"""Private single-image TRELLIS.2 runner.

Expected environment: the official Microsoft TRELLIS.2 checkout is installed and
its Python environment is active. The studio server calls this script with:
image_path output_glb resolution decimation_target texture_size
"""
import os, sys
os.environ['OPENCV_IO_ENABLE_OPENEXR']='1'
os.environ['PYTORCH_CUDA_ALLOC_CONF']='expandable_segments:True'

from PIL import Image
import torch
from trellis2.pipelines import Trellis2ImageTo3DPipeline
import o_voxel

if len(sys.argv)!=6:
    raise SystemExit('usage: studio_runner.py IMAGE OUTPUT_GLB RESOLUTION FACES TEXTURE_SIZE')
image_path, output_path, resolution, faces, texture_size = sys.argv[1:]
resolution=int(resolution); faces=int(faces); texture_size=int(texture_size)
if not torch.cuda.is_available():
    raise SystemExit('TRELLIS.2 requires an NVIDIA CUDA GPU for this deployment')

pipeline=Trellis2ImageTo3DPipeline.from_pretrained('microsoft/TRELLIS.2-4B')
pipeline.cuda()
image=Image.open(image_path).convert('RGBA')
mesh=pipeline.run(image, resolution=resolution)[0]
mesh.simplify(16777216)
glb=o_voxel.postprocess.to_glb(
    vertices=mesh.vertices,
    faces=mesh.faces,
    attr_volume=mesh.attrs,
    coords=mesh.coords,
    attr_layout=mesh.layout,
    voxel_size=mesh.voxel_size,
    aabb=[[-0.5,-0.5,-0.5],[0.5,0.5,0.5]],
    decimation_target=faces,
    texture_size=texture_size,
    remesh=True,
    remesh_band=1,
    remesh_project=0,
    verbose=True,
)
glb.export(output_path, extension_webp=True)
torch.cuda.empty_cache()
print(output_path)
