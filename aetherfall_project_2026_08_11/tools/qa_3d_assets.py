#!/usr/bin/env python3
"""Static gate for the redistributable Android 3D catalog and GLB contracts."""
from pathlib import Path
import hashlib
import json
import struct

ROOT = Path(__file__).resolve().parents[1]
ASSETS = ROOT / "app/src/main/assets"
CATALOG = ASSETS / "models/avatar_catalog.json"
MANIFEST_PATH = ROOT / "docs/3d-assets/3D_ASSET_LICENSE_MANIFEST.json"
PROVENANCE = ROOT / "docs/3d-assets"
errors: list[str] = []


def check(condition: bool, message: str) -> None:
    if not condition:
        errors.append(message)


def glb_json(path: Path) -> dict:
    try:
        data = path.read_bytes()
        check(len(data) >= 20, f"{path.name}: file too short")
        if len(data) < 20:
            return {}
        magic, version, length = struct.unpack_from("<4sII", data, 0)
        check(magic == b"glTF", f"{path.name}: invalid glTF magic")
        check(version == 2, f"{path.name}: expected glTF 2")
        check(length == len(data), f"{path.name}: header length mismatch")
        offset = 12
        root = None
        saw_bin = False
        while offset + 8 <= len(data):
            chunk_length, chunk_type = struct.unpack_from("<II", data, offset)
            offset += 8
            chunk = data[offset:offset + chunk_length]
            offset += chunk_length
            if chunk_type == 0x4E4F534A:
                root = json.loads(chunk.rstrip(b" \t\r\n\0").decode("utf-8"))
            elif chunk_type == 0x004E4942:
                saw_bin = True
        check(root is not None, f"{path.name}: missing JSON chunk")
        check(saw_bin, f"{path.name}: missing BIN chunk")
        return root or {}
    except (OSError, ValueError, json.JSONDecodeError, struct.error) as exc:
        errors.append(f"{path.name}: malformed GLB ({exc})")
        return {}


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for block in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


catalog = json.loads(CATALOG.read_text(encoding="utf-8"))
manifest = json.loads(MANIFEST_PATH.read_text(encoding="utf-8"))
entries = catalog["entries"]
check(len(entries) == 10, "catalog must contain exactly ten active avatars")
check(catalog["fallbackModel"] == "models/Xbot.glb", "fallback must remain Xbot.glb")
check(catalog.get("sourceReview", {}).get("finalStatus", "").startswith("NOT_COMPLETE"),
      "catalog must not claim the stylized cast is complete")

# No Renderpeople GLB, especially the explicitly prohibited posed candidate, may
# be bundled in the redistributable Android project.
for forbidden in ("models/renderpeople_sophia.glb", "models/rp_posed_00178_29.glb"):
    check(not (ASSETS / forbidden).exists(), f"prohibited asset remains bundled: {forbidden}")
for path in ASSETS.rglob("*.glb"):
    check("renderpeople" not in path.name.lower() and "rp_posed" not in path.name.lower(),
          f"prohibited Renderpeople GLB remains: {path.relative_to(ASSETS)}")

all_glbs = sorted(ASSETS.rglob("*.glb"))
check(len(all_glbs) == 11, f"expected ten active GLBs plus Xbot fallback, found {len(all_glbs)}")
for path in all_glbs:
    gltf = glb_json(path)
    check(bool(gltf.get("meshes")), f"{path.relative_to(ASSETS)}: no mesh")

