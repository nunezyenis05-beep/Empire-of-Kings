from pathlib import Path

root = Path(__file__).resolve().parents[1]
checks = []
def check(name, ok): checks.append((name, bool(ok)))
def text(rel): return (root / rel).read_text(errors="ignore")
def exists(rel): return (root / rel).is_file()
def has(rel, needle): return needle in text(rel)

required = [
    "build.gradle.kts", "settings.gradle.kts", "gradle.properties", "gradle/libs.versions.toml",
    "app/build.gradle.kts", "app/src/main/AndroidManifest.xml",
    "app/src/main/java/com/aistudio/empireofkings/game/MainActivity.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireRemoteClient.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/data/EmpireDatabase.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt",
    "app/src/main/java/com/aistudio/empireofkings/game/data/EmpireDao.kt",
    "app/src/main/res/values/strings.xml", "app/src/main/res/values/colors.xml",
    "app/src/main/res/values/themes.xml", "app/src/test/java/com/aistudio/empireofkings/game/ExampleUnitTest.kt",
    "app/src/test/java/com/aistudio/empireofkings/game/EmpireScreenshotTest.kt",
    "app/src/test/java/com/aistudio/empireofkings/game/ExampleRobolectricTest.kt",
    "app/src/androidTest/java/com/aistudio/empireofkings/game/ExampleInstrumentedTest.kt",
    "CHANGELOG.md", "EMPIRE_OF_KINGS_GPT_HANDOFF.md", "tools/android-env.sh", "tools/check-android-env.sh",
    "app/src/main/res/drawable/img_king_warrior_1786249144739.jpg",
    "app/src/main/res/drawable/img_empire_lobby_bg_1786249131349.jpg",
    "app/src/main/res/drawable/img_app_icon_1786249120706.jpg", "app/src/main/res/xml/backup_rules.xml",
]
for p in required: check("file:" + p, exists(p))

content_checks = [
    ("app/build.gradle.kts", 'namespace = "com.aistudio.empireofkings.game"'),
    ("app/build.gradle.kts", "minSdk = 24"), ("app/build.gradle.kts", "targetSdk = 36"),
    ("app/build.gradle.kts", "socket.io.client"),
    ("settings.gradle.kts", 'include(":app")'), ("gradle/libs.versions.toml", 'agp = "9.1.1"'),
    ("gradle/libs.versions.toml", 'kotlin = "2.2.10"'), ("EMPIRE_OF_KINGS_GPT_HANDOFF.md", "JDK 17"),
    ("tools/android-env.sh", "JAVA_HOME"), ("tools/android-env.sh", "JAVA_TOOL_OPTIONS"),
    ("tools/check-android-env.sh", "Android SDK"), ("tools/check-android-env.sh", "Gradle Wrapper"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "OnlineSessionStatus"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "connectOnlinePlayer"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "KING_PLAYER"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "sendOnlineReady"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "remotePlayers"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt", "localPlayerId"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt", "BattleRoyaleScreen"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "while (true)"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "BUSCANDO RIVAL ONLINE"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "ESTOY LISTO"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "CANCELAR LISTO"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "JUGAR LOCAL"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "SIN SEÑAL"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "remote.health"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "launchCountdown"),
    ("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "onOnlineAction"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "OnlineActionNames"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "reconnectionAttempts"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "timeout = 10000L"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "joinMatchmaking"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "coerceIn(0, 100)"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "size < 6"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "forceNew = true"),
    ("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "socket?.off()"),
]
for rel, needle in content_checks: check("content:" + rel + ":" + needle, has(rel, needle))

sources = list((root / "app/src/main").rglob("*.kt"))
all_source = "\n".join(p.read_text(errors="ignore") for p in sources)
for forbidden in ["Server API Key", "sk_live_", "AIza", "BEGIN PRIVATE KEY", 'password = "password"']:
    check("safety:no-" + forbidden, forbidden not in all_source)
checks.extend([
    ("safety:server-repo-not-under-android", not (root / "Empire-of-Kings-Server").exists()),
    ("consistency:application-id-test", "com.aistudio.empireofkings.game" in text("app/src/androidTest/java/com/aistudio/empireofkings/game/ExampleInstrumentedTest.kt")),
    ("consistency:health-url", "empire-of-kings-server.onrender.com" in text("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireRemoteClient.kt")),
    ("consistency:room-seed", "initializeSeedDataIfNeeded" in text("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt")),
    ("consistency:offline-fallback", "JUGAR LOCAL" in text("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt")),
    ("consistency:single-avatar", "un solo avatar" in text("EMPIRE_OF_KINGS_GPT_HANDOFF.md").lower()),
    ("consistency:remote-health-range", "health: Int" in text("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt")),
    ("consistency:remote-stale-window", "10_000L" in text("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt")),
    ("consistency:action-length", "take(40)" in text("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt")),
    ("consistency:coordinate-range", "coerceIn(0f, 1000f)" in text("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt")),
    ("consistency:retry-cleanup", "_remoteActions.value = emptyMap()" in text("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt")),
    ("consistency:countdown-lock", "enabled = launchCountdown == 0" in text("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt")),
    ("consistency:health-bar", "remote.health / 100f" in text("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt")),
    ("consistency:render-ok-doc", "HTTP 200" in text("CHANGELOG.md")),
    ("consistency:java-doc", "JDK 17" in text("EMPIRE_OF_KINGS_GPT_HANDOFF.md")),
    ("consistency:no-whatsapp-payment-url", "WhatsApp payment URL" in text("CHANGELOG.md")),
    ("consistency:single-local-id", "localPlayerId = localPlayerId" in text("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt")),
])

for p in [
    root / "app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt",
    root / "app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt",
    root / "app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt",
    root / "app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt",
]:
    s = p.read_text(errors="ignore")
    check("syntax:balanced-braces:" + p.name, s.count("{") == s.count("}"))
    check("syntax:balanced-parens:" + p.name, s.count("(") == s.count(")"))
    check("syntax:package:" + p.name, s.startswith("package com.aistudio.empireofkings.game"))

print(f"checks={len(checks)} passed={sum(ok for _, ok in checks)} failed={sum(not ok for _, ok in checks)}")
for name, ok in checks:
    if not ok: print("FAIL", name)
if len(checks) != 100 or not all(ok for _, ok in checks): raise SystemExit(1)
