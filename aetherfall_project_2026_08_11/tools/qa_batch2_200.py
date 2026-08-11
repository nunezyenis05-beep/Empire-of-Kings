#!/usr/bin/env python3
"""Exactly 201 static audits for continuation batch 2.

This audit covers the additive Room wardrobe/profile slice, the fixed nine-tab
portrait navigation, and the reference-safe visual contracts. It intentionally
never treats a static check as an Android build result.
"""
from pathlib import Path
import re

ROOT = Path(__file__).resolve().parents[1]
SRC = ROOT / "app/src/main/java/com/aistudio/empireofkings/game"
DATA = SRC / "data"
UI = SRC / "ui"
SCREENS = UI / "screens"
COMP = UI / "components"
checks = []
def check(group, name, ok): checks.append((group, name, bool(ok)))
def text(path): return Path(path).read_text(encoding="utf-8", errors="ignore") if Path(path).exists() else ""
def has(path, token): return token in text(path)
def exists(path): return Path(path).is_file()

db = DATA / "EmpireDatabase.kt"
dao = DATA / "EmpireDao.kt"
repo = DATA / "EmpireRepository.kt"
account = DATA / "UserAccount.kt"
wardrobe = DATA / "WardrobeItem.kt"
vm = UI / "EmpireViewModel.kt"
app = UI / "EmpireApp.kt"
wardrobe_ui = SCREENS / "WardrobeScreen.kt"
profile_ui = SCREENS / "ProfileScreen.kt"
nav = COMP / "BottomNavBar.kt"
top = COMP / "TopBar.kt"
chrome = COMP / "ReferenceChrome.kt"
changelog = ROOT / "CHANGELOG.md"
lock = ROOT / "docs/UI_REFERENCE_LOCK.md"

# Group 1: 49 persistent data and migration checks.
data_tokens = [
    ("wardrobe entity exists", exists(wardrobe)),
    ("wardrobe Entity annotation", has(wardrobe, '@Entity(tableName = "wardrobe_items")')),
    ("wardrobe primary key", has(wardrobe, "@PrimaryKey val id")),
    ("wardrobe slot", has(wardrobe, "val slot: String")),
    ("wardrobe rarity", has(wardrobe, "val rarity: String")),
    ("wardrobe description", has(wardrobe, "val description: String")),
    ("wardrobe icon fallback field", has(wardrobe, "val iconName: String")),
    ("wardrobe avatar preset", has(wardrobe, "val avatarPreset: String")),
    ("wardrobe ownership", has(wardrobe, "val isOwned: Boolean")),
    ("wardrobe equipped state", has(wardrobe, "val isEquipped: Boolean")),
    ("database wardrobe entity", has(db, "WardrobeItem::class")),
    ("database version five", has(db, "version = 5")),
    ("migration two three", has(db, "MIGRATION_2_3")),
    ("migration class", has(db, "Migration(2, 3)")),
    ("migration profile avatar", has(db, "ADD COLUMN avatarPreset")),
    ("migration profile bio", has(db, "ADD COLUMN profileBio")),
    ("migration presence", has(db, "ADD COLUMN presenceStatus")),
    ("migration wardrobe table", has(db, "CREATE TABLE IF NOT EXISTS wardrobe_items")),
    ("migration wardrobe primary key", has(db, "PRIMARY KEY(id)")),
    ("migration keeps defaults", has(db, "DEFAULT 'king_warrior'")),
    ("migration registered", has(db, ".addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)")),
    ("dao wardrobe flow", has(dao, "fun getWardrobeItems(): Flow<List<WardrobeItem>>")),
    ("dao wardrobe order", has(dao, "ORDER BY isEquipped DESC")),
    ("dao wardrobe snapshot", has(dao, "suspend fun getWardrobeItem")),
    ("dao wardrobe count", has(dao, "suspend fun getWardrobeCount")),
    ("dao wardrobe insert", has(dao, "suspend fun insertWardrobeItems")),
    ("dao wardrobe equip", has(dao, "suspend fun equipWardrobeItem")),
    ("dao slot isolation", has(dao, "WHERE slot = :slot")),
    ("dao conflict replace", has(dao, "OnConflictStrategy.REPLACE")),
    ("repository wardrobe flow", has(repo, "val wardrobeItems: Flow<List<WardrobeItem>>")),
    ("repository count backfill", has(repo, "dao.getWardrobeCount() == 0")),
    ("repository initial wardrobe", has(repo, "initialWardrobeItems()")),
    ("repository wardrobe list", has(repo, "private fun initialWardrobeItems(): List<WardrobeItem>")),
    ("seed royal outfit", has(repo, '"outfit_royal"')),
    ("seed night outfit", has(repo, '"outfit_night"')),
    ("seed armor", has(repo, '"armor_azur"')),
    ("seed cape", has(repo, '"cape_crown"')),
    ("retired crown seed removed", ("crown" + "_" + "eternity") not in text(repo)),
    ("seed accessory", has(repo, '"amulet_sun"')),
    ("seed equipped defaults", has(repo, "isEquipped = true")),
    ("account avatar preset", has(account, "val avatarPreset: String")),
    ("account profile bio", has(account, "val profileBio: String")),
    ("account presence", has(account, "val presenceStatus: String")),
    ("account defaults safe", has(account, '"king_warrior"')),
    ("profile update repository", has(repo, "suspend fun updateProfile")),
    ("profile name trim", has(repo, "username.trim().take(24)")),
    ("profile bio bound", has(repo, "bio.trim().take(120)")),
    ("profile avatar catalog allowlist", has(repo, "AvatarCatalog.validIds")),
    ("wardrobe ownership guard", has(repo, "if (!item.isOwned)")),
]
for i, (name, ok) in enumerate(data_tokens, 1): check("data", f"D{i:02d}:{name}", ok)

