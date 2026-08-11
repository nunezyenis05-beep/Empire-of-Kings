# AETHERFALL: EMPIRE OF KINGS — revisión de lanzamiento

**Fecha:** 2026-08-11
**Alcance:** proyecto Android `Empire-of-Kings` únicamente.
**Última verificación ejecutada:** 2026-08-11 16:10 CDT (sandbox).

## Resultado

La aplicación está **bien estructurada para continuar la beta**, pero **todavía no debe declararse lista para lanzamiento público**. Hay bloqueadores que no se pueden ocultar:

1. **Compilación real pendiente:** se repitió `test assembleDebug` en un mismo shell con JDK 17/SDK 36, `--no-daemon`, opciones JVM coincidentes y una variante `--offline`; Gradle 9.3.1 inició el daemon auxiliar, pero el cliente no pudo conectarse antes de evaluar el proyecto. La memoria limitada y la política de procesos/conexiones auxiliares del sandbox son el bloqueo. La compilación debe ejecutarse en Android Studio, CI o una máquina con JDK 17 y SDK Android.
2. **Prueba en dispositivo/emulador pendiente:** no se ha verificado el arranque, rotación, Room, Socket.IO, batalla, audio, rendimiento ni recuperación tras cerrar/reabrir en un Android real.
3. **Cast humano 3D premium pendiente:** Android ya contiene un renderer nativo Filament/SceneView y diez GLBs humanos Quaternius con licencia CC0 verificada (`maya`, `sofia`, `amara`, `elena`, `nadia`, `leo`, `mateo`, `karim`, `daniel` e `isaac`), además de superficies 3D para perfil, selección, barra superior, vestuario y discoteca. Los modelos son estilizados low-poly, no sustituyen todavía el acabado humano realista premium ni las prendas/armas GLB con licencia para producción; la reproducción Android, la distinción visual final y la aceptación visual siguen sin validarse en dispositivo.
4. **Firma release pendiente:** el build release solo se firma cuando el entorno inyecta `EMPIRE_RELEASE_STORE_FILE`, `EMPIRE_RELEASE_STORE_PASSWORD`, `EMPIRE_RELEASE_KEY_ALIAS` y `EMPIRE_RELEASE_KEY_PASSWORD`. No hay keystore ni secretos dentro del repositorio, correctamente.
5. **Recursos premium pendientes de integración:** las imágenes de inventario/armas generadas todavía no están dentro de `app/src/main/res`; tampoco están las tres imágenes pendientes ni los modelos GLB previstos.
6. **Online todavía es best-effort:** el health check responde, pero el servidor actual no sincroniza `ready/not_ready`; el daño, las recompensas y la batalla siguen siendo locales. No debe venderse como matchmaking/combat multijugador completo hasta validar ese contrato extremo a extremo.
7. **Minijuegos y pagos siguen siendo locales/demo:** la pantalla de juegos usa una resolución local controlada y las compras no cobran dinero real. Antes de publicar hay que decidir si se presentan explícitamente como demo/beta o integrar servicios reales y su cumplimiento correspondiente.

## Comprobaciones aprobadas

- QA 3D ejecutado tras la limpieza: 10 avatares activos, 11 GLBs totales (10 Quaternius + Xbot fallback), hashes/licencias/procedencia consistentes, contratos mesh/skin/JOINTS/WEIGHTS/animación e idle verificados; Renderpeople prohibidos ausentes.
- `tools/test_import_human_avatar.py`: import pipeline OK; fuente con procedencia Renderpeople rechazada y fixture independiente validado.
- Los intentos reales documentados de `bash build_in_sandbox.sh`, la invocación directa con JVM coincidente y la variante `--offline` prepararon JDK 17/SDK 36 en el mismo shell; en los tres casos Gradle 9.3.1 no pudo conectar con su daemon antes de evaluar el proyecto. No se produjo APK ni resultado de tests; no se declara build/APK exitosos.
- No hay binario `emulator`, imagen de sistema ni AVD disponible. Platform Tools sí se preparó temporalmente, pero `adb devices -l` no pudo mantener su daemon local y terminó con conexión rechazada, sin dispositivos; la reproducción SceneView/Filament, escala/materiales, idle y persistencia en runtime siguen sin verificar.
- 1.101 auditorías estáticas declaradas del proyecto: aprobadas (sin contar los gates 3D, importación y preflight, listados por separado).
- QA estático posterior a los nuevos intentos (11-ago-2026): `qa_100.py` 100/100, `qa_200.py` 200/200, `qa_400.py` 200/200, `qa_batch2_200.py` 201/201, `qa_batch3_200.py` 200/200, `qa_batch4_200.py` 200/200, `qa_3d_assets.py` OK (10 avatares/11 GLBs), `test_import_human_avatar.py` OK y `parallel_50_audit.py` 50/50.
- 50 tareas de preflight ejecutadas en paralelo (`tools/parallel_50_audit.py`): 50/50 aprobadas.
- Llaves, paréntesis y XML de recursos: correctos.
- Manifest con `INTERNET`, `ACCESS_NETWORK_STATE`, launcher y actividad exportada.
- Endpoint HTTPS del servidor comprobado: `/health` respondió HTTP 200 y `ok=true`.
- Room con migraciones aditivas 1→2, 2→3, 3→4 y 4→5; sin fallback destructivo para esas versiones.
- Navegación de las nueve secciones conectada al `EmpireViewModel`.
- Gradle Wrapper presente y scripts de entorno ejecutables.
- No se encontraron claves privadas, Server API Keys ni credenciales dentro del código Android.
- El servidor no fue modificado.

## Criterio de salida antes de publicar

No se creó ningún ZIP etiquetado `final`: el gate no se puede declarar aprobado mientras falten build y dispositivo/emulador.

No publicar hasta completar, en un entorno Android real:

```text
./gradlew test assembleDebug
./gradlew assembleRelease
```

Además, debe existir una APK release firmada con el keystore real, y debe completarse una prueba manual de primer arranque, autenticación, persistencia Room, economía, inventario, vestuario, batalla, recompensas, chat, clan, configuración, reconexión online y cierre de sesión.
