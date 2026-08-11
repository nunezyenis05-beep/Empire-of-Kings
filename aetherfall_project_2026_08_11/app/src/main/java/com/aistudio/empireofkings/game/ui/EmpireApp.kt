package com.aistudio.empireofkings.game.ui

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aistudio.empireofkings.game.ui.components.PaymentModal
import com.aistudio.empireofkings.game.ui.screens.*

@Composable
fun EmpireApp(
    viewModel: EmpireViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
    val weapons by viewModel.weapons.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val wardrobeItems by viewModel.wardrobeItems.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val appSettings by viewModel.appSettings.collectAsStateWithLifecycle()
    val discoState by viewModel.discoState.collectAsStateWithLifecycle()
    val miniGameProgress by viewModel.miniGameProgress.collectAsStateWithLifecycle()
    val clanState by viewModel.clanState.collectAsStateWithLifecycle()
    val selectedChannel by viewModel.selectedChannel.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val squadSlots by viewModel.squadSlots.collectAsStateWithLifecycle()
    val activeActionText by viewModel.activeActionText.collectAsStateWithLifecycle()
    val serverStatus by viewModel.serverStatus.collectAsStateWithLifecycle()
    val onlineSessionStatus by viewModel.onlineSessionStatus.collectAsStateWithLifecycle()
    val matchPlayers by viewModel.matchPlayers.collectAsStateWithLifecycle()
    val readyPlayers by viewModel.readyPlayers.collectAsStateWithLifecycle()
    val remotePlayers by viewModel.remotePlayers.collectAsStateWithLifecycle()
    val remoteActions by viewModel.remoteActions.collectAsStateWithLifecycle()
    val remoteLoadouts by viewModel.remoteLoadouts.collectAsStateWithLifecycle()
    val equippedWeaponName = weapons.firstOrNull { it.isEquipped }?.name ?: "Corona del Rey"
    val localPlayerId = userAccount?.username?.trim().orEmpty().ifBlank { "KING_PLAYER" }

    val showPaymentModal by viewModel.showPaymentModal.collectAsStateWithLifecycle()
    val paymentItemName by viewModel.paymentItemName.collectAsStateWithLifecycle()
    val paymentAmountUSD by viewModel.paymentAmountUSD.collectAsStateWithLifecycle()
    val paymentBusy by viewModel.paymentBusy.collectAsStateWithLifecycle()
    val paymentFeedback by viewModel.paymentFeedback.collectAsStateWithLifecycle()

    when (currentScreen) {
        is ScreenRoute.Splash -> {
            SplashScreen(
                onSplashFinished = { viewModel.finishSplash() }
            )
        }

        is ScreenRoute.Auth -> {
            AuthScreen(
                onAuthSuccess = { username, password, register, avatarPreset ->
                    viewModel.completeAuth(username, password, register, avatarPreset)
                }
            )
        }

        is ScreenRoute.Lobby -> {
            MainLobbyScreen(
                userAccount = userAccount,
                squadSlots = squadSlots,
                friends = friends,
                selectedChannel = selectedChannel,
                chatMessages = chatMessages,
                activeActionText = activeActionText,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) },
                onInviteFriend = { friendName -> viewModel.inviteFriendToSquad(friendName) },
                onRemoveSquadMember = { index -> viewModel.removeSquadMember(index) },
                onSelectChatChannel = { channel -> viewModel.selectChatChannel(channel) },
                onSendChatMessage = { msg -> viewModel.sendChatMessage(msg) },
                onTriggerAction = { action -> viewModel.triggerEmote(action) }
            )
        }

        is ScreenRoute.Shop -> {
            ShopScreen(
                userAccount = userAccount,
                weapons = weapons,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { itemName, price -> viewModel.openPaymentModal(itemName, price) },
                onSpinRoulette = { onResult -> viewModel.spinRoulette(onResult) },
                onEquipWeapon = { weaponId -> viewModel.equipWeapon(weaponId) }
            )
        }

        is ScreenRoute.Wardrobe -> {
            WardrobeScreen(
                userAccount = userAccount,
                weapons = weapons,
                wardrobeItems = wardrobeItems,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) },
                onEquipWeapon = { id -> viewModel.equipWeapon(id) },
                onEquipWardrobeItem = { id -> viewModel.equipWardrobeItem(id) },
                onUpgradeWeapon = { wpn, callback -> viewModel.upgradeWeapon(wpn, callback) }
            )
        }

        is ScreenRoute.Disco -> {
            DiscoScreen(
                userAccount = userAccount,
                discoState = discoState,
                musicEnabled = appSettings.musicEnabled,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) },
                onSelectTrack = { trackId -> viewModel.saveDiscoTrack(trackId) },
                onSelectEmote = { emoteId -> viewModel.triggerDiscoEmote(emoteId) }
            )
        }

        is ScreenRoute.Games -> {
            GamesScreen(
                userAccount = userAccount,
                progress = miniGameProgress,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) },
                onSelectGame = { gameId -> viewModel.selectMiniGame(gameId) },
                onSettleGame = { gameId, won, gold, diamonds -> viewModel.settleMiniGame(gameId, won, gold, diamonds) }
            )
        }

        is ScreenRoute.Inventory -> {
            InventoryScreen(
                userAccount = userAccount,
                inventoryItems = inventory,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) }
            )
        }

        is ScreenRoute.Profile -> {
            ProfileScreen(
                userAccount = userAccount,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) },
                onSaveProfile = { name, bio, presence, avatar, result ->
                    viewModel.saveProfile(name, bio, presence, avatar, result)
                }
            )
        }

        is ScreenRoute.Clan -> {
            ClanScreen(
                userAccount = userAccount,
                clanState = clanState,
                friends = friends,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) },
                onContribute = { onResult -> viewModel.contributeToClan(onResult) },
                onOpenClanChat = { viewModel.selectChatChannel("CLAN"); viewModel.navigateTo(ScreenRoute.Lobby) }
            )
        }

        is ScreenRoute.SettingsAdmin -> {
            SettingsAndAdminScreen(
                userAccount = userAccount,
                appSettings = appSettings,
                onSettingsChanged = { settings -> viewModel.updateAppSettings(settings) },
                onNavigate = { route -> viewModel.navigateTo(route) },
                onOpenPaymentModal = { viewModel.openPaymentModal("1,000 Diamantes Imperiales", 9.99) },
                onAddAdminCurrencies = { g, d -> viewModel.addAdminCurrencies(g, d) },
                onSignOut = { viewModel.signOut() },
                serverStatus = serverStatus,
                onCheckServer = { viewModel.checkServerStatus() },
                onlineSessionStatus = onlineSessionStatus,
                matchPlayers = matchPlayers
            )
        }

        is ScreenRoute.Battle -> {
            BattleRoyaleScreen(
                onNavigate = { route -> viewModel.navigateTo(route) },
                onBattleFinished = { victory, kills -> viewModel.recordBattleResult(victory, kills) },
                remotePlayers = remotePlayers,
                remoteActions = remoteActions,
                remoteLoadouts = remoteLoadouts,
                onlineSessionStatus = onlineSessionStatus,
                serverStatus = serverStatus,
                matchPlayers = matchPlayers,
                readyPlayers = readyPlayers,
                localPlayerId = localPlayerId,
                localAvatarPreset = userAccount?.avatarPreset ?: "king_warrior",
                equippedWeaponName = equippedWeaponName,
                onOnlineMove = { x, y, running -> viewModel.sendOnlineMove(x, y, running) },
                onOnlineAction = { action -> viewModel.sendOnlineAction(action) },
                onOnlineReady = { isReady -> viewModel.sendOnlineReady(isReady) },
                onRetryOnline = { viewModel.retryOnlineConnection() },
                onCancelOnlineSearch = { viewModel.cancelOnlineSearch() }
            )
        }
    }

    if (showPaymentModal) {
        PaymentModal(
            itemName = paymentItemName,
            amountUSD = paymentAmountUSD,
            processing = paymentBusy,
            feedback = paymentFeedback,
            onDismiss = { viewModel.closePaymentModal() },
            onConfirmPayment = { method ->
                viewModel.confirmPayment(method) { msg ->
                    // Payment complete feedback
                }
            }
        )
    }
}
