#!/usr/bin/env python3
"""Regression tests for the non-generative human-GLB import gate."""
from __future__ import annotations

import json
import struct
import subprocess
import sys
import tempfile
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
XBOT = ROOT / "app/src/main/assets/models/Xbot.glb"
COMMON = [
    sys.executable, str(ROOT / "tools/import_human_avatar.py"),
    "--id", "test_male", "--name", "Test Male", "--gender", "male",
    "--source-url", "https://example.invalid/source",
    "--license", "CC0 (test fixture)", "--license-status", "pending_verification",
    "--attribution", "Test fixture",
]


def run(*args: str) -> subprocess.CompletedProcess[str]:
    return subprocess.run([*COMMON, *args], cwd=ROOT, text=True, capture_output=True)


def make_prohibited_fixture(source: Path, destination: Path) -> None:
    """Copy Xbot while adding a prohibited provenance marker to its JSON chunk."""
    raw = source.read_bytes()
    magic, version, _ = struct.unpack_from("<4sII", raw, 0)
    offset = 12
    chunks: list[tuple[int, bytes]] = []
    while offset + 8 <= len(raw):
        size, chunk_type = struct.unpack_from("<II", raw, offset)
        offset += 8
        chunks.append((chunk_type, raw[offset:offset + size]))
        offset += size
    rebuilt: list[bytes] = []
    for chunk_type, payload in chunks:
        if chunk_type == 0x4E4F534A:
            root = json.loads(payload.rstrip(b" \t\r\n\0").decode("utf-8"))
            root.setdefault("asset", {}).setdefault("extras", {})["variantBase"] = "renderpeople_sophia.glb"
            payload = json.dumps(root, separators=(",", ":")).encode("utf-8")
            payload += b" " * ((4 - len(payload) % 4) % 4)
        rebuilt.append(struct.pack("<II", len(payload), chunk_type) + payload)
    body = b"".join(rebuilt)
    destination.write_bytes(struct.pack("<4sII", magic, version, 12 + len(body)) + body)


def main() -> int:
    with tempfile.TemporaryDirectory(prefix="eok-import-") as temp:
        temp_root = Path(temp)
        prohibited = temp_root / "renderpeople_sophia.glb"
        make_prohibited_fixture(XBOT, prohibited)
        rejected = run("--source", str(prohibited))
        assert rejected.returncode == 2, rejected.stdout + rejected.stderr
        assert "not cleared" in rejected.stderr, rejected.stderr

        output = temp_root / "avatars"
        provenance = temp_root / "provenance"
        staged = run(
            "--source", str(XBOT), "--write",
            "--output-dir", str(output), "--provenance-dir", str(provenance),
        )
        assert staged.returncode == 0, staged.stdout + staged.stderr
        assert (output / "test_male.glb").is_file()
        sidecar = provenance / "test_male.json"
        assert sidecar.is_file()
        text = sidecar.read_text(encoding="utf-8")
        assert '"status": "pending_visual_review"' in text
        assert '"sourceSha256"' in text
    print("import_pipeline_tests=OK prohibited_source_rejected=1 staged_fixture=1")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