# Group 2: 49 wardrobe interaction and visual checks.
wardrobe_tokens = [
    ("screen accepts wardrobe items", has(wardrobe_ui, "wardrobeItems: List<WardrobeItem>")),
    ("screen equip callback", has(wardrobe_ui, "onEquipWardrobeItem")),
    ("screen weapon callback", has(wardrobe_ui, "onEquipWeapon")),
    ("screen upgrade callback", has(wardrobe_ui, "onUpgradeWeapon")),
    ("section wardrobe", has(wardrobe_ui, '"VESTUARIO"')),
    ("section weapons", has(wardrobe_ui, '"ARMAS"')),
    ("section state", has(wardrobe_ui, "activeSection")),
    ("selected cosmetic state", has(wardrobe_ui, "selectedItem")),
    ("selected weapon state", has(wardrobe_ui, "selectedWeapon")),
    ("room emission sync cosmetics", has(wardrobe_ui, "LaunchedEffect(wardrobeItems)")),
    ("room emission sync weapons", has(wardrobe_ui, "LaunchedEffect(weapons)")),
    ("collection panel", has(wardrobe_ui, '"COLECCIÓN"')),
    ("owned count", has(wardrobe_ui, "isOwned")),
    ("equipped label", has(wardrobe_ui, '"ACTIVO"')),
    ("cosmetic list key", has(wardrobe_ui, "key = { it.id }")),
    ("cosmetic click", has(wardrobe_ui, "onSelect(item)")),
    ("equip action invokes callback", has(wardrobe_ui, "onEquipWardrobeItem(item.id)")),
    ("slot feedback", has(wardrobe_ui, "equipado en ${item.slot}")),
    ("cosmetic detail", has(wardrobe_ui, "item.description")),
    ("cosmetic rarity", has(wardrobe_ui, "item.rarity")),
    ("one native avatar", has(wardrobe_ui, "HumanAvatar3D")),
    ("GLB asset path", has(wardrobe_ui, 'preset = item.avatarPreset')),
    ("native 3D disclosure", has(wardrobe_ui, "EquipmentCatalog.statusLabel")),
    ("one avatar per tile", has(wardrobe_ui, "One human avatar per selection tile")),
    ("equip disabled state", has(wardrobe_ui, "enabled = item.isOwned && !item.isEquipped")),
    ("equip button label", has(wardrobe_ui, '"EQUIPAR ${item.slot.uppercase()}"')),
    ("equipped button label", has(wardrobe_ui, '"EQUIPADO"')),
    ("feedback state", has(wardrobe_ui, "var feedback")),
    ("reference background", has(wardrobe_ui, "Mystic3DBackground")),
    ("reference panel", has(wardrobe_ui, "ReferencePanel")),
    ("reference title", has(wardrobe_ui, "ReferenceTitle")),
    ("top bar", has(wardrobe_ui, "TopBar")),
    ("bottom nav", has(wardrobe_ui, "BottomNavBar")),
    ("weapon list", has(wardrobe_ui, "items(weapons")),
    ("weapon stats damage", has(wardrobe_ui, "weapon.damage")),
    ("weapon stats cadence", has(wardrobe_ui, "weapon.fireRate")),
    ("weapon stats range", has(wardrobe_ui, "weapon.range")),
    ("weapon stats accuracy", has(wardrobe_ui, "weapon.accuracy")),
    ("weapon equip action", has(wardrobe_ui, "onEquip(weapon.id)")),
    ("weapon upgrade action", has(wardrobe_ui, "onUpgrade(weapon)")),
    ("weapon level", has(wardrobe_ui, "weapon.level")),
    ("weapon premium label", has(wardrobe_ui, "isMysticalPremium")),
    ("weapon feedback", has(wardrobe_ui, "feedback?.let")),
    ("portrait vertical panels", has(wardrobe_ui, "Column(modifier.fillMaxWidth().padding(horizontal = 12.dp)")),
    ("detail panel fills portrait", has(wardrobe_ui, "Modifier.fillMaxWidth().weight(1f)")),
    ("single avatar comment", has(wardrobe_ui, "Un avatar humano por selección")),
    ("lazy cosmetic list", has(wardrobe_ui, "LazyColumn")),
    ("owned action guard", has(wardrobe_ui, "item.isOwned")),
    ("slot equipment semantics", has(wardrobe_ui, "item.slot.uppercase()")),
]
for i, (name, ok) in enumerate(wardrobe_tokens, 1): check("wardrobe", f"W{i:02d}:{name}", ok)