# Every catalogued character has a complete static contract and a complete
# provenance/license record. The neutral clip is checked by exact name.
active_ids = set()
manifest_by_path = {a.get("path"): a for a in manifest.get("assets", [])}
for entry in entries:
    avatar_id = entry["id"]
    active_ids.add(avatar_id)
    model = ASSETS / entry["model"]
    check(model.is_file(), f"{avatar_id}: missing model {entry['model']}")
    check(entry.get("status") == "licensed_human_source_android_contract_validated",
          f"{avatar_id}: source status must be licensed and statically validated")
    record = manifest_by_path.get("app/src/main/assets/" + entry["model"])
    check(record is not None, f"{avatar_id}: missing license manifest record")
    sidecar = PROVENANCE / f"{avatar_id}.json"
    check(sidecar.is_file(), f"{avatar_id}: missing provenance sidecar")
    if model.is_file():
        gltf = glb_json(model)
        meshes = gltf.get("meshes") or []
        skins = gltf.get("skins") or []
        animations = gltf.get("animations") or []
        check(len(meshes) == 1, f"{avatar_id}: expected exactly one mesh, found {len(meshes)}")
        check(len(skins) == 1, f"{avatar_id}: expected exactly one skin, found {len(skins)}")
        expected_clips = 8 if avatar_id == "leo" else 11
        check(len(animations) == expected_clips,
              f"{avatar_id}: expected {expected_clips} clips, found {len(animations)}")
        names = {a.get("name") for a in animations}
        check(entry.get("idleAnimation") in names, f"{avatar_id}: missing declared idle animation")
        for mesh in meshes:
            for primitive in mesh.get("primitives", []):
                attrs = primitive.get("attributes", {})
                check("POSITION" in attrs, f"{avatar_id}: mesh primitive lacks POSITION")
                check("JOINTS_0" in attrs and "WEIGHTS_0" in attrs,
                      f"{avatar_id}: mesh primitive lacks JOINTS_0/WEIGHTS_0")
        extras = ((gltf.get("asset") or {}).get("extras") or {})
        check("sophia" not in json.dumps(extras).lower() and "renderpeople" not in json.dumps(extras).lower(),
              f"{avatar_id}: GLB contains prohibited Sophia/Renderpeople provenance")
        current_hash = sha256(model)
        if record:
            recorded_hash = record.get("sha256") or record.get("glbSha256")
            check(recorded_hash == current_hash, f"{avatar_id}: manifest hash does not match bundled GLB")
            check(record.get("status") == "integrated_static_contract_validated",
                  f"{avatar_id}: manifest status is not static-contract validated")
            license_name = record.get("license") or (record.get("license") or {}).get("name")
            check("CC0" in str(license_name), f"{avatar_id}: manifest license is not CC0")
        if sidecar.is_file():
            side = json.loads(sidecar.read_text(encoding="utf-8"))
            check(side.get("sourceSha256") == current_hash,
                  f"{avatar_id}: provenance hash does not match bundled GLB")
            check(side.get("licenseStatus") == "verified" and "CC0" in str(side.get("license")),
                  f"{avatar_id}: provenance license record incomplete")

# The fallback is intentionally not part of the production cast and remains
# license-review-only, but it must remain a valid technical GLB.
xbot = ASSETS / "models/Xbot.glb"
check(xbot.is_file(), "Xbot fallback is missing")
if xbot.is_file():
    xgltf = glb_json(xbot)
    check(bool(xgltf.get("meshes")) and bool(xgltf.get("skins")) and bool(xgltf.get("animations")),
          "Xbot fallback does not satisfy the renderer contract")

# The manifest must not silently contain an active production record outside the
# ten catalog IDs; excluded assets are records of absence, not bundled files.
check(len(active_ids) == 10, "active catalog IDs are not unique")
for excluded in manifest.get("excludedAssets", []):
    check(not (ROOT / excluded["path"]).exists(), f"excluded asset unexpectedly exists: {excluded['path']}")

retired = [
    "essence" + "_" + "mystica",
    "pocion" + "_" + "vida" + "_" + "imperial",
    "muro" + "_" + "cristal",
    "cofre" + "_" + "obsidiana",
    "corona" + "_" + "eternidad",
    "crown" + "_" + "eternity",
]
for path in ROOT.rglob("*"):
    if (not path.is_file() or any(part in {".git", ".toolchains", ".gradle", "build", "outputs", "__pycache__"} for part in path.parts)
            or path.suffix.lower() in {".png", ".jpg", ".jpeg", ".webp", ".glb", ".pyc"}):
        continue
    text = path.read_text(encoding="utf-8", errors="ignore")
    for token in retired:
        check(token not in text, f"retired resource reference remains: {token} in {path}")

if errors:
    for error in errors:
        print("FAIL", error)
    raise SystemExit(1)
print(f"3d_asset_checks=OK avatars={len(entries)} glb_assets={len(all_glbs)} active_license_records={len(active_ids)}")
