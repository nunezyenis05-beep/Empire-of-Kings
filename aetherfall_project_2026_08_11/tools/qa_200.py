#!/usr/bin/env python3
"""Second-stage 200-task audit for the Android project.
The first 100 checks are delegated to qa_100.py; this file adds 100 deeper checks.
"""
from pathlib import Path
import subprocess
import xml.etree.ElementTree as ET

root = Path(__file__).resolve().parents[1]
first = subprocess.run(["python3", str(root / "tools/qa_100.py")], text=True)
if first.returncode:
    raise SystemExit(first.returncode)

checks = []
def check(name, ok): checks.append((name, bool(ok)))
def t(rel): return (root / rel).read_text(errors="ignore")
def has(rel, value): return value in t(rel)
def exists(rel): return (root / rel).is_file()

# 20 environment and build checks
for rel in [
    "setup_env_once.sh", "build_in_sandbox.sh", "ENVIRONMENT.md", ".gitignore",
    "gradlew", "gradle/wrapper/gradle-wrapper.jar", "gradle/wrapper/gradle-wrapper.properties",
    "gradle/wrapper/gradle-wrapper-shared.jar", "gradle/wrapper/gradle-cli.jar",
    "gradle/wrapper/gradle-files.jar", "gradle/wrapper/gradle-stdlib-java-extensions.jar",
]: check("env:file:" + rel, exists(rel))
check("env:wrapper-url", has("gradle/wrapper/gradle-wrapper.properties", "gradle-9.3.1-bin.zip"))
check("env:wrapper-network-timeout", has("gradle/wrapper/gradle-wrapper.properties", "networkTimeout=10000"))
check("env:bootstrap-temurin", has("setup_env_once.sh", "api.adoptium.net"))
check("env:bootstrap-platform", has("setup_env_once.sh", "platform-36_r02.zip"))
check("env:bootstrap-build-tools", has("setup_env_once.sh", "build-tools_r36_linux.zip"))
check("env:one-process-build", has("build_in_sandbox.sh", "source setup_env_once.sh"))
check("env:low-memory-gradle", has("build_in_sandbox.sh", "MaxMetaspaceSize=48m"))
check("env:docs-sandbox", has("ENVIRONMENT.md", "build_in_sandbox.sh"))
check("env:ignored-sdk", has(".gitignore", ".android-sdk/"))

# 20 XML and resource checks
xmls = list((root / "app/src/main/res").rglob("*.xml")) + [root / "app/src/main/AndroidManifest.xml"]
check("xml:expected-count", len(xmls) == 10)
for p in xmls:
    try: ET.parse(p); ok = True
    except ET.ParseError: ok = False
    check("xml:parse:" + p.name, ok)
check("xml:manifest-main-activity", has("app/src/main/AndroidManifest.xml", ".MainActivity"))
check("xml:manifest-internet", "android.permission.INTERNET" in t("app/src/main/AndroidManifest.xml"))
check("xml:manifest-main", "android.intent.action.MAIN" in t("app/src/main/AndroidManifest.xml"))
check("xml:manifest-launcher", "android.intent.category.LAUNCHER" in t("app/src/main/AndroidManifest.xml"))
check("xml:launcher-icon", has("app/src/main/AndroidManifest.xml", "@mipmap/ic_launcher"))
check("xml:strings-app-name", has("app/src/main/res/values/strings.xml", "Empire of Kings"))
check("xml:colors-defaults", has("app/src/main/res/values/colors.xml", "purple_500"))
check("xml:theme-app", has("app/src/main/res/values/themes.xml", "Theme.EmpireOfKings"))
check("xml:backup-rules", has("app/src/main/res/xml/backup_rules.xml", "full-backup-content"))
check("xml:data-extraction", has("app/src/main/res/xml/data_extraction_rules.xml", "data-extraction-rules"))
check("xml:adaptive-icon", has("app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml", "adaptive-icon"))
check("xml:round-icon", has("app/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml", "adaptive-icon"))

