package com.example.ui

import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PaymentModal
import com.example.ui.screens.*

@Composable
fun EmpireApp(
    viewModel: EmpireViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val userAccount by viewModel.userAccount.collectAsStateWithLifecycle()
    val weapons by viewModel.weapons.collectAsStateWithLifecycle()
    val inventory by viewModel.inventory.collectAsStateWithLifecycle()
    val friends by viewModel.friends.collectAsStateWithLifecycle()
    val selectedChannel by viewModel.selectedChannel.collectAsStateWithLifecycle()
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val squadSlots by viewModel.squadSlots.collectAsStateWithLifecycle()
    val activeActionText by viewModel.activeActionText.collectAsStateWithLifecycle()

    val showPaymentModal by viewModel.showPaymentModal.collectAsStateWithLifecycle()
    val paymentItemName by viewModel.paymentItemName.collectAsStateWithLifecycle()
    val paymentAmountUSD by viewModel.paymentAmountUSD.collectAsStateWithLifecycle()

    when (currentScreen) {
        is ScreenRoute.Splash -> {
            SplashScreen(
                onSplashFinished = { viewModel.navigateTo(ScreenRoute.Lobby) }
            )
        }

        is ScreenRoute.Auth -> {
            AuthScreen(
                onAuthSuccess = { viewModel.navigateTo(ScreenRoute.Lobby) }
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
                onOpenPaymentModal = { viewModel.openPaymentModal("Pack de Diamantes", 9.99) }
            )
        }

        is ScreenRoute.Wardrobe -> {
            WardrobeScreen(
                userAccount = userAccount,
                weapons = weapons,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onEquipWeapon = { id -> viewModel.equipWeapon(id) },
                onUpgradeWeapon = { wpn, callback -> viewModel.upgradeWeapon(wpn, callback) }
            )
        }

        is ScreenRoute.Disco -> {
            DiscoScreen(
                userAccount = userAccount,
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }

        is ScreenRoute.Games -> {
            GamesScreen(
                userAccount = userAccount,
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }

        is ScreenRoute.Inventory -> {
            InventoryScreen(
                userAccount = userAccount,
                inventoryItems = inventory,
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }

        is ScreenRoute.Profile -> {
            ProfileScreen(
                userAccount = userAccount,
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }

        is ScreenRoute.SettingsAdmin -> {
            SettingsAndAdminScreen(
                userAccount = userAccount,
                onNavigate = { route -> viewModel.navigateTo(route) },
                onAddAdminCurrencies = { g, d -> viewModel.addAdminCurrencies(g, d) }
            )
        }

        is ScreenRoute.Battle -> {
            BattleRoyaleScreen(
                onNavigate = { route -> viewModel.navigateTo(route) }
            )
        }
    }

    if (showPaymentModal) {
        PaymentModal(
            itemName = paymentItemName,
            amountUSD = paymentAmountUSD,
            onDismiss = { viewModel.closePaymentModal() },
            onConfirmPayment = { method ->
                viewModel.confirmPayment(method) { msg ->
                    // Payment complete feedback
                }
            }
        )
    }
}
