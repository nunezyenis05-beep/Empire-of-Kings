#!/usr/bin/env bash
# Diagnostic only: never installs packages or changes the project.
set -u
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
# shellcheck source=android-env.sh
source "$SCRIPT_DIR/android-env.sh" >/dev/null

ok=1
if java -version >/dev/null 2>&1; then
  echo "[OK] Java: $(java -version 2>&1 | grep -m1 'version')"
else
  echo "[FAIL] Java no puede iniciarse"; ok=0
fi

if command -v gradle >/dev/null 2>&1; then
  echo "[OK] Gradle del sistema: $(gradle --version 2>/dev/null | sed -n '3p')"
elif [[ -x "$EMPIRE_WORKSPACE_ROOT/.toolchains/gradle-9.1.0/bin/gradle" ]]; then
  echo "[OK] Gradle local: 9.1.0"
else
  echo "[WARN] Gradle no está instalado; añade el wrapper o instala Gradle 9.1.0."
fi

if [[ -n "${ANDROID_HOME:-}" && -x "$ANDROID_HOME/platform-tools/adb" ]]; then
  echo "[OK] Android SDK: $ANDROID_HOME"
else
  echo "[WARN] Android SDK no está configurado; no se puede ejecutar assembleDebug aquí."
fi

if [[ -f "$EMPIRE_PROJECT_ROOT/gradlew" ]]; then
  echo "[OK] Gradle Wrapper presente"
else
  echo "[WARN] Falta Gradle Wrapper (gradlew); la compilación reproducible requiere añadirlo."
fi

exit $((1-ok))
