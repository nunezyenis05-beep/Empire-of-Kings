FROM nvidia/cuda:12.4.1-devel-ubuntu22.04
ENV DEBIAN_FRONTEND=noninteractive PYTHONUNBUFFERED=1 PIP_NO_CACHE_DIR=1 OPENCV_IO_ENABLE_OPENEXR=1 PYTORCH_CUDA_ALLOC_CONF=expandable_segments:True
RUN apt-get update && apt-get install -y --no-install-recommends python3 python3-pip git git-lfs ffmpeg libgl1 libglib2.0-0 libsm6 libxext6 libxrender1 build-essential cmake ninja-build && rm -rf /var/lib/apt/lists/*
WORKDIR /opt
RUN git clone --recursive https://github.com/microsoft/TRELLIS.2.git trellis2
WORKDIR /opt/trellis2
RUN python3 -m pip install --upgrade pip setuptools wheel && python3 setup.py develop
WORKDIR /opt
RUN git clone --recursive https://github.com/microsoft/TRELLIS.git trellis && cd trellis && python3 setup.py develop
WORKDIR /studio
COPY requirements.txt .
RUN pip install -r requirements.txt
COPY . .
ENV STUDIO_DATA=/studio/data STUDIO_ADMIN_TOKEN=change-me
EXPOSE 8000
CMD ["python3","-m","uvicorn","server:app","--host","0.0.0.0","--port","8000"]
