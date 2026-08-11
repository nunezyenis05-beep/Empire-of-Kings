#!/usr/bin/env bash
# Prepares the temporary Android toolchain and builds in the same process.
# This is intended for the constrained Zapia sandbox, where /tmp is not
# shared across separate shell invocations.
set -euo pipefail
cd "$(dirname "$0")"

source setup_env_once.sh
# Keep Gradle's large dependency cache outside the project quota.
export GRADLE_USER_HOME="$WORK/gradle-home"
export GRADLE_OPTS='-Xmx96m -Xss256k -XX:MaxMetaspaceSize=48m -XX:ActiveProcessorCount=1 -Dfile.encoding=UTF-8'

"$WORK/gradle/gradle-9.3.1/bin/gradle" test assembleDebug \
  --no-daemon --console=plain \
  -Dorg.gradle.daemon=false \
  -Dorg.gradle.jvmargs=''
