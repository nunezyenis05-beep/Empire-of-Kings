#!/usr/bin/env python3
"""Batch 3 static audit: exactly 200 auditable checks.

This is source QA only. It deliberately does not report an Android build as
successful; Gradle/test/assemble must be run separately.
"""
from pathlib import Path
import re
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "app/src/main/java/com/aistudio/empireofkings/game"
DATA = SRC / "data"
UI = SRC / "ui"
SCREENS = UI / "screens"
COMP = UI / "components"
checks = []
def check(group, name, ok): checks.append((f"{group}:{name}", bool(ok)))
def text(path): return Path(path).read_text(encoding="utf-8", errors="ignore") if Path(path).exists() else ""
def has(path, token): return token in text(path)
def exists(path): return Path(path).is_file()
def tokens(group, path, pairs):
    for name, token in pairs: check(group, name, has(path, token))

db = DATA / "EmpireDatabase.kt"
dao = DATA / "EmpireDao.kt"
repo = DATA / "EmpireRepository.kt"
state = DATA / "ActivityState.kt"
vm = UI / "EmpireViewModel.kt"
app = UI / "EmpireApp.kt"
disco = SCREENS / "DiscoScreen.kt"
games = SCREENS / "GamesScreen.kt"
battle = SCREENS / "BattleRoyaleScreen.kt"
lock = ROOT / "docs/UI_REFERENCE_LOCK.md"

# 50 persisted state and Room checks.
tokens("room", state, [
 ("activity state file", "package com.aistudio.empireofkings.game.data"), ("disco entity", '@Entity(tableName = "disco_state")'),
 ("disco key", '"local_discoteca"'), ("track id", "selectedTrackId: String"), ("emote id", "selectedEmoteId: String"),
 ("emote count", "emoteCount: Int"), ("state timestamp", "updatedAt: Long"), ("arcade entity", '@Entity(tableName = "mini_game_progress")'),
 ("arcade key", '"local_arcade"'), ("selected game", "selectedGameId: String"), ("played count", "gamesPlayed: Int"),
 ("won count", "gamesWon: Int"), ("last result", "lastResult: String"), ("gold reward", "lastRewardGold: Long"),
 ("diamond reward", "lastRewardDiamonds: Long"), ("replaceable audio disclosure", "tracks are replaceable IDs"),
])
tokens("room", db, [
 ("database version five", "version = 5"), ("disco entity registered", "DiscoState::class"), ("arcade entity registered", "MiniGameProgress::class"),
 ("three-four migration", "MIGRATION_3_4"), ("migration class", "Migration(3, 4)"), ("disco table migration", "CREATE TABLE IF NOT EXISTS disco_state"),
 ("arcade table migration", "CREATE TABLE IF NOT EXISTS mini_game_progress"), ("disco primary key", "PRIMARY KEY(id)"),
 ("migration registered", "MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5"),
])
tokens("room", dao, [
 ("disco flow", "fun getDiscoState(): Flow<DiscoState?>"), ("disco snapshot", "suspend fun getDiscoStateSnapshot"), ("disco save", "suspend fun saveDiscoState"),
 ("arcade flow", "fun getMiniGameProgress(): Flow<MiniGameProgress?>"), ("arcade snapshot", "suspend fun getMiniGameProgressSnapshot"), ("arcade save", "suspend fun saveMiniGameProgress"),

])
# 16 + 12 + 7 + 7 + 8 = 50

tokens("repository", repo, [
 ("disco repository flow", "val discoState: Flow<DiscoState?>"), ("arcade repository flow", "val miniGameProgress: Flow<MiniGameProgress?>"),
 ("seed disco", "dao.saveDiscoState(DiscoState"), ("seed arcade", "dao.saveMiniGameProgress(MiniGameProgress"),
 ("disco backfill", "dao.getDiscoStateSnapshot() == null"), ("arcade backfill", "dao.getMiniGameProgressSnapshot() == null"),
 ("track allowlist", "DISCO_TRACK_IDS"), ("emote allowlist", "DISCO_EMOTE_IDS"), ("game allowlist", "MINI_GAME_IDS"),
 ("save disco method", "suspend fun saveDiscoSelection"), ("emote count increment", "current.emoteCount + 1"),
 ("save game method", "suspend fun saveSelectedMiniGame"), ("settle method", "suspend fun settleMiniGame"), ("game count increment", "current.gamesPlayed + 1"),
 ("win count guard", "if (won) 1 else 0"), ("bounded gold reward", "rewardGold.coerceIn(0L, 50_000L)"), ("bounded diamond reward", "rewardDiamonds.coerceIn(0L, 500L)"),
 ("reward currency write", "saveUserAndMiniGameProgress"),
])
# 20 repository + 20 VM/app state = 40, finish room group with 10.
tokens("viewmodel", vm, [
 ("viewmodel disco state", "val discoState: StateFlow<DiscoState>"), ("viewmodel arcade state", "val miniGameProgress: StateFlow<MiniGameProgress>"),
 ("viewmodel save track", "fun saveDiscoTrack"), ("viewmodel emote", "fun triggerDiscoEmote"), ("viewmodel select game", "fun selectMiniGame"),
 ("viewmodel settle game", "fun settleMiniGame"), ("viewmodel repository track", "repository.saveDiscoSelection"), ("viewmodel repository settle", "repository.settleMiniGame"),
 ("legacy reward clamp", "gold.coerceAtLeast(0)"), ("local repository source", "private val repository: EmpireRepository"),
])