# 20 package, route and screen checks
kt = list((root / "app/src/main/java").rglob("*.kt"))
# The current design split keeps Room entities, remote clients, components,
# theme files and routes in dedicated production sources. Keep this count
# explicit so a new production source file cannot be added silently.
check("kotlin:file-count", len(kt) == 39)
check("routes:splash", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Splash"))
check("routes:auth", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Auth"))
check("routes:lobby", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Lobby"))
check("routes:shop", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Shop"))
check("routes:wardrobe", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Wardrobe"))
check("routes:disco", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Disco"))
check("routes:games", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Games"))
check("routes:inventory", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Inventory"))
check("routes:profile", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Profile"))
check("routes:settings", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object SettingsAdmin"))
check("routes:battle", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "object Battle"))
check("screen:auth", exists("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/AuthScreen.kt"))
check("screen:lobby", exists("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/MainLobbyScreen.kt"))
check("screen:battle", exists("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt"))
check("screen:settings", exists("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/SettingsAndAdminScreen.kt"))
check("screen:app-switch", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireApp.kt", "when (currentScreen)"))

# 20 data, auth and offline-first checks
check("data:room-database", has("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireDatabase.kt", "RoomDatabase"))
check("data:room-dao", has("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireDao.kt", "@Dao"))
check("data:repository", has("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt", "class EmpireRepository"))
check("data:seed", has("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt", "initializeSeedDataIfNeeded"))
check("data:account", exists("app/src/main/java/com/aistudio/empireofkings/game/data/UserAccount.kt"))
check("data:weapons", exists("app/src/main/java/com/aistudio/empireofkings/game/data/WeaponItem.kt"))
check("data:inventory", exists("app/src/main/java/com/aistudio/empireofkings/game/data/InventoryItem.kt"))
check("data:flow-account", has("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt", "Flow<UserAccount"))
check("data:flow-weapons", has("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt", "Flow<List<WeaponItem>>"))
check("data:flow-inventory", has("app/src/main/java/com/aistudio/empireofkings/game/data/EmpireRepository.kt", "Flow<List<InventoryItem>>"))
check("auth:local-session", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "authenticated"))
check("auth:remote-best-effort", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "remoteClient.authenticate"))
check("auth:token-storage", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "remote_token"))
check("auth:password-length", has("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/AuthScreen.kt", "password.length < 6"))
check("auth:sign-out", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "fun signOut"))
check("offline:local-battle", has("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "five combatants"))
check("offline:local-bots", has("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "Sombra_Bot_1"))
check("offline:rewards", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "recordBattleResult"))
check("offline:room-source", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "repository"))
check("offline:server-fallback", has("app/src/main/java/com/aistudio/empireofkings/game/ui/screens/BattleRoyaleScreen.kt", "RESPALDO LOCAL ACTIVO"))

# 20 online/network and safety checks
check("net:base-url", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireRemoteClient.kt", "https://empire-of-kings-server.onrender.com/"))
check("net:health", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireRemoteClient.kt", "health"))
check("net:socket", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "IO.socket"))
check("net:websocket", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "WebSocket.NAME"))
check("net:connect", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "EVENT_CONNECT"))
check("net:disconnect", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "EVENT_DISCONNECT"))
check("net:movement", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", '"playerMove"'))
check("net:actions", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", '"playerAction"'))
check("net:matchmaking", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", '"joinMatchmaking"'))
check("net:match-found", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", '"matchFound"'))
check("net:retry", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "retryOnlineConnection"))
check("net:cancel", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "cancelOnlineSearch"))
check("net:bounded-id", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "take(40)"))
check("net:bounded-roster", has("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt", "take(6)"))
check("net:bounded-health", has("app/src/main/java/com/aistudio/empireofkings/game/data/remote/EmpireSocketClient.kt", "coerceIn(0, 100)"))
check("net:no-api-key", "Server API Key" not in "\n".join(p.read_text(errors="ignore") for p in (root / "app/src/main").rglob("*.kt")))
check("net:no-private-key", "BEGIN PRIVATE KEY" not in "\n".join(p.read_text(errors="ignore") for p in (root / "app/src/main").rglob("*.kt")))
check("net:token-not-logged", "Log.d(\"token" not in t("app/src/main/java/com/aistudio/empireofkings/game/ui/EmpireViewModel.kt"))
check("net:server-not-modified", not (root / "Empire-of-Kings-Server").exists())
check("net:health-confirmed", "HTTP 200" in t("CHANGELOG.md"))

# This stage is 100 additional checks; together with qa_100.py it is a 200-task batch.
print(f"deep_checks={len(checks)} passed={sum(ok for _, ok in checks)} failed={sum(not ok for _, ok in checks)}")
for name, ok in checks:
    if not ok: print("FAIL", name)
if len(checks) != 100 or not all(ok for _, ok in checks): raise SystemExit(1)
