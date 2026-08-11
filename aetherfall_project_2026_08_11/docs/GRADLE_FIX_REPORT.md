# Gradle build fix — Aetherfall: Empire of Kings

## Stable toolchain

- Gradle Wrapper: **9.3.1**
- Android Gradle Plugin: **9.1.1**
- Kotlin: **2.2.10**
- KSP: **2.2.10-2.0.2**
- Runtime JDK: **17**
- Java/Kotlin bytecode target: **11**
- compileSdk / targetSdk: **36**
- minSdk: **24**

AGP 9.1.1's dependency management points to KSP `2.2.10-2.0.2`. The previous KSP `2.3.5` did not match the Kotlin `2.2.10` compiler line and was replaced.

## Repository resolution

`settings.gradle.kts` keeps the required repositories:

- `google()` for AndroidX, AGP and Google/Firebase artifacts.
- `mavenCentral()` for Kotlin, Room, Retrofit, OkHttp, Moshi, Socket.IO and test artifacts.
- `gradlePluginPortal()` for Gradle plugins.

No offline mode was enabled and no dependency was removed.

## Changes made

- Corrected the KSP version in `gradle/libs.versions.toml`.
- Added an explicit Kotlin JVM target of 11 to match `compileOptions`.
- Increased the Gradle daemon heap from a sandbox-only 128 MB to a normal Android Studio/CI configuration.
- Disabled configuration cache for compatibility with the applied plugins.
- Changed the Wrapper distribution host from `services.gradle.org` to `downloads.gradle.org` because some networks cannot resolve the former; added the official SHA-256 checksum.
- Increased the wrapper network timeout to 60 seconds.
- Added `gradlew.bat` so the wrapper is complete on Windows as well as Unix-like systems.
- No UI, navigation, Room schema, server, or gameplay design was changed by the Gradle fix.

## Verification status

The wrapper starts Gradle **9.3.1** on **JDK 17**, and the documented bootstrap prepares Android SDK Platform 36/Build Tools 36.0.0. A real `test assembleDebug` was retried in one shell using the sandbox script, matching JVM options, and `--offline` after toolchain preparation. Each route reached Gradle's auxiliary daemon startup but the client could not connect before project evaluation; one higher-heap variant was also stopped by the sandbox memory limit. This is an execution-environment limitation, not a dependency-resolution or source-compilation result. No APK or test result is claimed.

Run from Android Studio or CI with internet access:

```bash
./gradlew clean assembleDebug --stacktrace --console=plain
```

Expected APK path after a successful build:

```text
app/build/outputs/apk/debug/app-debug.apk
```