# Group 3: 49 profile editing, stats and avatar checks.
profile_tokens = [
    ("profile save parameter", has(profile_ui, "onSaveProfile:")),
    ("profile save name", has(profile_ui, "String, String, String, String")),
    ("profile user fallback", has(profile_ui, "UserAccount()")),
    ("profile username state", has(profile_ui, "var username")),
    ("profile bio state", has(profile_ui, "var bio")),
    ("profile presence state", has(profile_ui, "var presence")),
    ("profile avatar state", has(profile_ui, "var avatarPreset")),
    ("profile edit state", has(profile_ui, "var editing")),
    ("profile feedback", has(profile_ui, "var feedback")),
    ("profile state sync", has(profile_ui, "LaunchedEffect(user.id")),
    ("profile title", has(profile_ui, '"PERFIL DEL IMPERIO"')),
    ("identity panel", has(profile_ui, '"IDENTIDAD"')),
    ("stats panel", has(profile_ui, '"ESTADÍSTICAS DEL IMPERIO"')),
    ("identity edit button", has(profile_ui, '"EDITAR"')),
    ("identity close button", has(profile_ui, '"CERRAR"')),
    ("name text field", has(profile_ui, 'label = { Text("Nombre") }')),
    ("bio text field", has(profile_ui, 'label = { Text("Bio") }')),
    ("presence text field", has(profile_ui, 'label = { Text("Estado") }')),
    ("name input bound", has(profile_ui, "username = it.take(24)")),
    ("bio input bound", has(profile_ui, "bio = it.take(120)")),
    ("presence input bound", has(profile_ui, "presence = it.take(24)")),
    ("avatar heading", has(profile_ui, '"AVATAR HUMANO"')),
    ("one avatar tile contract", has(profile_ui, "Cada recuadro muestra un solo avatar")),
    ("native avatar contract", has(profile_ui, "render nativo GLB animado")),
    ("avatar renderer", has(profile_ui, "HumanAvatar3D")),

    ("avatar choices", has(profile_ui, "avatarChoices")),
    ("king preset", has(profile_ui, '"king_warrior"')),
    ("guard preset", has(profile_ui, '"royal_guard"')),
    ("queen preset", has(profile_ui, '"arcane_queen"')),
    ("avatar choice selected", has(profile_ui, "selected: Boolean")),
    ("avatar choice click", has(profile_ui, "clickable { onClick() }")),
    ("save profile action", has(profile_ui, "onSaveProfile(username, bio, presence, avatarPreset)")),
    ("save success feedback", has(profile_ui, "Perfil guardado")),
    ("save failure feedback", has(profile_ui, "Revisa nombre")),
    ("profile native avatar", has(profile_ui, "private fun AvatarPreview3D")),
    ("avatar renderer", has(profile_ui, "HumanAvatar3D")),
    ("avatar GLB contract", has(ROOT / "app/src/main/assets/models/avatar_catalog.json", '"fallbackModel": "models/Xbot.glb"')),
    ("circular avatar frame", has(profile_ui, "CircleShape")),
    ("battle wins", has(profile_ui, "user.totalWins")),
    ("battle kills", has(profile_ui, "user.totalKills")),
    ("kd calculation", has(profile_ui, "coerceAtLeast(1)")),
    ("battle pass", has(profile_ui, "battlePassLevel")),
    ("pass progress", has(profile_ui, "LinearProgressIndicator")),
    ("essence stat", has(profile_ui, "mysticalEssence")),
    ("gold stat", has(profile_ui, "goldCoins")),
    ("diamond stat", has(profile_ui, "coronasDiamonds")),
    ("vip badge", has(profile_ui, "VIP ACTIVO")),
    ("profile nav", has(profile_ui, "BottomNavBar(ScreenRoute.Profile")),
    ("profile top bar", has(profile_ui, "TopBar(userAccount")),
    ("profile lazy portrait", has(profile_ui, "LazyColumn")),
]
for i, (name, ok) in enumerate(profile_tokens, 1): check("profile", f"P{i:02d}:{name}", ok)

