#!/usr/bin/env bash
set -euo pipefail
cd "$(dirname "$0")"
JDK_ROOT="$PWD/.toolchains/jdk-17"
if [ ! -x "$JDK_ROOT/bin/java" ]; then
  mkdir -p "$PWD/.toolchains"
  jdk_url="$(curl -fsSL --max-time 30 'https://api.adoptium.net/v3/assets/latest/17/hotspot?architecture=x64&image_type=jdk&os=linux&vendor=eclipse' | python3 -c 'import json,sys; print(json.load(sys.stdin)[0]["binary"]["package"]["link"])')"
  curl -fL --retry 3 --max-time 240 "$jdk_url" -o /tmp/empire-temurin17.tar.gz
  rm -rf "$JDK_ROOT"
  mkdir -p "$JDK_ROOT"
  tar -xzf /tmp/empire-temurin17.tar.gz -C "$JDK_ROOT" --strip-components=1
  rm -f /tmp/empire-temurin17.tar.gz
  rm -f "$JDK_ROOT/lib/src.zip"
  rm -rf "$JDK_ROOT/jmods" "$JDK_ROOT/demo" "$JDK_ROOT/man" "$JDK_ROOT/legal"
fi
export JAVA_HOME="$JDK_ROOT"
export PATH="$JAVA_HOME/bin:$PATH"
export JAVA_TOOL_OPTIONS="-Xmx128m -Xss256k -XX:CompressedClassSpaceSize=16m -XX:MaxMetaspaceSize=64m -XX:+UseSerialGC -XX:ActiveProcessorCount=1"
export GRADLE_OPTS="-Xmx384m -Xss512k -XX:MaxMetaspaceSize=128m -XX:ActiveProcessorCount=2 -Dfile.encoding=UTF-8 -Dorg.gradle.native.dir=$PWD/.gradle-native"
export GRADLE_USER_HOME="$PWD/.gradle-home"
WORK=/tmp/empire-env
rm -rf "$WORK"
mkdir -p "$WORK/gradle" "$WORK/android-sdk" "$WORK/gradle-home"

if [ ! -x "$WORK/gradle/gradle-9.3.1/bin/gradle" ]; then
  curl -fL --retry 3 --max-time 180 'https://services.gradle.org/distributions/gradle-9.3.1-bin.zip' -o "$WORK/gradle.zip"
  python3 - "$WORK/gradle.zip" "$WORK/gradle" <<'PY'
import sys, zipfile
with zipfile.ZipFile(sys.argv[1]) as z:
    z.extractall(sys.argv[2])
PY
  rm -f "$WORK/gradle.zip"
  chmod -R u+rwX "$WORK/gradle/gradle-9.3.1"
  find "$WORK/gradle/gradle-9.3.1/bin" -type f -exec chmod a+rx {} +
  find "$WORK/gradle/gradle-9.3.1" -type f -name '*.so' -exec chmod a+rx {} +
  chmod +x "$WORK/gradle/gradle-9.3.1/bin/gradle"
fi

if [ ! -x "$WORK/android-sdk/cmdline-tools/latest/bin/sdkmanager" ]; then
  curl -fL --retry 3 --max-time 180 'https://dl.google.com/android/repository/commandlinetools-linux-13114758_latest.zip' -o "$WORK/cmdline-tools.zip"
  mkdir -p "$WORK/android-sdk/cmdline-tools/latest"
  python3 - "$WORK/cmdline-tools.zip" "$WORK/android-sdk/cmdline-tools/latest" <<'PY'
import sys, zipfile, os
with zipfile.ZipFile(sys.argv[1]) as z:
    for n in z.namelist():
        if n.startswith('cmdline-tools/'):
            target=n[len('cmdline-tools/'):]
            if not target: continue
            out=os.path.join(sys.argv[2], target)
            if n.endswith('/'):
                os.makedirs(out, exist_ok=True)
            else:
                os.makedirs(os.path.dirname(out), exist_ok=True)
                with z.open(n) as src, open(out,'wb') as dst: dst.write(src.read())
PY
  rm -f "$WORK/cmdline-tools.zip"
  chmod -R u+rwX "$WORK/android-sdk/cmdline-tools/latest"
  chmod +x "$WORK/android-sdk/cmdline-tools/latest/bin/sdkmanager" "$WORK/android-sdk/cmdline-tools/latest/bin/avdmanager" "$WORK/android-sdk/cmdline-tools/latest/bin/retrace"
fi

export ANDROID_SDK_ROOT="$WORK/android-sdk"
export ANDROID_HOME="$ANDROID_SDK_ROOT"

