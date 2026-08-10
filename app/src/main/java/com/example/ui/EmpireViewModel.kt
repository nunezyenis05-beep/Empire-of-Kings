package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenRoute {
    object Splash : ScreenRoute()
    object Auth : ScreenRoute()
    object Lobby : ScreenRoute()
    object Shop : ScreenRoute()
    object Wardrobe : ScreenRoute()
    object Disco : ScreenRoute()
    object Games : ScreenRoute()
    object Inventory : ScreenRoute()
    object Profile : ScreenRoute()
    object SettingsAdmin : ScreenRoute()
    object Battle : ScreenRoute()
    object Kingdom : ScreenRoute()
}

class EmpireViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EmpireRepository

    init {
        val dao = EmpireDatabase.getInstance(application).empireDao()
        repository = EmpireRepository(dao)
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    val userAccount: StateFlow<UserAccount?> = repository.userAccount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weapons: StateFlow<List<WeaponItem>> = repository.weapons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryItem>> = repository.inventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friends: StateFlow<List<FriendUser>> = repository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentScreen = MutableStateFlow<ScreenRoute>(ScreenRoute.Lobby)
    val currentScreen: StateFlow<ScreenRoute> = _currentScreen.asStateFlow()

    private val _selectedChannel = MutableStateFlow("GLOBAL")
    val selectedChannel: StateFlow<String> = _selectedChannel.asStateFlow()

    val chatMessages: StateFlow<List<ChatMessage>> = _selectedChannel
        .flatMapLatest { channel -> repository.getChatMessages(channel) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Squad members state (up to 6 slots)
    private val _squadSlots = MutableStateFlow<List<String?>>(listOf("KING_PLAYER", null, null, null, null, null))
    val squadSlots: StateFlow<List<String?>> = _squadSlots.asStateFlow()

    // Active dance/emote action state
    private val _activeActionText = MutableStateFlow<String?>(null)
    val activeActionText: StateFlow<String?> = _activeActionText.asStateFlow()

    // Kingdom Map State Flow
    val kingdomState: StateFlow<KingdomGameState> = repository.kingdomState
        .map { entity -> KingdomStateEntity.toGameState(entity) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), KingdomStateEntity.createDefaultState())

    fun saveKingdom(state: KingdomGameState) {
        viewModelScope.launch {
            repository.saveKingdomState(state)
        }
    }

    fun resetKingdom() {
        viewModelScope.launch {
            repository.saveKingdomState(KingdomStateEntity.createDefaultState())
        }
    }

    // Payment modal state
    private val _showPaymentModal = MutableStateFlow(false)
    val showPaymentModal: StateFlow<Boolean> = _showPaymentModal.asStateFlow()

    private val _paymentItemName = MutableStateFlow("1,000 Diamantes Imperiales")
    val paymentItemName: StateFlow<String> = _paymentItemName.asStateFlow()

    private val _paymentAmountUSD = MutableStateFlow(9.99)
    val paymentAmountUSD: StateFlow<Double> = _paymentAmountUSD.asStateFlow()

    fun navigateTo(screen: ScreenRoute) {
        _currentScreen.value = screen
    }

    fun selectChatChannel(channel: String) {
        _selectedChannel.value = channel
    }

    fun sendChatMessage(msg: String) {
        if (msg.isBlank()) return
        val sender = userAccount.value?.username ?: "KING_PLAYER"
        viewModelScope.launch {
            repository.sendChatMessage(sender, msg, _selectedChannel.value)
        }
    }

    fun triggerEmote(actionName: String) {
        _activeActionText.value = actionName
        viewModelScope.launch {
            kotlinx.coroutines.delay(3000)
            if (_activeActionText.value == actionName) {
                _activeActionText.value = null
            }
        }
    }

    fun inviteFriendToSquad(friendName: String) {
        val current = _squadSlots.value.toMutableList()
        val emptyIndex = current.indexOfFirst { it == null }
        if (emptyIndex != -1) {
            current[emptyIndex] = friendName
            _squadSlots.value = current
        }
    }

    fun removeSquadMember(index: Int) {
        if (index <= 0) return // cannot remove leader
        val current = _squadSlots.value.toMutableList()
        current[index] = null
        _squadSlots.value = current
    }

    fun equipWeapon(weaponId: String) {
        viewModelScope.launch {
            repository.equipWeapon(weaponId)
        }
    }

    fun upgradeWeapon(weapon: WeaponItem, onResult: (Boolean, String) -> Unit) {
        val user = userAccount.value ?: return
        viewModelScope.launch {
            val success = repository.upgradeWeapon(weapon, user)
            if (success) {
                onResult(true, "¡Arma mejorada al Nivel ${weapon.level + 1}!")
            } else {
                onResult(false, "Insuficientes recursos (Diamantes, Oro o Esencia Mística).")
            }
        }
    }

    fun openPaymentModal(itemName: String, priceUSD: Double) {
        _paymentItemName.value = itemName
        _paymentAmountUSD.value = priceUSD
        _showPaymentModal.value = true
    }

    fun closePaymentModal() {
        _showPaymentModal.value = false
    }

    fun confirmPayment(method: String, onComplete: (String) -> Unit) {
        val currentUser = userAccount.value ?: return
        viewModelScope.launch {
            val addedDiamonds = when (_paymentAmountUSD.value) {
                4.99 -> 500L
                9.99 -> 1200L
                24.99 -> 3500L
                49.99 -> 8000L
                else -> 1000L
            }
            val updatedUser = currentUser.copy(
                coronasDiamonds = currentUser.coronasDiamonds + addedDiamonds,
                goldCoins = currentUser.goldCoins + 50000L
            )
            repository.saveUser(updatedUser)
            _showPaymentModal.value = false
            onComplete("¡Pago exitoso vía $method! Se añadieron $addedDiamonds Diamantes y 50,000 Oro.")
        }
    }

    fun addAdminCurrencies(gold: Long, diamonds: Long) {
        val user = userAccount.value ?: return
        viewModelScope.launch {
            val updated = user.copy(
                goldCoins = user.goldCoins + gold,
                coronasDiamonds = user.coronasDiamonds + diamonds
            )
            repository.saveUser(updated)
        }
    }
}
