#!/usr/bin/env python3
"""Three-gate static validation for inventory work batches.

A pending entry is intentionally allowed to lack a GLB. This gate verifies that
references and catalog records are consistent without pretending that a 2D
reference is a finished 3D asset.
"""
from pathlib import Path
import hashlib
import json
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
CATALOG = ROOT / "app/src/main/assets/models/equipment_catalog.json"
KOTLIN = ROOT / "app/src/main/java/com/aistudio/empireofkings/game/data/EquipmentCatalog.kt"
REFS = ROOT.parent / "whatsapp_3d_refs/reference_manifest.json"
BATCH_LIMIT = 200
errors = []

def fail(msg):
    errors.append(msg)

catalog = json.loads(CATALOG.read_text(encoding="utf-8"))
entries = catalog.get("entries", [])
ids = [e.get("id") for e in entries]
if len(entries) > BATCH_LIMIT:
    fail(f"catalog batch exceeds {BATCH_LIMIT}: {len(entries)}")
if len(ids) != len(set(ids)):
    fail("duplicate equipment IDs")
for e in entries:
    if not e.get("id") or not e.get("slot") or not e.get("name"):
        fail(f"entry missing identity fields: {e!r}")
    if e.get("status") == "model_pending" and e.get("model") is not None:
        fail(f"pending entry has a model path: {e['id']}")

kotlin = KOTLIN.read_text(encoding="utf-8")
kotlin_ids = re.findall(r'EquipmentDefinition\("([^\"]+)"', kotlin)
if len(kotlin_ids) != len(set(kotlin_ids)):
    fail("duplicate IDs in Kotlin catalog")
if set(kotlin_ids) != set(ids):
    fail(f"Kotlin/JSON ID mismatch: json={len(ids)} kotlin={len(kotlin_ids)}")

ref = json.loads(REFS.read_text(encoding="utf-8"))
images = ref.get("images", [])
if ref.get("imageCount") != len(images):
    fail("reference imageCount does not match image records")
seen_hashes_by_source = set()
for item in images:
    path = ROOT.parent / item["path"]
    if not path.is_file():
        fail(f"missing reference file: {item['path']}")
        continue
    digest = hashlib.sha256(path.read_bytes()).hexdigest()
    if digest != item.get("sha256"):
        fail(f"reference hash mismatch: {item['path']}")
    # The same gallery image may be intentionally reused by the standalone
    # HTML and the ZIP index. Only reject duplicate content inside one source.
    source_key = (item.get("source"), digest)
    if source_key in seen_hashes_by_source:
        fail(f"duplicate reference content in source: {item['path']}")
    seen_hashes_by_source.add(source_key)

# Required first weapon slice: every WhatsApp mythical weapon is represented.
weapon_names = [e["name"] for e in entries if e.get("collection") == "WHATSAPP_ARMAS_MITICAS"]
if len(weapon_names) != 29:
    fail(f"expected 29 mythical WhatsApp weapons, found {len(weapon_names)}")
if any(e.get("status") != "model_pending" for e in entries if e.get("collection") == "WHATSAPP_ARMAS_MITICAS"):
    fail("a reference weapon was marked ready without a GLB contract")

if errors:
    for error in errors:
        print("FAIL", error)
    sys.exit(1)
print(f"inventory_batch_checks=OK batch_limit={BATCH_LIMIT} catalog_entries={len(entries)} reference_images={len(images)} weapon_refs={len(weapon_names)}")