# Direct SDK archives avoid sdkmanager's high-memory metadata worker pool.
install_sdk_zip() {
  local url="$1" target="$2"
  local archive="$WORK/$(basename "$url")"
  if [ ! -d "$ANDROID_SDK_ROOT/$target" ]; then
    curl -fL --retry 3 --max-time 180 "$url" -o "$archive"
    mkdir -p "$ANDROID_SDK_ROOT/$target"
    python3 - "$archive" "$ANDROID_SDK_ROOT/$target" <<'PY'
import os, sys, zipfile
archive, dest = sys.argv[1:]
with zipfile.ZipFile(archive) as z:
    names=[n for n in z.namelist() if n and not n.endswith('/')]
    roots={n.split('/')[0] for n in names}
    strip = next(iter(roots)) + '/' if len(roots)==1 else ''
    for n in names:
        rel=n[len(strip):] if strip and n.startswith(strip) else n
        if not rel: continue
        out=os.path.join(dest, rel)
        os.makedirs(os.path.dirname(out), exist_ok=True)
        with z.open(n) as src, open(out,'wb') as dst: dst.write(src.read())
        mode=z.getinfo(n).external_attr >> 16
        os.chmod(out, mode | 0o600 if mode else 0o700 if os.path.basename(out) in {'adb','aapt2','d8','zipalign','apksigner'} else 0o600)
PY
    rm -f "$archive"
  fi
}
install_sdk_zip 'https://dl.google.com/android/repository/platform-tools_r37.0.1-linux.zip' 'platform-tools'
install_sdk_zip 'https://dl.google.com/android/repository/platform-36_r02.zip' 'platforms/android-36'
install_sdk_zip 'https://dl.google.com/android/repository/build-tools_r36_linux.zip' 'build-tools/36.0.0'

# Create the wrapper without starting a Gradle daemon in the constrained sandbox.
mkdir -p gradle/wrapper
wrapper_jar="$(find "$WORK/gradle/gradle-9.3.1/lib/plugins" -name 'gradle-wrapper-*.jar' -print -quit)"
if [ -z "$wrapper_jar" ]; then
  echo 'Gradle wrapper JAR not found in the distribution' >&2
  exit 1
fi
cp "$wrapper_jar" gradle/wrapper/gradle-wrapper.jar
shared_jar="$(find "$WORK/gradle/gradle-9.3.1/lib" -name 'gradle-wrapper-shared-*.jar' -print -quit)"
if [ -z "$shared_jar" ]; then
  echo 'Gradle wrapper shared JAR not found in the distribution' >&2
  exit 1
fi
cp "$shared_jar" gradle/wrapper/gradle-wrapper-shared.jar
for module in gradle-cli gradle-files gradle-stdlib-java-extensions; do
  module_jar="$(find "$WORK/gradle/gradle-9.3.1/lib" -name "$module-*.jar" -print -quit)"
  [ -n "$module_jar" ] && cp "$module_jar" "gradle/wrapper/$module.jar"
done
cat > gradle/wrapper/gradle-wrapper.properties <<'EOF'
distributionBase=GRADLE_USER_HOME
distributionPath=wrapper/dists
distributionUrl=https://services.gradle.org/distributions/gradle-9.3.1-bin.zip
networkTimeout=10000
validateDistributionUrl=true
zipStoreBase=GRADLE_USER_HOME
zipStorePath=wrapper/dists
EOF
cat > gradlew <<'EOF'
#!/bin/sh
APP_HOME=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAVA_EXEC="${JAVA_HOME:-}/bin/java"
[ -x "$JAVA_EXEC" ] || JAVA_EXEC=java
exec "$JAVA_EXEC" -Xmx256m -Xss256k -XX:MaxMetaspaceSize=64m -XX:CompressedClassSpaceSize=16m \
  -classpath "$APP_HOME/gradle/wrapper/gradle-wrapper.jar:$APP_HOME/gradle/wrapper/gradle-wrapper-shared.jar:$APP_HOME/gradle/wrapper/gradle-cli.jar:$APP_HOME/gradle/wrapper/gradle-files.jar:$APP_HOME/gradle/wrapper/gradle-stdlib-java-extensions.jar" \
  org.gradle.wrapper.GradleWrapperMain "$@"
EOF
# The sandbox may not allow chmod on files created by the file tool; bash gradlew remains supported.
chmod u+x gradlew 2>/dev/null || true
printf '\nENVIRONMENT_READY\n'
"$JAVA_HOME/bin/java" -version
printf 'ANDROID_SDK_ROOT=%s\n' "$ANDROID_SDK_ROOT"
printf 'SDK components: '; find "$ANDROID_SDK_ROOT" -maxdepth 2 -type d | sort | tr '\n' ' '; printf '\n'
printf 'GRADLE_WRAPPER=gradle/wrapper/gradle-wrapper.jar\n'
