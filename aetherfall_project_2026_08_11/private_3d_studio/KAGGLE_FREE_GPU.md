# Free GPU launcher

`kaggle_free_gpu.ipynb` is the free-GPU launcher for the Private 3D Studio.

It uses the official TRELLIS image-to-3D pipeline, accepts 1–20 images, exports GLB, serves the Studio UI from the same GPU session, and creates a temporary HTTPS Quick Tunnel.

## Runtime

- Kaggle Notebook
- NVIDIA GPU accelerator (P100 is the intended free target)
- Internet enabled in the notebook session
- TRELLIS original (`microsoft/TRELLIS-image-large`)
- FastAPI + Uvicorn
- Cloudflare Quick Tunnel

The generated private token is printed only in the notebook output and is required for the Studio API.

The GPU session is ephemeral. Model files are stored in the session's working directory, so download/export important GLBs before stopping the session.
