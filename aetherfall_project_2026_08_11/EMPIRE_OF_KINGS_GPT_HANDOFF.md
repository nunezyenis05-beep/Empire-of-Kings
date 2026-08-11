# Empire of Kings — actualización para continuar el desarrollo

**Fecha de actualización:** 2026-08-10 16:17 CDT  
**Proyecto:** Android existente `Empire-of-Kings`  
**Repositorio Android:** https://github.com/nunezyenis05-beep/Empire-of-Kings  
**Repositorio servidor:** `Empire-of-Kings-Server` — **NO TOCAR**  
**Workspace local:** `/app/state/f74974e5-3493-4418-9098-4be0a5ff51eb/work/Empire-of-Kings`

## Instrucciones obligatorias

- Continuar únicamente el proyecto Android `Empire-of-Kings`.
- No crear un proyecto nuevo.
- No modificar `Empire-of-Kings-Server`.
- No crear nuevas API Keys de LootLocker.
- No incluir Server API Keys, secretos ni credenciales en Android.
- No publicar en Google Play.
- No afirmar que los tests o la compilación pasaron sin ejecutarlos realmente.
- Mantener la interfaz mística/fantástica original.
- Los avatares deben seguir una dirección humana 3D realista/premium; cada recuadro de selección debe mostrar un solo avatar, nunca dos juntos.

## Identidad técnica actual

- Namespace: `com.aistudio.empireofkings.game`
- Application ID: `com.aistudio.empireofkings.game`
- Kotlin packages alineados con el namespace.
- Arquitectura principal: Jetpack Compose + ViewModel + Room.
- Persistencia local en `EmpireDatabase`.
- Flujo actual: Splash → autenticación local → lobby.
- Base de datos Room versión 3: migraciones aditivas 1→2 y 2→3; `fallbackToDestructiveMigration()` solo queda como último recurso para esquemas desconocidos.
- El proyecto incluye `gradlew` y los auxiliares del Gradle Wrapper; su ejecución queda sujeta a un daemon funcional en el entorno.

## Cambios implementados

### Entrada y sesión

- Splash funcional.
- Autenticación local con validación de:
  - usuario durante el registro;
  - correo/teléfono;
  - contraseña de mínimo 6 caracteres;
  - aceptación de términos.
- Sesión local persistida con `SharedPreferences`.
- Los jugadores que regresan saltan autenticación y llegan al lobby.
- Se agregó cierre de sesión local desde Configuración.
- La persistencia del nombre espera brevemente a que Room termine la siembra inicial para evitar que vuelva a `KING_PLAYER`.

### Room y progreso

- La versión 3 añade `wardrobe_items` y campos de perfil (`avatarPreset`, `profileBio`, `presenceStatus`) mediante migración no destructiva.
- Vestuario se siembra por separado cuando la tabla está vacía, incluidos upgrades desde la versión 2; equipar se limita al slot del cosmético y se persiste en Room.
- Perfil permite editar nombre, bio, estado y preset de avatar con validación y feedback persistente.
- Los datos iniciales solo se insertan cuando no existe el jugador local.
- El progreso no se sobrescribe al reiniciar la aplicación.
- Mejoras de armas basadas en las filas más recientes de Room.
- Mejora de arma guarda de forma transaccional:
  - usuario;
  - arma;
  - energía de mejora del saldo del usuario (sin duplicarla en el inventario).
- Equipar un arma inexistente no modifica accidentalmente el equipamiento.
- Compras, recompensas de minijuegos y recompensas de batalla usan snapshots actuales de Room para evitar sobrescrituras por estado obsoleto de Compose.

### Ruleta y tienda

- La Ruleta Premium cuesta 25 diamantes.
- Valida el saldo antes de girar.
- Persiste una de tres recompensas:
  - +100 energía de mejora;
  - 10.000 de oro;
  - Fragmentos de Arma Mística x10.
- La ruleta queda bloqueada mientras espera el resultado para evitar dobles giros.
- El catálogo de armas navega correctamente al Armario.
- Los packs de tienda pasan el nombre y precio correctos al modal.
- Las recargas usan cantidades dependientes del precio.

### Pagos demo

- El modal de pagos es solamente local/demo.
- Se eliminó la URL falsa de WhatsApp.
- La opción WhatsApp informa que queda pendiente de un backend real y no entrega monedas.
- Las tarjetas demo requieren exactamente 16 dígitos.
- No hay procesamiento real de pagos.
- El panel administrativo fue marcado como modo local y ya no afirma que el servidor esté operativo.
- Se eliminaron requisitos de keystore privado para que Debug no dependa de claves ausentes ni secretos del servidor.

