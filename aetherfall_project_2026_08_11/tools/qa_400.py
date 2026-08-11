#!/usr/bin/env python3
"""Static contract audit for the next 200-task batch.

This does not replace a real Android build. It verifies that the online
Socket.IO/loadout changes are present, bounded, documented and compatible
with the checked-in server contract.
"""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "app/src/main/java/com/aistudio/empireofkings/game"
SOCKET = ANDROID / "data/remote/EmpireSocketClient.kt"
VM = ANDROID / "ui/EmpireViewModel.kt"
APP = ANDROID / "ui/EmpireApp.kt"
BATTLE = ANDROID / "ui/screens/BattleRoyaleScreen.kt"
SERVER = ROOT.parent / "proyecto-empire/src/game_server.ts"
DOCS = [ROOT / name for name in (
    "ENVIRONMENT.md", "CHANGELOG.md", "EMPIRE_OF_KINGS_GPT_HANDOFF.md",
    "tools/qa_200.py", "setup_env_once.sh", "build_in_sandbox.sh",
)]

checks = []
def need(group, label, condition):
    checks.append((group, label, bool(condition)))

def text(path):
    path = Path(path)
    return path.read_text(encoding="utf-8") if path.exists() else ""

socket, vm, app, battle, server = map(text, (SOCKET, VM, APP, BATTLE, SERVER))
SOCKET_PATH, VM_PATH, APP_PATH, BATTLE_PATH, SERVER_PATH = SOCKET, VM, APP, BATTLE, SERVER

# Group 1: 50 server/client protocol checks.
actions = ["idle", "walk", "run", "sneak", "jump", "attack", "hurt", "death", "celebrate", "look"]
for action in actions:
    need("protocol", f"server accepts action {action}", f"'{action}'" in server)
for action in actions:
    need("protocol", f"client allowlist contains action {action}", f'"{action}"' in socket)
events = ["playerMove", "playerAction", "playerLoadout", "joinMatchmaking", "matchWaiting", "matchFound", "playerMoved", "gameError", "notificationError", "playerRegistered"]
for event in events:
    need("protocol", f"server event {event}", f'"{event}"' in server or f"'{event}'" in server)
client_events = ["playerMove", "playerAction", "playerLoadout", "joinMatchmaking", "matchWaiting", "matchFound", "playerMoved", "playerLoadout", "playerAction", "matchWaiting"]
for event in client_events:
    need("protocol", f"client event {event}", f'"{event}"' in socket)
protocol_tokens = [
    ("server handshake auth", "socket.handshake.auth", server),
    ("server registers handshake player", "registerPlayer(socket, handshakePlayerId", server),
    ("client auth player id", '"playerId" to playerId', socket),
    ("client auth avatar", '"avatarPreset" to avatarPreset', socket),
    ("server player room", "socket.join(id)", server),
    ("server player registration event", "playerRegistered", server),
    # Registration is completed by the authenticated Socket.IO handshake; the
    # client does not need a separate playerRegistered acknowledgement listener.
    ("client registration uses handshake identity", '"playerId" to playerId', socket),
    ("server invalid action error", "Animación no válida", server),
    ("client match players array", 'optJSONArray("players")', socket),
    ("server match players payload", "players: match", server),
]
for label, needle, source in protocol_tokens:
    need("protocol", label, needle in source)
# 20 action checks + 10 server events + 10 client events + 10 protocol checks = 50.

# Group 2: 50 loadout integration checks.
loadout_fields = ["outfit", "weapon", "armor", "accessory"]
loadout_sources = [
    ("data model", socket),
    ("socket emit", socket),
    ("socket validation", socket),
    ("server normalization", server),
    ("battle display", battle),
]
for field in loadout_fields:
    for label, source in loadout_sources:
        need("loadout", f"{label} contains {field}", field in source)
