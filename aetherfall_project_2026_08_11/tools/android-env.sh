#!/usr/bin/env bash
# Shared environment for the Empire of Kings Android project.
# Usage: source tools/android-env.sh
set -euo pipefail

PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
WORKSPACE_ROOT="$(cd "$PROJECT_ROOT/.." && pwd)"

# The workspace JDK is intentionally pinned to Java 17 for Android tooling.
if [[ -x "$PROJECT_ROOT/.toolchains/jdk-17/bin/java" ]]; then
  export JAVA_HOME="$PROJECT_ROOT/.toolchains/jdk-17"
elif [[ -z "${JAVA_HOME:-}" || ! -x "$JAVA_HOME/bin/java" ]]; then
  echo "Java 17 no está disponible. Instala un JDK 17 o crea .toolchains/jdk-17." >&2
  return 1 2>/dev/null || exit 1
fi

export PATH="$JAVA_HOME/bin:$PATH"
# Keep Java/Gradle usable in the constrained sandbox and avoid class-space OOMs.
export JAVA_TOOL_OPTIONS="${JAVA_TOOL_OPTIONS:-} -Xmx512m -XX:CompressedClassSpaceSize=64m -XX:MaxMetaspaceSize=128m"
export GRADLE_OPTS="${GRADLE_OPTS:-} -Dorg.gradle.jvmargs=-Xmx256m\ -Xms64m\ -XX:CompressedClassSpaceSize=32m\ -XX:MaxMetaspaceSize=96m"

if [[ -z "${ANDROID_HOME:-}" && -d "$WORKSPACE_ROOT/.android-sdk" ]]; then
  export ANDROID_HOME="$WORKSPACE_ROOT/.android-sdk"
fi
if [[ -n "${ANDROID_HOME:-}" && -d "$ANDROID_HOME" ]]; then
  export ANDROID_SDK_ROOT="$ANDROID_HOME"
  export PATH="$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH"
fi

export EMPIRE_PROJECT_ROOT="$PROJECT_ROOT"
export EMPIRE_WORKSPACE_ROOT="$WORKSPACE_ROOT"

echo "Empire of Kings Android environment"
echo "  JAVA_HOME=$JAVA_HOME"
echo "  ANDROID_HOME=${ANDROID_HOME:-<not configured>}"