### Batalla local

- La simulación usa 5 combatientes totales: jugador + 4 bots.
- La victoria es alcanzable.
- El jugador tiene límites de arena.
- La zona segura se reduce y causa daño fuera de ella.
- El centro renderizado de la zona segura coincide con el centro usado por la simulación.
- Las recompensas y estadísticas se registran una sola vez por partida.
- La pantalla de derrota ya no muestra las recompensas de victoria.

### Social y minijuegos

- Invitaciones duplicadas al squad bloqueadas.
- Squad limitado a sus espacios disponibles.
- Chat limitado a 200 caracteres y con `trim()`.
- Canales válidos: `GLOBAL`, `CLAN`, `SQUAD`.
- Minijuegos ahora actualizan el saldo persistente:
  - Ajedrez: +5.000 oro.
  - Cartas Rúnicas: +20 diamantes.
  - Dominó: +3.500 oro.

## Archivos principales modificados recientemente

- `app/build.gradle.kts`
- `app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/ShopScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/GamesScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/SettingsAndAdminScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/WardrobeScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/ProfileScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/InventoryScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/data/WardrobeItem.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/screens/DiscoScreen.kt`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/components/BottomNavBar.kt`
- `tools/qa_batch2_200.py`
- `app/src/main/java/com/aistudio/empireofkings/game/ui/components/PaymentModal.kt`
- `CHANGELOG.md`

## Verificaciones realizadas

- Llaves balanceadas en los 34 archivos Kotlin de producción; el entorno actual no incluye Java/Android runtime para afirmar parseo o compilación del compilador.
- Llaves y paréntesis balanceados.
- XML de recursos Android válido.
- Se corrigió una coma faltante en la lista inicial de `InventoryItem`.
- No quedan referencias a `com.example`, `MyApplication` ni `Greeting`.
- No quedan URLs placeholder de WhatsApp.
- No hay cambios en el código del servidor.
- Servidor desplegado y verificado: `https://empire-of-kings-server.onrender.com` (`/health` respondió HTTP 200 con `ok=true`).
- Android ahora incluye un puente de comprobación de salud hacia ese servidor y muestra el estado en Configuración/Admin; Room sigue siendo la fuente offline-first.
- El flujo de autenticación hace un intento best-effort de registro/login remoto y guarda el JWT recibido, pero conserva el acceso local si el servidor gratuito está dormido o no disponible.
- Se añadió cliente Socket.IO para registrar jugadores, emitir movimiento/acciones, entrar en matchmaking y mostrar marcadores de jugadores remotos; la batalla local de cinco participantes permanece como fallback.
- Las acciones `attack` remotas muestran un pulso visual breve. El daño, recompensas y contrato de combate siguen locales porque no se modificó el servidor.
- El HUD de batalla muestra el estado Socket.IO y el número de jugadores encontrados; los marcadores remotos se limpian al desconectar.
- Se añadió un aviso no bloqueante de matchmaking con tiempo transcurrido; la batalla local continúa mientras se busca otro jugador.
- El aviso de partida encontrada muestra los IDs de los jugadores conectados antes de la sincronización avanzada del combate.
- La sala online muestra hasta seis jugadores con una vista previa de avatar humano, marcador individual y estado `CONECTADO`; el jugador local muestra su vestuario y arma equipada.
- Los equipamientos remotos se intercambian por el evento existente `playerLoadout`; el cliente envía los cuatro campos normalizados, valida las allowlists y muestra el equipamiento recibido en la sala.
- La sala tiene botón `ESTOY LISTO`/`CANCELAR LISTO` y contador `LISTOS: X/Y`; el estado del jugador local es real. El servidor actual rechaza `ready`/`not_ready` en `playerAction`, por lo que no se envían acciones incompatibles y la sincronización remota de listo sigue pendiente.
- La cuenta atrás visual de tres segundos solo se activa con más de un jugador y cuando todos los estados conocidos están listos; el botón se bloquea durante la cuenta atrás.
- El cliente envía al conectarse el loadout normalizado (`outfit`, `weapon`, `armor`, `accessory`) por el evento soportado `playerLoadout`, valida las respuestas remotas y muestra el equipamiento remoto en la sala.
- Al salir de batalla se desconecta Socket.IO y se limpian jugadores, estados de listo y acciones antiguas.
- Los nombres de acciones online están centralizados para evitar errores de texto entre ataques y estados de listo.
- Se añadió detección de conexión Socket.IO y reconexión automática al volver a la batalla.
- Se evitaron solicitudes duplicadas de matchmaking y se sanitizaron las acciones salientes.
- Las salas se limitan a seis jugadores sin duplicados; un `matchFound` vacío mantiene el estado de búsqueda.
- El roster incluye siempre al jugador local, limpia datos remotos antiguos en partidas nuevas y explica el arranque en frío de Render después de ocho segundos.
- Se validan coordenadas y vida recibidas, y se acotan los movimientos enviados al servidor.
- Los snapshots remotos dejan de dibujarse tras diez segundos sin actualización; el roster muestra `SIN SEÑAL`.
- Tras una espera larga aparecen `REINTENTAR` y `JUGAR LOCAL`, además del estado de salud del servidor en el HUD.
- Reintentar/cancelar limpia la sala y al volver se reconecta usando el avatar guardado del usuario.
- Socket.IO tiene reintentos y tiempos acotados, conexión fresca, timeout y debounce ligero de acciones.
- El HUD muestra señales remotas y cada jugador remoto dibuja una barra de vida validada.
- `/health` fue comprobado de nuevo: HTTP 200 y `ok: true`.
- Se añadió `setup_env_once.sh` para preparar JDK 17, Gradle 9.3.1, Android SDK Platform 36, Build Tools 36.0.0, Platform Tools y command-line tools.
- El Gradle Wrapper quedó instalado: `gradlew`, `gradle-wrapper.jar` y los módulos auxiliares están en `gradle/wrapper/`.
- `ENVIRONMENT.md` y `build_in_sandbox.sh` documentan cómo preparar y compilar en el mismo proceso cuando `/tmp` no persiste entre comandos.
- El JDK y las cachés pesadas se mantienen ignorados para proteger la cuota del workspace.
- Se intentó `test assembleDebug` con el entorno completo; el proceso auxiliar de Gradle no pudo mantenerse activo en este sandbox, por lo que no se declara una compilación exitosa.
- `tools/qa_200.py` ejecuta el lote completo de 200 comprobaciones (100 base y 100 profundas); las 200 pasan.
- `tools/qa_400.py` ejecuta exactamente 200 comprobaciones semánticas adicionales sobre el contrato Socket.IO, loadouts, ciclo de vida y documentación; las 200 pasan.
- `tools/qa_batch2_200.py` ejecuta exactamente 200 comprobaciones nuevas sobre Room 3, Vestuario, Perfil, navegación fija y contrato visual; las 200 pasan.