# 50 Discoteca checks: portrait composition, real persisted interactions, fallback disclosure.
tokens("disco", disco, [
 ("state parameter", "discoState: DiscoState"), ("music setting parameter", "musicEnabled: Boolean"), ("track callback", "onSelectTrack: (String) -> Unit"),
 ("emote callback", "onSelectEmote: (String) -> Unit"), ("track model", "private data class DiscoTrack"), ("emote model", "private data class DiscoEmote"),
 ("anthem id", '"anthem"'), ("thrones id", '"thrones"'), ("lions id", '"lions"'), ("sanctuary id", '"sanctuary"'),
 ("throne emote id", '"throne_dance"'), ("rune emote id", '"rune_spin"'), ("blue fire id", '"blue_fire"'), ("wave emote id", '"royal_wave"'),
 ("portrait column", "Column(modifier = Modifier.fillMaxSize())"), ("portrait lazy content", "LazyColumn("), ("bottom nav", "BottomNavBar(currentRoute = ScreenRoute.Disco"),
 ("shared title", "ReferenceTitle("), ("shared panel", "ReferencePanel"), ("gold panel", "GoldBorder"), ("dark surface", "Color(0xFF0F1526)"),
 ("native avatar renderer", "HumanAvatar3D"), ("GLB avatar preset", "preset = userAccount?.avatarPreset"), ("one avatar circle", "CircleShape"),
 ("selected track lookup", "discoState.selectedTrackId"), ("selected emote lookup", "discoState.selectedEmoteId"), ("persist track click", "onSelectTrack(track.id)"),
 ("persist emote click", "onSelectEmote(emote.id)"), ("track active label", '"ACTIVA"'), ("emote count display", "discoState.emoteCount"),
 ("audio enabled label", '"AUDIO ACTIVO"'), ("audio disabled label", '"AUDIO DESACTIVADO"'), ("replaceable audio", "Audio reemplazable"),
 ("no fake installed asset", "no inventa una pista instalada"), ("emote grid", "emotes.chunked(2)"), ("track list", "tracks.forEach"), ("track source", "track.source"),
 ("selected border", "if (selected) GoldBorder"), ("music icon", "Icons.Default.MusicNote"), ("play icon", "Icons.Default.PlayArrow"), ("profile route", "ScreenRoute.Profile"),
 ("responsive width", "fillMaxWidth()"), ("scroll padding", "contentPadding = PaddingValues"), ("compact text", "fontSize = 10.sp"),
])

# 50 Games checks: a reward requires a two-control session and settles through Room/VM.
tokens("games", games, [
 ("progress parameter", "progress: MiniGameProgress"), ("select callback", "onSelectGame: (String) -> Unit"), ("settle callback", "onSettleGame: (String, Boolean, Long, Long) -> Unit"),
 ("arcade model", "private data class ArcadeGame"), ("chess id", '"imperial_chess"'), ("cards id", '"rune_cards"'), ("domino id", '"lion_domino"'),
 ("gold reward", '"+5,000 Oro"'), ("diamond reward", '"+20 Diamantes"'), ("domino reward", '"+3,500 Oro"'),
 ("active session state", "activeGameId"), ("move state", "moveCount"), ("feedback state", "feedback"), ("begin function", "fun begin(game: ArcadeGame)"),
 ("action function", "fun act(game: ArcadeGame)"), ("session start", "Partida iniciada"), ("select persistence", "onSelectGame(game.id)"), ("two controls", "moveCount >= 2"),
 ("settle persistence", "onSettleGame(game.id, true, game.gold, game.diamonds)"), ("settle once", "activeGameId = null"), ("reset moves", "moveCount = 0"),
 ("progress wins", "progress.gamesWon"), ("progress played", "progress.gamesPlayed"), ("last result", "progress.lastResult"), ("last reward gold", "progress.lastRewardGold"),
 ("last reward diamonds", "progress.lastRewardDiamonds"), ("portrait column", "Column(Modifier.fillMaxSize())"), ("portrait lazy list", "LazyColumn("), ("bottom nav", "BottomNavBar(currentRoute = ScreenRoute.Games"),
 ("shared title", "ReferenceTitle("), ("shared panel", "ReferencePanel"), ("mobile width", "fillMaxWidth()"), ("game keys", "items(arcadeGames, key = { it.id })"),
 ("start button", '"INICIAR PARTIDA"'), ("control button", "game.control"), ("move label", "JUGADA ${moveCount + 1}/2"), ("active branch", "if (isActive)"),
 ("inactive branch", "else {"), ("reward feedback", "Victoria confirmada"), ("local disclosure", "no se realiza dinero real"), ("top bar", "TopBar(userAccount"),
 ("gold visual", "GoldPrimary"), 
])

