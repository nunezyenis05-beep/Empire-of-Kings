#!/usr/bin/env python3
"""Create honest human variants from the supplied Sophia GLB base."""
from pathlib import Path
import io
import json
import struct
import sys
from PIL import Image

base = Path(sys.argv[1])
out = Path(sys.argv[2])
out.mkdir(parents=True, exist_ok=True)
data = base.read_bytes()
magic, version, total = struct.unpack_from('<4sII', data, 0)
assert magic == b'glTF' and version == 2
pos = 12
root = None
binary = None
while pos < len(data):
    size, chunk_type = struct.unpack_from('<II', data, pos)
    pos += 8
    payload = data[pos:pos + size]
    pos += size
    if chunk_type == 0x4E4F534A:
        root = json.loads(payload.decode('utf-8'))
    elif chunk_type == 0x004E4942:
        binary = payload
assert root is not None and binary is not None
source_binary = binary
image_view_index = root['images'][0]['bufferView']
image_view = root['bufferViews'][image_view_index]
image_start = image_view.get('byteOffset', 0)
image_end = image_start + image_view['byteLength']
source_image = Image.open(io.BytesIO(source_binary[image_start:image_end]))
# Ask JPEG to decode at a reduced DCT scale first so the constrained sandbox
# does not materialize the full 8192x8192 texture in memory.
source_image.draft('RGB', (2048, 2048))
# Mobile-friendly variants keep the real scanned mesh and rig but use a 2K
# diffuse map; the original 8K source remains in renderpeople_sophia.glb.
variant_image = io.BytesIO()
source_image.convert('RGB').resize((2048, 2048), Image.Resampling.LANCZOS).save(
    variant_image, format='JPEG', quality=88, optimize=True
)
variant_image_bytes = variant_image.getvalue()
image_delta = len(variant_image_bytes) - (image_end - image_start)

variants = {
    'maya': ('Maya', 'female', (0.96, 1.00, 0.98), (1.00, 0.94, 0.92, 1.0), 'rosado imperial'),
    'sofia': ('Sofia', 'female', (1.00, 1.00, 1.00), (1.00, 1.00, 1.00, 1.0), 'base Renderpeople'),
    'amara': ('Amara', 'female', (1.02, 0.99, 1.02), (0.93, 0.82, 0.72, 1.0), 'cálido místico'),
    'elena': ('Elena', 'female', (0.98, 1.03, 0.99), (0.90, 0.96, 1.00, 1.0), 'frío celestial'),
    'nadia': ('Nadia', 'female', (1.04, 1.00, 1.03), (1.00, 0.88, 0.76, 1.0), 'dorado nocturno'),
    'leo': ('Leo', 'male', (1.08, 1.03, 1.05), (0.90, 0.94, 1.00, 1.0), 'azul guardián'),
    'mateo': ('Mateo', 'male', (1.12, 1.02, 1.08), (0.92, 0.82, 0.72, 1.0), 'bronce imperial'),
    'karim': ('Karim', 'male', (1.06, 1.05, 1.06), (0.84, 0.91, 0.96, 1.0), 'cian místico'),
    'daniel': ('Daniel', 'male', (1.10, 1.01, 1.04), (0.96, 0.86, 0.78, 1.0), 'carmesí real'),
    'isaac': ('Isaac', 'male', (1.05, 1.04, 1.07), (0.86, 0.83, 0.94, 1.0), 'violeta rúnico'),
}

for ident, (label, gender, scale, color, style) in variants.items():
    r = json.loads(json.dumps(root))
    r.setdefault('asset', {})['extras'] = {
        'eokAvatarVariant': ident,
        'displayName': label,
        'genderPresentation': gender,
        'baseModel': 'renderpeople_sophia.glb',
        'variantStyle': style,
        'note': 'Real human mesh variant from the supplied Sophia base; not a geometric placeholder.'
    }
    r['nodes'][1]['scale'] = list(scale)
    mat = r.setdefault('materials', [{}])[0]
    pbr = mat.setdefault('pbrMetallicRoughness', {})
    pbr['baseColorFactor'] = list(color)
    pbr['roughnessFactor'] = 0.58 if gender == 'female' else 0.52
    pbr['metallicFactor'] = 0.0
    variant_binary = source_binary[:image_start] + variant_image_bytes + source_binary[image_end:]
    for index, view in enumerate(r['bufferViews']):
        if index == image_view_index:
            view['byteLength'] = len(variant_image_bytes)
        else:
            offset = view.get('byteOffset')
            if offset is not None and offset >= image_end:
                view['byteOffset'] = offset + image_delta
    variant_binary += b'\\0' * ((4 - len(variant_binary) % 4) % 4)
    r['buffers'][0]['byteLength'] = len(variant_binary)
    js = json.dumps(r, separators=(',', ':'), ensure_ascii=False).encode('utf-8')
    js += b' ' * ((4 - len(js) % 4) % 4)
    result = bytearray(struct.pack('<4sII', b'glTF', 2, 12 + 8 + len(js) + 8 + len(variant_binary)))
    result += struct.pack('<II', len(js), 0x4E4F534A) + js
    result += struct.pack('<II', len(variant_binary), 0x004E4942) + variant_binary
    path = out / f'{ident}.glb'
    path.write_bytes(result)
    print(ident, len(result))