## Pendiente crítico

Todavía falta ejecutar en un entorno Android real:

```text
./gradlew test
./gradlew assembleDebug
```

El wrapper está presente y el script puede preparar temporalmente JDK/SDK, pero el sandbox no conserva esos binarios entre invocaciones y el proceso auxiliar de Gradle no se mantuvo activo. No se debe afirmar que la compilación o los tests pasaron.

## Próximo trabajo recomendado

1. Abrir el proyecto en Android Studio con JDK compatible y Android SDK instalado.
2. Verificar el Gradle Wrapper y ejecutar la compilación en un entorno Android con daemon funcional.
3. Ejecutar `test` y `assembleDebug`.
4. Corregir errores reales de compilación que aparezcan.
5. Probar manualmente:
   - primer arranque;
   - registro/login;
   - cierre y reapertura de sesión;
   - compra demo;
   - ruleta;
   - mejora y equipamiento de armas;
   - minijuegos;
   - batalla y recompensa única;
   - chat, amigos y squad.
6. Preparar un commit limpio solo cuando la compilación y los tests hayan sido comprobados.

## Prompt recomendado para continuar

> Continúa el desarrollo real de la aplicación Android existente `Empire-of-Kings` usando este contexto. Trabaja únicamente en el repositorio Android, no toques `Empire-of-Kings-Server`, no crees API Keys ni pongas secretos en Android. Primero inspecciona los archivos actuales y verifica las modificaciones descritas. Después ejecuta tests y `assembleDebug` solo si existe un entorno Android/Gradle funcional. Corrige errores reales de compilación, persistencia, Room, Compose, navegación o jugabilidad. No declares éxito sin ejecutar la verificación. Mantén la estética mística/fantástica y la dirección de avatares humanos 3D realistas.