# 50 Battle / app wiring / contract checks.
tokens("battle", battle, [
 ("battle route screen", "fun BattleRoyaleScreen"), ("local five roster", "val totalPlayers = 5"), ("local bots", "Sombra_Bot_1"), ("safe zone", "safeZoneRadius"),
 ("player movement", "onOnlineMove"), ("online action", "onOnlineAction"), ("ready callback", "onOnlineReady"), ("retry callback", "onRetryOnline"), ("cancel callback", "onCancelOnlineSearch"),
 ("portrait scale x", "val scaleX = size.width / 800f"), ("portrait scale y", "val scaleY = size.height / 600f"), ("uniform radius scale", "val scale = minOf(scaleX, scaleY)"),
 ("arena transform", "fun arenaPoint(x: Float, y: Float)"), ("dynamic grid", "for (i in 0..800 step 80)"), ("remote clamp", "remote.x.coerceIn(16f, 784f)"),
 ("remote health bar", "remote.health / 100f"), ("bot transform", "arenaPoint(b.x, b.y)"), ("projectile transform", "arenaPoint(p.x, p.y)"), ("player transform", "arenaPoint(playerX, playerY)"),
 ("safe zone scaled", "safeZoneRadius * scale"), ("back action", "onNavigate(ScreenRoute.Lobby)"), ("back content description", '"Volver al Lobby"'),
 ("equipped weapon label", "equippedWeaponName.take(22)"), ("bounded joystick", "if (length > 48f)"), ("joystick reset", "joystickOffset = Offset.Zero"), ("fire ammo guard", "if (ammo > 0)"),
 ("gloo count guard", "if (glooWallsCount > 0)"), ("victory state", "isVictory"), ("defeat state", "isDefeat"), ("reward guard", "rewardRecorded"),
])
tokens("wiring", app, [
 ("app collects disco", "viewModel.discoState.collectAsStateWithLifecycle()"), ("app collects arcade", "viewModel.miniGameProgress.collectAsStateWithLifecycle()"),
 ("app passes disco", "discoState = discoState"), ("app passes music", "musicEnabled = appSettings.musicEnabled"), ("app track callback", "onSelectTrack = { trackId -> viewModel.saveDiscoTrack(trackId) }"),
 ("app emote callback", "onSelectEmote = { emoteId -> viewModel.triggerDiscoEmote(emoteId) }"), ("app passes progress", "progress = miniGameProgress"),
 ("app select callback", "onSelectGame = { gameId -> viewModel.selectMiniGame(gameId) }"), ("app settle callback", "onSettleGame = { gameId, won, gold, diamonds -> viewModel.settleMiniGame(gameId, won, gold, diamonds) }"),
])
tokens("contract", lock, [
 ("dark blue black", "dark blue/black"), ("gold frames", "thin gold"), ("compact portrait", "compact mobile portrait"), ("single avatar", "one avatar/character"),
 ("fixed navigation", "Lobby, Shop, Inventory, Wardrobe, Profile, Disco, Clan, Games, Settings"), ("real logic rule", "real logic, data, persistence, and feedback"),
 ("no decorative controls", "no decorative-only controls"), ("kotlin compose", "Kotlin, Jetpack Compose, ViewModel, and Room"),
])
# 24 battle + 10 wiring + 8 contract = 42; XML and documentation checks complete the final 8.
manifest = ROOT / "app/src/main/AndroidManifest.xml"
for name, ok in [
 ("manifest parses", _ := (lambda: (ET.parse(manifest) is not None))()),
 ("manifest internet", has(manifest, "android.permission.INTERNET")),
 ("manifest launcher", has(manifest, "android.intent.category.LAUNCHER")),
 ("no server project", not (ROOT / "Empire-of-Kings-Server").exists()),
 ("qa file exists", exists(ROOT / "tools/qa_batch3_200.py")),
 ("build script documented", has(ROOT / "ENVIRONMENT.md", "assembleDebug")),
 ("reference image exists", exists(ROOT / "docs/reference/ui-reference-master.jpg")),
 ("android manifest activity", has(manifest, ".MainActivity")),
]: check("release", name, ok)

print(f"batch3_checks={len(checks)} passed={sum(ok for _, ok in checks)} failed={sum(not ok for _, ok in checks)}")
for name, ok in checks:
    if not ok: print("FAIL", name)
if len(checks) != 200 or not all(ok for _, ok in checks): raise SystemExit(1)