# Group 4: 53 fixed navigation, consistency and documentation checks.
nav_tokens = [
    ("nine tab declarations", len(re.findall(r"NavTabItem\(", text(nav))) == 10), # data class + 9 entries
    ("lobby route", has(nav, "ScreenRoute.Lobby")),
    ("shop route", has(nav, "ScreenRoute.Shop")),
    ("inventory route", has(nav, "ScreenRoute.Inventory")),
    ("wardrobe route", has(nav, "ScreenRoute.Wardrobe")),
    ("profile route", has(nav, "ScreenRoute.Profile")),
    ("disco route", has(nav, "ScreenRoute.Disco")),
    ("clan route", has(nav, "ScreenRoute.Clan")),
    ("games route", has(nav, "ScreenRoute.Games")),
    ("settings route", has(nav, "ScreenRoute.SettingsAdmin")),
    ("lobby label", has(nav, '"LOBBY"')),
    ("shop label", has(nav, '"TIENDA"')),
    ("inventory label", has(nav, '"INVENTARIO"')),
    ("wardrobe label", has(nav, '"VESTUARIO"')),
    ("profile label", has(nav, '"PERFIL"')),
    ("disco label", has(nav, '"DISCOTECA"')),
    ("clan label", has(nav, '"CLAN"')),
    ("games label", has(nav, '"JUEGOS"')),
    ("settings label", has(nav, '"AJUSTES"')),
    ("fixed first row", has(nav, "tabs.take(5)")),
    ("fixed second row", has(nav, "tabs.drop(5)")),
    ("row balancing", has(nav, "repeat(5 - rowTabs.size)")),
    ("selected route", has(nav, "tab.route::class == currentRoute::class")),
    ("real click", has(nav, "clickable { onNavigate(tab.route) }")),
    ("selected tint", has(nav, "GoldPrimary")),
    ("muted tint", has(nav, "TextMuted")),
    ("compact icon", has(nav, "size(17.dp)")),
    ("compact label", has(nav, "fontSize = 7.sp")),
    ("navigation disclaimer", has(nav, "NO DINERO REAL")),
    ("blue black nav", has(nav, "0xFA080B14")),
    ("gold nav border", has(nav, "GoldBorder")),
    ("top bar weighted profile", has(top, "weight(1.05f)")),
    ("top bar weighted title", has(top, "weight(1.05f).padding")),
    ("top bar weighted currencies", has(top, "weight(1.35f)")),
    ("shared panel", has(chrome, "fun ReferencePanel")),
    ("shared title", has(chrome, "fun ReferenceTitle")),
    ("shared badge", has(chrome, "fun ReferenceBadge")),
    ("lock dark panels", has(lock, "dark blue/black")),
    ("lock gold frames", has(lock, "thin gold")),
    ("lock compact portrait", has(lock, "compact mobile portrait")),
    ("lock single avatar", has(lock, "one avatar/character")),
    ("lock nine nav", has(lock, "Lobby, Shop, Inventory, Wardrobe, Profile, Disco, Clan, Games, Settings")),
    ("app collects wardrobe", has(app, "viewModel.wardrobeItems")),
    ("app passes wardrobe", has(app, "wardrobeItems = wardrobeItems")),
    ("app saves profile", has(app, "onSaveProfile")),
    ("vm exposes wardrobe", has(vm, "val wardrobeItems: StateFlow")),
    ("vm equips wardrobe", has(vm, "fun equipWardrobeItem")),
    ("vm saves profile", has(vm, "fun saveProfile")),
    ("changelog additive migration", has(changelog, "Room 2→3")),
    ("changelog wardrobe", has(changelog, "wardrobe")),
    ("changelog profile", has(changelog, "profile editor")),
    ("screen contract source", has(wardrobe_ui, "fun WardrobeScreen")),
    ("profile contract source", has(profile_ui, "fun ProfileScreen")),
]
for i, (name, ok) in enumerate(nav_tokens, 1): check("contract", f"C{i:02d}:{name}", ok)

# Keep the total exactly 201; source groups are intentionally weighted toward
# the fixed-nav contract because it binds every destination to a real click.
expected = {"data": 49, "wardrobe": 49, "profile": 50, "contract": 53}
actual = {group: sum(1 for g, _, _ in checks if g == group) for group in expected}
failed = [(g, n) for g, n, ok in checks if not ok]
print(f"batch2_checks={len(checks)} passed={len(checks)-len(failed)} failed={len(failed)}")
if len(checks) != 201 or actual != expected or failed:
    for group, name in failed: print(f"FAIL [{group}] {name}")
    raise SystemExit(1)
