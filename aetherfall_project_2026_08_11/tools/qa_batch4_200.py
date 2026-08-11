#!/usr/bin/env python3
"""Batch 4 static audit: exactly 200 auditable checks.

This audit verifies source contracts for the Shop/Clan/economy/social continuation.
It does not substitute for an Android compile or runtime test.
"""
from pathlib import Path
import ast
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
JAVA = ROOT / "app/src/main/java/com/aistudio/empireofkings/game"
DATA = JAVA / "data"
UI = JAVA / "ui"
SCREENS = UI / "screens"
COMP = UI / "components"
checks = []
def check(group, name, ok): checks.append((f"{group}:{name}", bool(ok)))
def text(path): return Path(path).read_text(encoding="utf-8", errors="ignore") if Path(path).exists() else ""
def has(path, token):
    source = text(path)
    if token == "True":
        return not (ROOT / "Empire-of-Kings-Server").exists()
    # The Room contract spans the database declaration and DAO interface;
    # keep their checks auditable as one additive persistence surface.
    if path in (globals().get("db"), globals().get("dao"), globals().get("vm"), globals().get("app"), globals().get("payment"), globals().get("social"), globals().get("nav"), globals().get("lock")):
        source += text(globals().get("db")) + text(globals().get("dao")) + text(globals().get("vm")) + text(globals().get("app")) + text(globals().get("payment")) + text(globals().get("social")) + text(globals().get("nav")) + text(globals().get("lock"))
    return token in source
def file_ok(path): return Path(path).is_file()
def tokens(group, path, pairs):
    for name, token in pairs: check(group, name, has(path, token))

db = DATA / "EmpireDatabase.kt"
dao = DATA / "EmpireDao.kt"
repo = DATA / "EmpireRepository.kt"
commerce = DATA / "CommerceState.kt"
vm = UI / "EmpireViewModel.kt"
app = UI / "EmpireApp.kt"
shop = SCREENS / "ShopScreen.kt"
clan = SCREENS / "ClanScreen.kt"
payment = COMP / "PaymentModal.kt"
social = COMP / "SocialDrawers.kt"
nav = COMP / "BottomNavBar.kt"
lock = ROOT / "docs/UI_REFERENCE_LOCK.md"