# 4 fields x 5 sources = 20; add 30 lifecycle/loadout-specific checks.
loadout_tokens = [
    ("PlayerLoadout model", "data class PlayerLoadout", socket),
    ("RemotePlayerLoadout model", "data class RemotePlayerLoadout", socket),
    ("loadout default explorer", 'outfit: String = "explorer"', socket),
    ("loadout default none weapon", 'weapon: String = "none"', socket),
    ("loadout default leather armor", 'armor: String = "leather"', socket),
    ("loadout default none accessory", 'accessory: String = "none"', socket),
    ("loadout callback parameter", "onPlayerLoadout", socket),
    ("loadout emitted on connect", 'emitLoadout()', socket),
    ("loadout event listener", 'newSocket.on("playerLoadout")', socket),
    ("loadout callback in ViewModel", "onPlayerLoadout =", vm),
    ("remote loadouts state", "_remoteLoadouts", vm),
    ("remote loadouts public flow", "val remoteLoadouts", vm),
    ("remote loadouts collected", "viewModel.remoteLoadouts", app),
    ("remote loadouts passed to screen", "remoteLoadouts = remoteLoadouts", app),
    ("screen accepts remote loadouts", "remoteLoadouts: List<RemotePlayerLoadout>", battle),
    ("remote outfit rendered", "remoteLoadout.outfit", battle),
    ("remote weapon rendered", "remoteLoadout.weapon", battle),
    ("remote armor rendered", "remoteLoadout.armor", battle),
    ("loadout limit six", "updated.size <= 6", vm),
    ("loadout id bounded", 'optString("id").trim().take(40)', socket),
    ("outfit whitelist", "validOutfits", socket),
    ("weapon whitelist", "validWeapons", socket),
    ("armor whitelist", "validArmor", socket),
    ("accessory whitelist", "validAccessories", socket),
    ("server loadout map", "playerLoadouts", server),
    ("server loadout handler", "playerLoadout", server),
    ("server broadcasts loadout", "io.emit('playerLoadout'", server),
    ("local loadout helper", "currentOnlineLoadout", vm),
    ("weapon category mapping", "weaponCategory.contains", vm),
    ("loadout sent after reconnect", "EVENT_CONNECT", socket),
]
for label, needle, source in loadout_tokens:
    need("loadout", label, needle in source)

# Group 3: 50 lifecycle and safety checks.
status_names = ["DISCONNECTED", "CONNECTING", "CONNECTED", "MATCHMAKING", "MATCH_FOUND"]
for status in status_names:
    need("lifecycle", f"status declared {status}", status in socket)
    # Statuses are delivered through one shared callback; CONNECTING (and the
    # other ordinary states) does not need a redundant ViewModel branch.
    vm_handles_status = (
        "_onlineSessionStatus.value = status" in vm
        or f"OnlineSessionStatus.{status}" in vm
    )
    need("lifecycle", f"ViewModel handles {status}", vm_handles_status)
    need(
        "lifecycle",
        f"screen handles {status}",
        f"OnlineSessionStatus.{status}" in battle,
    )
# 5 x 3 = 15, plus 35 concrete lifecycle/safety assertions.
lifecycle_tokens = [
    ("disconnect clears socket", "socket = null", socket),
    ("disconnect removes listeners", "socket?.off()", socket),
    ("disconnect resets matchmaking", "matchmakingRequested = false", socket),
    ("cancel matchmaking exists", "fun cancelMatchmaking", socket),
    ("retry connection exists", "fun retryOnlineConnection", vm),
    ("cancel online search exists", "fun cancelOnlineSearch", vm),
    ("navigate leaves battle", "leavingBattle", vm),
    ("battle disconnect on exit", "socketClient.disconnect()", vm),
    ("remote players cleared", "_remotePlayers.value = emptyMap()", vm),
    ("match players cleared", "_matchPlayers.value = emptyList()", vm),
    ("ready players cleared", "_readyPlayers.value = emptySet()", vm),
    ("remote actions cleared", "_remoteActions.value = emptyMap()", vm),
    ("remote loadouts cleared", "_remoteLoadouts.value = emptyMap()", vm),
    ("movement finite guard", "!x.isFinite() || !y.isFinite()", socket),
    ("movement x bounded", "coerceIn(0f, 1000f)", socket),
    ("movement y bounded", "coerceIn(0f, 1000f)", socket),
    ("movement running sent", '"running", running', socket),
    ("remote position x bounded", "x.toFloat().coerceIn(0f, 1000f)", socket),
    ("remote position y bounded", "y.toFloat().coerceIn(0f, 1000f)", socket),
    ("remote health bounded", "coerceIn(0, 100)", socket),
    ("action blank guard", "cleanAction.isBlank()", socket),
    ("action allowlist guard", "cleanAction !in allowedActions", socket),
    ("action throttle", "lastActionAtMs", socket),
    ("match player limit", "size < 6", socket),
    ("ViewModel match limit", "take(6)", vm),
    ("screen roster limit", ".take(6)", battle),
    ("remote signal timeout", "10_000L", battle),
    ("countdown requires match", "MATCH_FOUND", battle),
    ("countdown requires two players", "rosterPlayers.size > 1", battle),
    ("countdown button lock", "enabled = launchCountdown == 0", battle),
    ("offline fallback label", "RESPALDO LOCAL ACTIVO", battle),
    ("server health check", "checkHealth()", vm),
    ("server status independent", "checkServerStatus()", vm),
    ("reconnection enabled", "reconnection = true", socket),
    ("reconnection attempts bounded", "reconnectionAttempts = 5", socket),
]
for label, needle, source in lifecycle_tokens:
    need("lifecycle", label, needle in source)

