#!/usr/bin/env python3
"""Validate and stage a licensed human GLB without manufacturing variants.

This is an import gate, not a character generator.  It intentionally refuses
files derived from the bundled Sophia scan.  It checks the Android GLB
contract (mesh + POSITION + skin + animation), records the source hash and
license metadata, and only copies a source when --write is supplied.  Human
likeness, distinct identity, clothing quality and the license grant remain a
manual review gate and are recorded as pending_visual_review in the sidecar.

Example (staging only):
  python3 tools/import_human_avatar.py \
    --source /path/to/male.glb --id leo --name Leo --gender male \
    --source-url https://example.invalid/model \
    --license "CC BY 4.0" --attribution "Creator" \
    --license-status pending_verification

Add --write only after the source has been separately reviewed.  The script
never edits avatar_catalog.json; catalog/ID changes are a deliberate review
step so protocol IDs cannot be accidentally changed.
"""
from __future__ import annotations

import argparse
import hashlib
import json
import shutil
import struct
import sys
from pathlib import Path
from typing import Any

GLB_MAGIC = b"glTF"
JSON_CHUNK = 0x4E4F534A
BIN_CHUNK = 0x004E4942


def fail(message: str) -> "NoReturn":
    raise ValueError(message)


def load_glb(path: Path) -> dict[str, Any]:
    data = path.read_bytes()
    if len(data) < 20:
        fail("GLB is shorter than the minimum header and chunk size")
    magic, version, declared_length = struct.unpack_from("<4sII", data, 0)
    if magic != GLB_MAGIC or version != 2:
        fail("expected a binary glTF 2.0 (GLB)")
    if declared_length != len(data):
        fail(f"header length {declared_length} does not match file length {len(data)}")
    offset = 12
    root: dict[str, Any] | None = None
    saw_bin = False
    while offset + 8 <= len(data):
        chunk_length, chunk_type = struct.unpack_from("<II", data, offset)
        offset += 8
        end = offset + chunk_length
        if end > len(data):
            fail("GLB chunk extends beyond the file")
        payload = data[offset:end]
        offset = end
        if chunk_type == JSON_CHUNK:
            try:
                root = json.loads(payload.rstrip(b" \t\r\n\0").decode("utf-8"))
            except (UnicodeDecodeError, json.JSONDecodeError) as exc:
                fail(f"invalid JSON chunk: {exc}")
        elif chunk_type == BIN_CHUNK:
            saw_bin = True
    if root is None:
        fail("missing JSON chunk")
    if not saw_bin:
        fail("missing BIN chunk")
    return root


def validate_contract(root: dict[str, Any]) -> list[str]:
    meshes = root.get("meshes") or []
    skins = root.get("skins") or []
    animations = root.get("animations") or []
    if not meshes:
        fail("source has no mesh")
    if not skins:
        fail("source has no skin/skeleton")
    if not animations:
        fail("source has no animation clip")
    position_count = 0
    for mesh in meshes:
        for primitive in mesh.get("primitives", []):
            attrs = primitive.get("attributes", {})
            if "POSITION" in attrs:
                position_count += 1
    if position_count == 0:
        fail("source has no POSITION attribute")
    names = [a.get("name") for a in animations if a.get("name")]
    return names


def sha256(path: Path) -> str:
    digest = hashlib.sha256()
    with path.open("rb") as handle:
        for block in iter(lambda: handle.read(1024 * 1024), b""):
            digest.update(block)
    return digest.hexdigest()


def parser() -> argparse.ArgumentParser:
    p = argparse.ArgumentParser(description=__doc__)
    p.add_argument("--source", type=Path, required=True)
    p.add_argument("--id", required=True, help="stable catalog ID; must be lowercase snake_case")
    p.add_argument("--name", required=True)
    p.add_argument("--gender", choices=("male", "female"), required=True)
    p.add_argument("--source-url", required=True)
    p.add_argument("--license", dest="license_name", required=True)
    p.add_argument("--license-status", choices=("verified", "pending_verification"), required=True)
    p.add_argument("--attribution", required=True)
    p.add_argument("--output-dir", type=Path, default=Path("app/src/main/assets/models/avatars"))
    p.add_argument("--provenance-dir", type=Path, default=Path("docs/3d-assets"))
    p.add_argument("--write", action="store_true", help="copy the validated source and write its provenance sidecar")
    p.add_argument("--force", action="store_true", help="allow replacing an existing staged file")
    return p


def main(argv: list[str] | None = None) -> int:
    args = parser().parse_args(argv)
    if not args.source.is_file():
        fail(f"source file does not exist: {args.source}")
    if not args.id or args.id != args.id.lower() or any(c not in "abcdefghijklmnopqrstuvwxyz0123456789_" for c in args.id):
        fail("id must use lowercase letters, numbers and underscores only")

    root = load_glb(args.source)
    animation_names = validate_contract(root)
    source_hash = sha256(args.source)

    # Never turn a Renderpeople/Sophia scan, or a derivative carrying its
    # provenance marker, into another production identity. The source is not
    # expected to be bundled; this gate also protects future imports.
    extras = ((root.get("asset") or {}).get("extras") or {})
    provenance = json.dumps(extras).lower()
    if "renderpeople" in provenance or "renderpeople" in args.source.name.lower():
        fail("Renderpeople sources are not cleared for redistributable GLB bundling")
    if "sophia" in provenance or "sophia" in args.source.name.lower():
        fail("the Sophia source is not an independent avatar")

    target = args.output_dir / f"{args.id}.glb"
    sidecar = args.provenance_dir / f"{args.id}.json"
    if args.write and target.exists() and not args.force:
        fail(f"refusing to overwrite existing target: {target} (use --force deliberately)")

    record = {
        "schema": 1,
        "id": args.id,
        "displayName": args.name,
        "genderPresentation": args.gender,
        "sourceFile": str(args.source),
        "sourceSha256": source_hash,
        "sourceUrl": args.source_url,
        "license": args.license_name,
        "licenseStatus": args.license_status,
        "attribution": args.attribution,
        "androidContract": {
            "glbVersion": 2,
            "meshCount": len(root.get("meshes") or []),
            "skinCount": len(root.get("skins") or []),
            "animationNames": animation_names,
        },
        "status": "pending_visual_review",
        "manualGates": [
            "confirm a separately authored human identity (not a Sophia derivative)",
            "confirm male/female presentation and facial distinction in Android lighting",
            "confirm source license permits app redistribution and GLB conversion",
            "confirm rig and neutral animation play on a device",
        ],
    }
    if args.write:
        args.output_dir.mkdir(parents=True, exist_ok=True)
        args.provenance_dir.mkdir(parents=True, exist_ok=True)
        shutil.copyfile(args.source, target)
        sidecar.write_text(json.dumps(record, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
        print(f"staged={target}")
        print(f"provenance={sidecar}")
    else:
        print("dry_run=OK (validated; no files copied)")
    print(f"id={args.id} meshes={len(root.get('meshes') or [])} skins={len(root.get('skins') or [])} animations={len(root.get('animations') or [])}")
    print(f"source_sha256={source_hash}")
    print("status=pending_visual_review")
    return 0


if __name__ == "__main__":
    try:
        raise SystemExit(main())
    except (OSError, ValueError) as exc:
        print(f"IMPORT_FAIL: {exc}", file=sys.stderr)
        raise SystemExit(2)