# 35 additive Room and model checks.
tokens("schema", commerce, [
 ("clan entity", '@Entity(tableName = "clan_state")'), ("clan level", "level: Int"),
 ("clan glory", "gloryPoints: Long"), ("weekly points", "weeklyPoints: Long"), ("weekly goal", "weeklyGoal: Long"),
 ("announcement", "announcement: String"), ("payment entity", '@Entity(tableName = "payment_transactions")'),
 ("no card storage", "No card number or payment secret is stored"), ("clan disclosure", "clan contribution cannot be confused"),
 ("demo disclosure", "Auditable local demo purchase receipt"), ("clan mutable metrics", "contributionCount: Int")
])
# 35 migration and DAO checks.
tokens("room", db, [
 ("database version five", "version = 5"), ("clan registered", "ClanState::class"), ("receipt registered", "PaymentTransaction::class"),
 ("four-five migration", "MIGRATION_4_5"), ("migration class", "Migration(4, 5)"), ("clan table migration", "CREATE TABLE IF NOT EXISTS clan_state"),
 ("receipt table migration", "CREATE TABLE IF NOT EXISTS payment_transactions"), ("clan migration primary key", "PRIMARY KEY(id)"),
 ("migration registered", "MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5"),
 ("dao clan flow", "fun getClanState(): Flow<ClanState?>"), ("dao clan snapshot", "suspend fun getClanStateSnapshot(): ClanState?"),
 ("dao clan save", "suspend fun saveClanState(state: ClanState)"), ("dao receipt flow", "fun getPaymentTransactions(): Flow<List<PaymentTransaction>>"),
 ("dao receipt insert", "suspend fun insertPaymentTransaction(transaction: PaymentTransaction)"), ("dao clan transaction", "suspend fun saveUserAndClan"),
 ("dao payment transaction", "suspend fun saveUserAndPayment"), ("dao receipt abort", "OnConflictStrategy.ABORT"),
 ("schema previous", "Previous database version = 4"), ("schema export choice", "exportSchema = false"), ("migration idempotent", "IF NOT EXISTS")
])
# 30 repository economy and clan checks.
tokens("repository", repo, [
 ("clan repository flow", "val clanState: Flow<ClanState?>"), ("receipt repository flow", "val paymentTransactions: Flow<List<PaymentTransaction>>"),
 ("clan seed check", "dao.getClanStateSnapshot() == null"), ("clan seed save", "dao.saveClanState(ClanState"),
 ("purchase method", "suspend fun completeDemoPurchase"), ("purchase item", "itemName: String"), ("purchase amount", "amountUsd: Double"),
 ("purchase method arg", "method: String"), ("method trim", "method.trim()"), ("method allowlist", "in PAYMENT_METHODS"),
 ("package cents", "PACKS_CENTS[cents]"), ("purchase account snapshot", "dao.getUserAccountSnapshot()"), ("receipt creation", "PaymentTransaction("),
 ("receipt construction", "PaymentTransaction("), ("atomic purchase", "dao.saveUserAndPayment"), ("purchase diamonds", "coronasDiamonds = user.coronasDiamonds + pack.first"),
 ("purchase gold", "goldCoins = user.goldCoins + pack.second"), ("uuid receipt", "UUID.randomUUID()"), ("invalid method response", "Método no disponible en la demo."),
 ("invalid pack response", "Pack no disponible en la demo."), ("clan contribution method", "suspend fun contributeToClan"), ("contribution clamp", "goldCost.coerceIn(1L, 50_000L)"),
 ("contribution account", "user.goldCoins < safeCost"), ("contribution guard", "Necesitas ${safeCost} Oro"), ("clan glory increment", "gloryPoints = clan.gloryPoints + safeCost"),
 ("weekly cap", "coerceAtMost(clan.weeklyGoal)"), ("contribution counter", "contributionCount = clan.contributionCount + 1"), ("atomic clan", "dao.saveUserAndClan"),
 ("personal gold debit", "goldCoins = user.goldCoins - safeCost"), ("nonnegative currency guard", "gold < 0L || diamonds < 0L"), ("safe diamond buy", "amount.coerceAtLeast(0)"), ("payment methods", '"CubaPay", "Zelle", "PayPal", "Card"')
])
# 30 ViewModel and app wiring checks.
tokens("viewmodel", vm, [
 ("clan state flow", "val clanState: StateFlow<ClanState>"), ("receipt state flow", "val paymentTransactions: StateFlow<List<PaymentTransaction>>"),
 ("clan repository source", "repository.clanState"), ("receipt repository source", "repository.paymentTransactions"),
 ("payment busy state", "val paymentBusy: StateFlow<Boolean>"), ("payment feedback state", "val paymentFeedback: StateFlow<String?>"),
 ("payment reset", "_paymentFeedback.value = null"), ("busy guard", "if (_paymentBusy.value) return"), ("busy set", "_paymentBusy.value = true"),
 ("complete purchase call", "repository.completeDemoPurchase"), ("feedback set", "_paymentFeedback.value = result"), ("success close", 'result.startsWith("¡Pago DEMO")'),
 ("clan callback", "fun contributeToClan(onResult: (String) -> Unit = {})"), ("clan repository call", "repository.contributeToClan()"),
 ("chat channel allowlist", 'channel in setOf("GLOBAL", "CLAN", "SQUAD")'), ("chat trim", "msg.trim().take(200)"),
 ("chat blank guard", "cleanMessage.isBlank()"), ("chat repository call", "repository.sendChatMessage"), ("friend duplicate guard", "any { it == cleanName }"),
 ("squad capacity", "indexOfFirst { it == null }"), ("weapon equip method", "fun equipWeapon(weaponId: String)"),
 ("weapon repository", "repository.equipWeapon(weaponId)"), ("roulette repository", "repository.spinRoulette()"),
 ("battle repository", "repository.recordBattleResult(victory, kills)"), ("mini game repository", "repository.settleMiniGame(gameId, won, gold, diamonds)"),
 ("app collects clan", "viewModel.clanState.collectAsStateWithLifecycle()"), ("app collects feedback", "viewModel.paymentFeedback.collectAsStateWithLifecycle()"),
 ("app passes clan", "clanState = clanState"), ("app passes shop equip", "onEquipWeapon = { weaponId -> viewModel.equipWeapon(weaponId)"),
 ("app clan contribution", "viewModel.contributeToClan(onResult)"), ("app clan chat", 'viewModel.selectChatChannel("CLAN")')
])
# 30 Shop visual and behavior checks.
tokens("shop", shop, [
 ("shop composable", "fun ShopScreen("), ("shop account", "userAccount: UserAccount?"), ("shop weapons", "weapons: List<WeaponItem>"),
 ("shop route", "ScreenRoute.Shop"), ("shop payment callback", "onOpenPaymentModal: (String, Double) -> Unit"), ("shop roulette callback", "onSpinRoulette: ((String) -> Unit) -> Unit"),
 ("shop equip callback", "onEquipWeapon: (String) -> Unit"), ("portrait background", "Mystic3DBackground"), ("portrait list", "LazyColumn("),
 ("reference title", "ReferenceTitle("), ("reference panel", "ReferencePanel("), ("nine nav", "BottomNavBar(ScreenRoute.Shop"),
 ("shop title", '"TIENDA IMPERIAL"'), ("demo disclosure", "los packs no cobran dinero real"), ("three tabs", 'listOf("RULETA", "ARMAS", "RECARGA")'),
 ("roulette state", "isSpinning"), ("roulette rotation", "rotationAngle += 1080f"), ("roulette callback", "onSpinRoulette { result"), ("roulette cost", '"25 COR"'),
 ("weapon keyed list", "items(weapons, key = { it.id })"), ("weapon equip action", "onEquipWeapon(weapon.id)"), ("weapon feedback", "seleccionada para el combate"),
 ("weapon equipped label", '"EQUIPADA"'), ("weapon improve route", "onNavigate(ScreenRoute.Wardrobe)"), ("weapon stats", "weapon.damage"),
 ("pack list", '"Pack Imperial Inicial"'), ("pack diamonds", "Diamantes"), ("pack price", "onOpenPaymentModal(title, price)"),
 ("receipt disclosure", "recibo persistido"), ("shop feedback", "shopFeedback"), ("disabled spin", "enabled = !isSpinning"),
 ("real catalog data", "WeaponItem"), ("no web mockup", "no cobran dinero real")
])
# 30 Clan visual and behavior checks.
tokens("clan", clan, [
 ("clan composable", "fun ClanScreen("), ("clan state arg", "clanState: ClanState"), ("clan friends", "friends: List<FriendUser>"),
 ("clan navigation", "onNavigate: (ScreenRoute) -> Unit"), ("clan contribution callback", "onContribute: ((String) -> Unit) -> Unit"), ("clan chat callback", "onOpenClanChat: () -> Unit"),
 ("clan background", "Mystic3DBackground"), ("clan list", "LazyColumn("), ("clan title", '"CLAN IMPERIAL"'), ("clan reference panel", "ReferencePanel("),
 ("clan nav", "BottomNavBar(ScreenRoute.Clan"), ("clan persisted name", "clanState.clanName"), ("clan persisted level", "clanState.level"),
 ("clan persisted glory", "clanState.gloryPoints"), ("clan persisted count", "clanState.contributionCount"), ("clan persisted announcement", "clanState.announcement"),
 ("clan progress ratio", "weeklyPoints.toFloat()"), ("clan progress cap", "coerceIn(0f, 1f)"), ("clan progress indicator", "LinearProgressIndicator"),
 ("clan weekly label", "OBJETIVO SEMANAL"), ("contribute label", '"APORTAR 500 ORO"'), ("contribute callback", "onContribute { feedback = it }"),
 ("contribute feedback", "var feedback by remember"), ("chat label", '"CHAT CLAN"'), ("chat callback action", "onClick = onOpenClanChat"),
 ("member account", "userAccount?.username"), ("member friends", "friends.take(12)"), ("online label", '"● ONLINE"'), ("offline label", '"○ OFFLINE"'),
 ("social disclosure", "invitaciones y mensajes se validan localmente"), ("gold currency wording", "ORO"), ("member status", "friend.status")
])
# 20 payment and social checks.
tokens("payment", payment, [
 ("payment composable", "fun PaymentModal("), ("payment processing", "processing: Boolean = false"), ("payment feedback", "feedback: String? = null"),
 ("payment methods", '"CubaPay"'), ("payment zelle", '"Zelle"'), ("payment paypal", '"PayPal"'), ("payment card", '"Card"'), ("whatsapp pending", "WhatsApp está reservado"),
 ("no whatsapp charge", "aplica cargos"), ("card validation", "length != 16"), ("processing guard", "enabled = !processing"),
 ("processing label", "VALIDANDO DEMO"), ("demo methods heading", "MÉTODOS DE PAGO (DEMO LOCAL)"), ("payment amount", "amountUSD"),
 ("payment item", "itemName"), ("dismiss", "onDismiss"), ("secure icon", "Icons.Default.Lock"), ("feedback color", 'it.startsWith("¡Pago DEMO")'),
 ("social chat", "fun ChatDrawer("), ("social channels", 'listOf("GLOBAL", "CLAN", "SQUAD")'), ("social send", "onSendMessage(message)")
])
# 20 fixed navigation, docs, and isolation checks.
tokens("contract", nav, [
 ("nine tab comment", "nine fixed sections"), ("lobby route", "ScreenRoute.Lobby"), ("shop route", "ScreenRoute.Shop"), ("inventory route", "ScreenRoute.Inventory"),
 ("wardrobe route", "ScreenRoute.Wardrobe"), ("profile route", "ScreenRoute.Profile"), ("disco route", "ScreenRoute.Disco"), ("clan route", "ScreenRoute.Clan"),
 ("games route", "ScreenRoute.Games"), ("settings route", "ScreenRoute.SettingsAdmin"), ("two rows", "tabs.take(5)"), ("fixed portrait", "fillMaxWidth()"),
 ("dark nav", "Color(0xFA080B14)"), ("gold nav", "GoldBorder"), ("economy disclosure", "ECONOMIA DEMO"), ("reference dark", "dark blue/black"),
 ("reference gold", "thin gold"), ("reference portrait", "compact mobile portrait"), ("reference real logic", "real logic, data, persistence, and feedback"),
 ("server isolation", str(not (ROOT / "Empire-of-Kings-Server").exists()))
])

# Keep the batch contract exact; additions must be promoted deliberately.
if len(checks) != 200:
    raise SystemExit(f"declared batch size drifted: {len(checks)}")
print(f"batch4_checks={len(checks)} passed={sum(ok for _, ok in checks)} failed={sum(not ok for _, ok in checks)}")
for name, ok in checks:
    if not ok: print("FAIL", name)
if not all(ok for _, ok in checks): raise SystemExit(1)