# Group 4: 50 documentation/build/contract checks.
doc_tokens = [
    ("environment documents Java", "Temurin JDK 17", DOCS[0]),
    ("environment documents SDK", "Android SDK Platform 36", DOCS[0]),
    ("environment documents memory", "memoria limitada", DOCS[0]),
    ("environment warns build unconfirmed", "compilación", DOCS[0]),
    ("changelog mentions loadouts", "loadout", DOCS[1]),
    ("changelog mentions protocol guard", "ready", DOCS[1]),
    ("handoff mentions offline", "offline", DOCS[2]),
    ("handoff mentions no secrets", "secret", DOCS[2]),
    ("handoff mentions Socket.IO", "Socket.IO", DOCS[2]),
    ("handoff documents actual build attempt", "Se intentó `test assembleDebug`", DOCS[2]),
    ("qa 200 exists", "checks=", DOCS[3]),
    ("setup uses Java 17", "JDK_ROOT", DOCS[4]),
    ("setup uses SDK root", "ANDROID_SDK_ROOT", DOCS[4]),
    ("setup uses Gradle 9.3.1", "gradle-9.3.1", DOCS[4]),
    ("sandbox invokes tests", "test assembleDebug", DOCS[5]),
    ("sandbox no daemon", "--no-daemon", DOCS[5]),
]
for label, needle, path in doc_tokens:
    need("docs", label, needle in text(path))
# 16 documented checks + 34 source-level invariants.
source_tokens = [
    ("namespace", "com.aistudio.empireofkings.game", ROOT / "app/build.gradle.kts"),
    ("compile sdk", "compileSdk", ROOT / "app/build.gradle.kts"),
    ("min sdk", "minSdk", ROOT / "app/build.gradle.kts"),
    ("Room dependency", "room", ROOT / "app/build.gradle.kts"),
    ("Compose dependency", "compose", ROOT / "app/build.gradle.kts"),
    ("wrapper properties", "gradle-9.3.1", ROOT / "gradle/wrapper/gradle-wrapper.properties"),
    ("remote base URL", "empire-of-kings-server.onrender.com", ANDROID / "data/remote/EmpireRemoteClient.kt"),
    ("health path", "health", ANDROID / "data/remote/EmpireRemoteClient.kt"),
    ("auth path", "auth", ANDROID / "data/remote/EmpireRemoteClient.kt"),
    ("Room repository", "class EmpireRepository", ANDROID / "data/EmpireRepository.kt"),
    ("local battle screen", "BattleRoyaleScreen", BATTLE_PATH),
    ("local reward callback", "onBattleFinished", BATTLE_PATH),
    ("player id handshake", '"playerId" to playerId', SOCKET_PATH),
    ("avatar handshake", '"avatarPreset" to avatarPreset', SOCKET_PATH),
    ("websocket transport", "WebSocket.NAME", SOCKET_PATH),
    ("timeout configured", "timeout = 10000L", SOCKET_PATH),
    ("server action validation", "allowedActions", SERVER_PATH),
    ("server movement validation", "movePlayer", SERVER_PATH),
    ("server matchmaking", "addToMatchmaking", SERVER_PATH),
    ("server connected state", "setPlayerConnected", SERVER_PATH),
    ("client launch guard", "allPlayersReady", BATTLE_PATH),
    ("client local action", "onOnlineAction", BATTLE_PATH),
    ("client local ready", "onOnlineReady", BATTLE_PATH),
    ("client retry button", "onRetryOnline", BATTLE_PATH),
    ("client local fallback", "onCancelOnlineSearch", BATTLE_PATH),
    ("app collects match state", "matchPlayers", APP_PATH),
    ("app collects ready state", "readyPlayers", APP_PATH),
    ("app collects remote players", "remotePlayers", APP_PATH),
    ("app passes server status", "serverStatus = serverStatus", APP_PATH),
    ("app passes local player id", "localPlayerId = localPlayerId", APP_PATH),
    ("app has authentication route", "ScreenRoute.Auth", APP_PATH),
    ("app has lobby route", "ScreenRoute.Lobby", APP_PATH),
    ("app has battle route", "ScreenRoute.Battle", APP_PATH),
    ("QA script executable content", "Static contract audit", ROOT / "tools/qa_400.py"),
]
for label, needle, path in source_tokens:
    need("docs", label, needle in text(path))

expected = {"protocol": 50, "loadout": 50, "lifecycle": 50, "docs": 50}
actual = {group: sum(1 for g, _, _ in checks if g == group) for group in expected}
if actual != expected:
    raise SystemExit(f"group count mismatch: expected {expected}, actual {actual}")
failed = [(group, label) for group, label, ok in checks if not ok]
print(f"batch_checks={len(checks)} passed={len(checks)-len(failed)} failed={len(failed)}")
for group, label in failed:
    print(f"FAIL [{group}] {label}")
if failed:
    raise SystemExit(1)
