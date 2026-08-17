FROM nvidia/cuda:12.1.1-devel-ubuntu22.04
ENV DEBIAN_FRONTEND=noninteractive PYTHONUNBUFFERED=1 PIP_NO_CACHE_DIR=1 ATTN_BACKEND=xformers SPCONV_ALGO=native PYTORCH_CUDA_ALLOC_CONF=expandable_segments:True
RUN apt-get update && apt-get install -y --no-install-recommends wget git git-lfs ffmpeg libgl1 libglib2.0-0 libsm6 libxext6 libxrender1 build-essential cmake ninja-build && rm -rf /var/lib/apt/lists/*
RUN wget -qO /tmp/miniconda.sh https://repo.anaconda.com/miniconda/Miniconda3-latest-Linux-x86_64.sh && bash /tmp/miniconda.sh -b -p /opt/conda && rm /tmp/miniconda.sh
ENV PATH=/opt/conda/bin:$PATH
WORKDIR /opt
RUN git clone --recursive https://github.com/microsoft/TRELLIS.git trellis
WORKDIR /opt/trellis
RUN bash setup.sh --new-env --basic --xformers --diffoctreerast --spconv --mipgaussian --kaolin --nvdiffrast --demo
COPY requirements.txt /opt/requirements.txt
RUN conda run --no-capture-output -n trellis pip install -r /opt/requirements.txt
WORKDIR /studio
COPY . .
ENV STUDIO_DATA=/studio/data STUDIO_ADMIN_TOKEN=change-me
EXPOSE 8000
CMD ["conda","run","--no-capture-output","-n","trellis","python","-m","uvicorn","server:app","--host","0.0.0.0","--port","8000"]
