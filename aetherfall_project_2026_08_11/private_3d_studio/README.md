# EMPIRE OF KINGS — Private 3D Studio

Private mobile-first asset lab for EMPIRE OF KINGS. It accepts 2–20 images for one object, runs real image-based reconstruction on a server with COLMAP, and exports a GLB.

## Layout
- `index.html` — mobile PWA UI
- `app.js` — upload, job polling and private library
- `server.py` — authenticated API, asset storage and reconstruction worker
- `Dockerfile` — server image with COLMAP

## Run
1. Build the container: `docker build -t eok-3d-studio .`
2. Start it with a private token: `docker run -p 8000:8000 -e STUDIO_ADMIN_TOKEN='YOUR_PRIVATE_TOKEN' -v eok3d:/studio/data eok-3d-studio`
3. Serve the frontend from the same origin as `/api` (reverse proxy `/api` to port 8000).

The reconstruction pipeline is intentionally server-side: multi-view reconstruction needs real compute and cannot reliably be done by a mobile browser alone. The output is a real `.glb` file, not a visual mockup.

For best reconstruction quality, photograph the same object from overlapping viewpoints with stable lighting and visible texture.
