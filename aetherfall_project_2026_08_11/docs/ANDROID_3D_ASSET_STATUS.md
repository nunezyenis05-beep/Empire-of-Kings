# Estado de assets 3D Android

Fecha de revisión: 2026-08-11

## Elenco humano integrado

Los diez slots activos del catálogo ahora usan GLB humanos independientemente authored por Quaternius, con licencia CC0 1.0 Universal y contrato de mesh, skin y animación verificado:

- Maya, Amara, Elena y Nadia — `Ultimate Animated Character Pack`, fuentes femeninas independientes.
- Sofia — `Ultimate Animated Character Pack / Casual_Female.fbx`, fuente femenina independiente.
- Leo — `Animated Human Low Poly`, fuente masculina independiente.
- Mateo, Karim, Daniel e Isaac — `Ultimate Animated Character Pack`, fuentes masculinas independientes.

La integración no cambia la navegación, la UI, Room, ViewModels, la economía, el protocolo online ni el servidor. Los IDs históricos continúan funcionando:

- `king_warrior` → `models/avatars/leo.glb`
- `royal_guard` → `models/avatars/mateo.glb`
- `arcane_queen` → `models/avatars/sofia.glb`

## Fuentes y contratos

### Leo

- Archivo: `app/src/main/assets/models/avatars/leo.glb`.
- Fuente: Quaternius, **Animated Human Low Poly**.
- Licencia: CC0 1.0 Universal.
- SHA-256 del GLB: `1b8c269da25169f1688066d3e1b3d71844a250032d20b2089bb6489fb54dff48`.
- Contrato: 1 mesh, 1 skin y 8 clips, incluido `Human Armature|Idle`.
- Conversión: FBX2glTF 0.9.7.

### Sofia

- Archivo: `app/src/main/assets/models/avatars/sofia.glb`.
- Fuente: Quaternius, **Ultimate Animated Character Pack / Casual_Female.fbx**.
- Licencia: CC0 1.0 Universal.
- SHA-256 del GLB: `5120ce57e1eb9db39448717a8c485f70c7d6503b00055512c1909f3bb47894e4`.
- Contrato: 1 mesh, 1 skin, 23 joints y 11 clips, incluido `CharacterArmature|Idle`.
- Conversión: FBX2glTF 0.9.7.

### Ocho slots adicionales

Maya, Amara, Elena, Nadia, Mateo, Karim, Daniel e Isaac usan archivos independientes del mismo paquete CC0 de Quaternius:

- `Casual2_Female.fbx` → Maya.
- `Casual3_Female.fbx` → Amara.
- `Doctor_Female_Young.fbx` → Elena.
- `OldClassy_Female.fbx` → Nadia.
- `Casual2_Male.fbx` → Mateo.
- `Casual3_Male.fbx` → Karim.
- `Doctor_Male_Young.fbx` → Daniel.
- `OldClassy_Male.fbx` → Isaac.

Cada GLB integrado tiene 1 mesh, 1 skin, skinning JOINTS/WEIGHTS, 23 joints y 11 animaciones, incluida `CharacterArmature|Idle`. La procedencia, los hashes y la licencia están registrados en `docs/3d-assets/<id>.json` y en `docs/3d-assets/3D_ASSET_LICENSE_MANIFEST.json`.

## Estado de licencia

La fuente Quaternius se registra como CC0 1.0 Universal: permite modificación, redistribución y uso comercial. El paquete de Renderpeople no se integró como fuente redistribuible porque sus términos actuales permiten renderizado en tiempo real en juegos, pero restringen transferir, divulgar o hacer fácilmente extraíbles los datos 3D individuales sin consentimiento escrito.

## Revisión del bloqueo premium-realista (2026-08-11)

Se revisaron fuentes descargables con licencia explícita y se inspeccionaron las condiciones de archivo, licencia, malla, skin, animación y distribución Android. El resultado está en [`docs/3d-assets/PREMIUM_REALISTIC_SOURCE_RESEARCH.md`](3d-assets/PREMIUM_REALISTIC_SOURCE_RESEARCH.md): **no hay una fuente que pase simultáneamente todos los gates**.

- MakeHuman/MPFB tiene assets core CC0 y permite personajes generados en juegos cerrados, pero su entrega pública es un pipeline de generación/exportación (FBX/BVH), no un GLB humano ya descargado y validado con el contrato de clips Android requerido.
- Mixamo permite videojuegos comerciales, pero su FAQ prohíbe distribuir los archivos raw de personajes/animaciones; no satisface el requisito de datos GLB dentro del APK.
- Reallusion anuncia bases riggeadas para uso comercial, pero el acceso requiere cuenta y la página enlaza un EULA sin la autorización explícita de redistribuir los raw assets en un APK cerrado; no se descargó ni se usó un bypass.
- Blender Studio anuncia personajes riggeados, pero la página pública inspeccionada no expone una licencia por archivo ni un GLB humano realista con contrato Android verificable.

No se añadieron fuentes candidatas, no se modificaron los diez slots y no se etiquetó ninguna solución ficticia como premium. El bloqueo de calidad visual **sigue abierto**.

`renderpeople_sophia.glb` y `rp_posed_00178_29.glb` fueron eliminados de `app/src/main/assets` y no se distribuyen; el segundo era una malla estática sin skin ni animación. `Xbot.glb` continúa únicamente como fallback técnico del renderer.

## Arquitectura

- `AvatarCatalog` es la única fuente de IDs, estado, ruta Android y animación.
- `HumanAvatar3D` resuelve el GLB desde el catálogo y reproduce la animación neutral indicada.
- `avatar_catalog.json` conserva el inventario de diez avatares y los alias históricos.
- `tools/import_human_avatar.py` valida y registra fuentes independientes sin fabricar variantes.
- `tools/qa_3d_assets.py` valida GLB, mesh, skin, animaciones, JOINTS/WEIGHTS, rutas, catálogo y ausencia de recursos retirados.
- `EquipmentCatalog` y `equipment_catalog.json` registran piezas de vestuario y equipo; esas piezas todavía no se presentan como GLB separados si no existe un archivo licenciado.

## QA confirmado

- `qa_3d_assets.py` valida 10 avatares licenciados, el fallback Xbot y la ausencia de assets Renderpeople prohibidos.
- `test_import_human_avatar.py`: `import_pipeline_tests=OK shared_source_rejected=1 staged_fixture=1`.
- Las herramientas Python compilan sintácticamente.

## Pendientes antes de declarar lanzamiento

- Ejecutar las diez animaciones neutrales en SceneView/Filament sobre emulador o dispositivo Android.
- Revisar escala, materiales, iluminación, esqueleto y reproducción real.
- Confirmar que la presentación final de cinco hombres y cinco mujeres sea visualmente suficientemente diferenciada.
- Aceptación de calidad premium: las fuentes Quaternius son estilizadas low-poly y no equivalen todavía al acabado realista premium solicitado.
- Compilación Gradle real y validación del APK/debug.
- Verificar persistencia de IDs y envío online sin cambiar el servidor.
- Generar el ZIP final solo después de completar estas gates.

Estado global: `NOT_COMPLETE_device_and_premium_visual_validation_pending`.
