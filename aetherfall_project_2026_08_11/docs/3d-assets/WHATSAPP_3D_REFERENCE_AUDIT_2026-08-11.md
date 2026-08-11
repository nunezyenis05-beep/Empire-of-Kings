# Auditoría de las cuatro referencias enviadas por WhatsApp

Fecha: 2026-08-11
Proyecto: Aetherfall: Empire of Kings

## Archivos revisados

1. **Trajes & Personalización Premium — Empire of Kings**
   - 6 grupos: ángeles/diablos/místicos, fundadores, 30 pelos + 30 maquillajes, 6 trajes místicos masculinos, 10 outfits completos y ropa femenina.
2. **Mascotas Místicas — Empire of Kings**
   - 20 mascotas adultas, 20 mascotas baby y 40 mascotas de la colección extendida: 80 referencias en total.
3. **Índice de Plantillas — Empire of Kings**
   - Índice de las galerías de trajes, arsenal, armas míticas, mascotas, personalización premium, emotes e inventarios de referencia.
4. **Archivo ZIP de plantillas**
   - 9 HTML: armas míticas, arsenal de referencia, emotes, inventario de referencia, mascotas, rareza, trajes, personalización premium e índice.

## Contenido identificado

- **Armas míticas:** 29 piezas visuales, entre ellas Cañón Castillo Carmesí, Rifle Cuervo Azul, Revólver Ojo de Dragón, pistolas dracónicas, rifles de dragón/cuervo/águila, SMG, escopetas, lanzas, espadas, tridentes, cetros y mazas.
- **Arsenal legendario:** 6 familias de referencia con escudos angelicales, ballestas angelicales, espadas demoníacas, blasones de dragón, hachas encadenadas y lanzas angelicales.
- **Trajes y accesorios:** 9 referencias de la colección Conejita Rosa y variantes satín, gótica, casual, deportiva, lolita, roja, negro-dorado y azul-dorado.
- **Personalización premium:** 6 grupos, incluyendo trajes místicos masculinos: Hechicero Lunar, Guardián del Bosque, Soberano Abisal, Dragón Celestial, Sacerdote Estelar y Emperador del Rayo.
- **Emotes:** 9 grupos o pantallas de referencia.
- **Mascotas:** 20 adultas, 20 baby y 40 extendidas.
- **Hojas de inventario:** referencias separadas para ropa femenina, ropa masculina, accesorios y plantillas faltantes.

## Hallazgo técnico importante

Los cuatro archivos son **referencias visuales HTML**. El ZIP contiene HTML con imágenes incrustadas en base64; no contiene modelos GLB, texturas PBR separadas, rigs, skin weights ni archivos de licencia para redistribución Android.

Por lo tanto:

- Se usarán como **brief visual y catálogo de diseño**.
- No se copiarán imágenes como si fueran modelos 3D.
- Ninguna pieza se marcará `model_ready` sin un GLB real, materiales/texturas, rig/skin compatible, licencia y prueba Android.
- Las piezas que aún no tengan GLB permanecerán `model_pending`.
- Las referencias no autorizan por sí solas la redistribución de los diseños o imágenes.

## Plan de integración

1. Mantener los 10 avatares humanos CC0 ya validados como base.
2. Mapear cada referencia a una entrada estable del catálogo.
3. Buscar o producir una fuente 3D independiente y redistribuible para cada pieza.
4. Convertir a GLB, comprobar materiales, escala, rig/skin y compatibilidad con SceneView.
5. Registrar ruta, hash y licencia en el manifiesto 3D.
6. Probar carga con avatar masculino o femenino, persistencia, Room, catálogo y Android.
7. Solo después cambiar `model_pending` a `model_ready_license_review`.

## Estado actual

- Bloque 001: 100 entradas masculinas registradas de forma honesta como `model_pending`.
- GLB de ropa, armas, mascotas o emotes procedente de estos cuatro archivos: **0**.
- La APK debug del proyecto base ya compila; la validación de los nuevos catálogos continúa separada de la validación visual en dispositivo.
