# Entorno Android de Empire of Kings

El proyecto incluye `setup_env_once.sh`, que prepara de una vez:

- Temurin JDK 17.
- Gradle 9.3.1 mediante Gradle Wrapper.
- Android SDK Platform 36.
- Android Build Tools 36.0.0.
- Android Platform Tools.
- Android command-line tools.

Las descargas pesadas se guardan temporalmente fuera del proyecto (`/tmp`) para no superar la cuota del workspace. El script recrea el JDK reducido en `.toolchains/jdk-17` cuando falta; esa carpeta está ignorada por Git. El wrapper queda en `gradle/wrapper/`.

## Uso en el sandbox

Como el SDK temporal vive en `/tmp` y ese directorio no se comparte entre invocaciones separadas, usa el script que prepara y compila en el mismo proceso:

```bash
bash build_in_sandbox.sh
```

También puedes ejecutar `bash setup_env_once.sh` para preparar el entorno y verificar sus componentes. En una máquina normal, configura `JAVA_HOME` y `ANDROID_HOME` de forma persistente y luego ejecuta:

```bash
./gradlew test assembleDebug --no-daemon --console=plain
```

## Estado verificado del sandbox (11 de agosto de 2026)

El JDK 17 (Temurin 17.0.20), Gradle 9.3.1, Android SDK Platform 36, Build Tools 36.0.0 y Platform Tools se prepararon correctamente en el mismo proceso de cada intento. Se probaron todas las rutas razonables disponibles sin modificar UI, navegación, servidor ni los diez avatares licenciados:

- `bash build_in_sandbox.sh` (`test assembleDebug --no-daemon`, con la sobrecarga JVM de sandbox): el daemon auxiliar inició su socket local, pero el cliente recibió `Could not connect to the Gradle daemon` antes de evaluar el proyecto.
- Invocación directa en el mismo shell con opciones JVM coincidentes y `--no-daemon --no-watch-fs`: produjo el mismo bloqueo de conexión; una variante con más heap además fue detenida por el límite de memoria del sandbox.
- Invocación `--offline` después de preparar el toolchain en el mismo shell: produjo el mismo bloqueo de conexión antes de poder resolver o compilar dependencias.

Por tanto, la memoria limitada y la política de procesos/conexiones auxiliares del sandbox impiden obtener un resultado real de `test assembleDebug`; no se produjo ningún APK ni resultado de tests, y no se debe afirmar que la compilación pasó. Debe repetirse en Android Studio, CI o una máquina con JDK 17, SDK 36 y soporte normal para procesos auxiliares. `./gradlew test assembleDebug --no-daemon --console=plain` sigue siendo el comando recomendado fuera de este sandbox.

Para runtime, Platform Tools sí está disponible temporalmente, pero no hay binario `emulator`, imagen de sistema ni AVD. `adb devices -l` no pudo mantener su daemon local (conexión rechazada) y no listó dispositivos; la puerta exacta de emulador/dispositivo queda no disponible en este entorno.
