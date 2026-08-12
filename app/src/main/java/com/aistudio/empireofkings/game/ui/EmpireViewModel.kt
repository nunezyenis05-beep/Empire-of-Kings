package com.aistudio.empireofkings.game.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.aistudio.empireofkings.game.data.*
import com.aistudio.empireofkings.game.data.remote.EmpireRemoteClient
import com.aistudio.empireofkings.game.data.remote.EmpireSocketClient
import com.aistudio.empireofkings.game.data.remote.OnlineActionNames
import com.aistudio.empireofkings.game.data.remote.OnlineSessionStatus
import com.aistudio.empireofkings.game.data.remote.PlayerLoadout
import com.aistudio.empireofkings.game.data.remote.RemoteActionEvent
import com.aistudio.empireofkings.game.data.remote.RemotePlayerLoadout
import com.aistudio.empireofkings.game.data.remote.RemotePlayerSnapshot
import com.aistudio.empireofkings.game.data.remote.ServerConnectionStatus
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
    object Clan : ScreenRoute()
    object SettingsAdmin : ScreenRoute()
    object Battle : ScreenRoute()
}

class EmpireViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: EmpireRepository
    private val remoteClient = EmpireRemoteClient()
    private val socketClient = EmpireSocketClient()
    private val sessionPreferences = application.getSharedPreferences("empire_session", android.content.Context.MODE_PRIVATE)

    private val _serverStatus = MutableStateFlow(ServerConnectionStatus.CHECKING)
    val serverStatus: StateFlow<ServerConnectionStatus> = _serverStatus.asStateFlow()

    private val _onlineSessionStatus = MutableStateFlow(OnlineSessionStatus.DISCONNECTED)
    val onlineSessionStatus: StateFlow<OnlineSessionStatus> = _onlineSessionStatus.asStateFlow()

    private val _remotePlayers = MutableStateFlow<Map<String, RemotePlayerSnapshot>>(emptyMap())
    val remotePlayers: StateFlow<List<RemotePlayerSnapshot>> = _remotePlayers
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _matchPlayers = MutableStateFlow<List<String>>(emptyList())
    val matchPlayers: StateFlow<List<String>> = _matchPlayers.asStateFlow()

    private val _readyPlayers = MutableStateFlow<Set<String>>(emptySet())
    val readyPlayers: StateFlow<Set<String>> = _readyPlayers.asStateFlow()

    private val _remoteActions = MutableStateFlow<Map<String, RemoteActionEvent>>(emptyMap())
    val remoteActions: StateFlow<List<RemoteActionEvent>> = _remoteActions
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _remoteLoadouts = MutableStateFlow<Map<String, RemotePlayerLoadout>>(emptyMap())
    val remoteLoadouts: StateFlow<List<RemotePlayerLoadout>> = _remoteLoadouts
        .map { it.values.toList() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        val dao = EmpireDatabase.getInstance(application).empireDao()
        repository = EmpireRepository(dao)
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
            repository.initializeCatalogAdminStates()
        }
        checkServerStatus()
    }

    fun checkServerStatus() {
        _serverStatus.value = ServerConnectionStatus.CHECKING
        viewModelScope.launch {
            _serverStatus.value = if (remoteClient.checkHealth()) {
                ServerConnectionStatus.ONLINE
            } else {
                ServerConnectionStatus.OFFLINE
            }
        }
    }

    val userAccount: StateFlow<UserAccount?> = repository.userAccount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val weapons: StateFlow<List<WeaponItem>> = repository.weapons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val inventory: StateFlow<List<InventoryItem>> = repository.inventory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wardrobeItems: StateFlow<List<WardrobeItem>> = repository.wardrobeItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val friends: StateFlow<List<FriendUser>> = repository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val appSettings: StateFlow<AppSettings> = repository.settings
        .map { it ?: AppSettings() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AppSettings())

    val discoState: StateFlow<DiscoState> = repository.discoState
        .map { it ?: DiscoState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DiscoState())

    val miniGameProgress: StateFlow<MiniGameProgress> = repository.miniGameProgress
        .map { it ?: MiniGameProgress() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MiniGameProgress())

    val clanState: StateFlow<ClanState> = repository.clanState
        .map { it ?: ClanState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ClanState())

    val paymentTransactions: StateFlow<List<PaymentTransaction>> = repository.paymentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val catalogAdminEntries: StateFlow<List<CatalogAdminEntry>> = combine(
        repository.catalogAdminStates,
        inventory
    ) { states, localInventory ->
        val byId = states.associateBy { it.itemId }
        val assigned = localInventory.map { it.id.removePrefix("inventory_") }.toSet()
        EquipmentCatalog.all.map { definition ->
            val state = byId[definition.id]
            CatalogAdminEntry(
                definition = definition,
                availability = state?.availability?.let { value -> runCatching { CatalogAvailability.valueOf(value) }.getOrDefault(CatalogAvailability.ASSIGNABLE) } ?: CatalogAvailability.ASSIGNABLE,
                publishedForDiamondSale = state?.publishedForDiamondSale ?: false,
                assignedLocally = definition.id in assigned
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun assignCatalogItems(itemIds: List<String>, onResult: (String) -> Unit = {}) = viewModelScope.launch { onResult(repository.assignCatalogItems(itemIds)) }
    fun publishCatalogItems(itemIds: List<String>, onResult: (String) -> Unit = {}) = viewModelScope.launch { onResult(repository.publishCatalogItems(itemIds)) }
    fun retireCatalogItems(itemIds: List<String>, onResult: (String) -> Unit = {}) = viewModelScope.launch { onResult(repository.retireCatalogItems(itemIds)) }

    private val _currentScreen = MutableStateFlow<ScreenRoute>(ScreenRoute.Splash)
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

    // Payment modal state
    private val _showPaymentModal = MutableStateFlow(false)
    val showPaymentModal: StateFlow<Boolean> = _showPaymentModal.asStateFlow()

    private val _paymentItemName = MutableStateFlow("1,000 Diamantes Imperiales")
    val paymentItemName: StateFlow<String> = _paymentItemName.asStateFlow()

    private val _paymentAmountUSD = MutableStateFlow(9.99)
    val paymentAmountUSD: StateFlow<Double> = _paymentAmountUSD.asStateFlow()

    private val _paymentBusy = MutableStateFlow(false)
    val paymentBusy: StateFlow<Boolean> = _paymentBusy.asStateFlow()
    private val _paymentFeedback = MutableStateFlow<String?>(null)
    val paymentFeedback: StateFlow<String?> = _paymentFeedback.asStateFlow()

    fun navigateTo(screen: ScreenRoute) {
        val leavingBattle = _currentScreen.value is ScreenRoute.Battle && screen !is ScreenRoute.Battle
        if (leavingBattle) {
            socketClient.disconnect()
            _onlineSessionStatus.value = OnlineSessionStatus.DISCONNECTED
            _remotePlayers.value = emptyMap()
            _matchPlayers.value = emptyList()
            _readyPlayers.value = emptySet()
            _remoteActions.value = emptyMap()
            _remoteLoadouts.value = emptyMap()
        }
        _currentScreen.value = screen
        if (screen is ScreenRoute.Battle) {
            _matchPlayers.value = emptyList()
            _readyPlayers.value = emptySet()
            val playerId = userAccount.value?.username?.trim().orEmpty().ifBlank { "KING_PLAYER" }
            if (!socketClient.isConnected()) {
                connectOnlinePlayer(playerId)
            }
            socketClient.joinMatchmaking()
        }
    }

    fun finishSplash() {
        _currentScreen.value = if (sessionPreferences.getBoolean("authenticated", false)) {
            ScreenRoute.Lobby
        } else {
            ScreenRoute.Auth
        }
    }

    fun retryOnlineConnection() {
        checkServerStatus()
        val playerId = userAccount.value?.username?.trim().orEmpty().ifBlank { "KING_PLAYER" }
        socketClient.disconnect()
        _onlineSessionStatus.value = OnlineSessionStatus.DISCONNECTED
        _remotePlayers.value = emptyMap()
        _matchPlayers.value = emptyList()
        _readyPlayers.value = emptySet()
        _remoteActions.value = emptyMap()
        _remoteLoadouts.value = emptyMap()
        connectOnlinePlayer(playerId)
        socketClient.joinMatchmaking()
    }

    fun cancelOnlineSearch() {
        socketClient.cancelMatchmaking()
        _onlineSessionStatus.value = OnlineSessionStatus.DISCONNECTED
        _remotePlayers.value = emptyMap()
        _matchPlayers.value = emptyList()
        _readyPlayers.value = emptySet()
        _remoteActions.value = emptyMap()
        _remoteLoadouts.value = emptyMap()
    }

    fun updateAppSettings(settings: AppSettings) {
        viewModelScope.launch { repository.saveSettings(settings) }
    }

    fun signOut() {
        socketClient.disconnect()
        _onlineSessionStatus.value = OnlineSessionStatus.DISCONNECTED
        _remotePlayers.value = emptyMap()
        _matchPlayers.value = emptyList()
        _readyPlayers.value = emptySet()
        _remoteActions.value = emptyMap()
        _remoteLoadouts.value = emptyMap()
        sessionPreferences.edit().clear().apply()
        _currentScreen.value = ScreenRoute.Auth
    }

    /**
     * Authenticates against Render when reachable, but never blocks the local
     * game if the free server is asleep or unavailable.
     */
    fun completeAuth(username: String, password: String, register: Boolean, avatarPreset: String = "king_warrior") {
        val normalizedName = username.trim().ifBlank { "KING_PLAYER" }
        sessionPreferences.edit().putBoolean("authenticated", true).apply()
        viewModelScope.launch {
            var saved = false
            repeat(20) {
                if (!saved) {
                    val nameSaved = repository.updateUsername(normalizedName)
                    val avatarSaved = repository.updateAvatarPreset(avatarPreset)
                    saved = nameSaved && avatarSaved
                    if (!saved) kotlinx.coroutines.delay(50)
                }
            }
            _currentScreen.value = ScreenRoute.Lobby
        }

        // Network work is deliberately separate so a sleeping Render instance
        // can never delay the local lobby.
        viewModelScope.launch {
            connectOnlinePlayer(normalizedName, avatarPresetOverride = avatarPreset)
            if (normalizedName != "KING_PLAYER" && password.isNotBlank()) {
                val remoteResult = remoteClient.authenticate(normalizedName, password, register)
                remoteResult.token?.let { token ->
                    sessionPreferences.edit().putString("remote_token", token).apply()
                }
            }
        }
    }

    /** Maps the expanded local catalog to the server's existing finite avatar protocol. */
    private fun onlineAvatarPreset(localPreset: String?): String = AvatarCatalog.onlinePresetFor(localPreset)

    private fun currentOnlineLoadout(): PlayerLoadout {
        val weaponCategory = weapons.value.firstOrNull { it.isEquipped }?.category.orEmpty()
        val weapon = when {
            weaponCategory.contains("Arco", ignoreCase = true) -> "bow"
            weaponCategory.contains("Mágica", ignoreCase = true) -> "staff"
            weaponCategory.contains("Cuerpo", ignoreCase = true) -> "sword"
            weaponCategory.contains("Pesada", ignoreCase = true) -> "axe"
            else -> "none"
        }
        val account = userAccount.value
        val equippedOutfit = wardrobeItems.value.firstOrNull { it.slot == "Atuendo" && it.isEquipped }
        val equippedArmor = wardrobeItems.value.firstOrNull { it.slot == "Armadura" && it.isEquipped }
        val equippedAccessory = wardrobeItems.value.firstOrNull { it.slot == "Accesorio" && it.isEquipped }
        return PlayerLoadout(
            outfit = if (equippedOutfit != null || account?.isVip == true) "royal" else "explorer",
            weapon = weapon,
            armor = if (equippedArmor != null || account?.isVip == true) "royal" else "leather",
            accessory = if (equippedAccessory != null || (account?.coronasDiamonds ?: 0L) > 0L) "amulet" else "none"
        )
    }

    private fun connectOnlinePlayer(playerId: String, avatarPresetOverride: String? = null) {
        val effectivePlayerId = playerId.trim().ifBlank { "KING_PLAYER" }
        socketClient.connect(
            playerId = effectivePlayerId,
            avatarPreset = avatarPresetOverride?.let(::onlineAvatarPreset)
                ?: onlineAvatarPreset(userAccount.value?.avatarPreset),
            loadout = currentOnlineLoadout(),
            onStatus = { status ->
                _onlineSessionStatus.value = status
                if (status == OnlineSessionStatus.CONNECTING) {
                    _matchPlayers.value = emptyList()
                    _readyPlayers.value = emptySet()
                    _remoteActions.value = emptyMap()
                    _remoteLoadouts.value = emptyMap()
                }
                if (status == OnlineSessionStatus.MATCHMAKING) {
                    _matchPlayers.value = emptyList()
                    _readyPlayers.value = emptySet()
                    _remoteActions.value = emptyMap()
                    _remoteLoadouts.value = emptyMap()
                }
                if (status == OnlineSessionStatus.DISCONNECTED) {
                    _remotePlayers.value = emptyMap()
                    _matchPlayers.value = emptyList()
                    _readyPlayers.value = emptySet()
                    _remoteActions.value = emptyMap()
                    _remoteLoadouts.value = emptyMap()
                }
            },
            onPlayerMoved = { snapshot ->
                // The server broadcasts movement to everyone, including the sender.
                // Do not add the local player to the remote roster or render them twice.
                if (snapshot.id != effectivePlayerId) {
                    _remotePlayers.update { current ->
                        val updated = current + (snapshot.id to snapshot)
                        if (updated.size <= 6) updated else updated.entries.toList().takeLast(6).associate { it.key to it.value }
                    }
                }
            },
            onPlayerAction = { action ->
                if (action.id != effectivePlayerId) {
                    _remoteActions.update { current -> current + (action.id to action) }
                    when (action.action) {
                        OnlineActionNames.READY -> _readyPlayers.update { it + action.id }
                        OnlineActionNames.NOT_READY -> _readyPlayers.update { it - action.id }
                    }
                }
            },
            onPlayerLoadout = { loadout ->
                if (loadout.id != effectivePlayerId) {
                    _remoteLoadouts.update { current ->
                        val updated = current + (loadout.id to loadout)
                        if (updated.size <= 6) updated
                        else updated.entries.toList().takeLast(6).associate { it.key to it.value }
                    }
                }
            },
            onMatchFound = { players ->
                _matchPlayers.value = players.distinct().take(6)
                _remotePlayers.value = emptyMap()
                _remoteActions.value = emptyMap()
                _readyPlayers.value = emptySet()
            }
        )
    }

    fun sendOnlineMove(x: Float, y: Float, running: Boolean) {
        socketClient.sendMove(x, y, running)
    }

    fun sendOnlineAction(action: String) {
        socketClient.sendAction(action)
    }

    fun sendOnlineReady(isReady: Boolean) {
        if (_onlineSessionStatus.value != OnlineSessionStatus.MATCH_FOUND) return
        val playerId = userAccount.value?.username?.trim().orEmpty().ifBlank { "KING_PLAYER" }
        _readyPlayers.update { current ->
            if (isReady) current + playerId else current - playerId
        }
        // The current public server only accepts the documented animation
        // actions on `playerAction`; it does not yet support ready/not_ready.
        // Keep this state local instead of sending an action the server rejects.
        // Remote ready synchronization remains pending until that protocol is added.
    }

    // Kept for callers that only need the offline/local authentication path.
    fun completeLocalAuth(username: String) {
        completeAuth(username, password = "", register = false)
    }

    fun selectChatChannel(channel: String) {
        if (channel in setOf("GLOBAL", "CLAN", "SQUAD")) {
            _selectedChannel.value = channel
        }
    }

    fun sendChatMessage(msg: String) {
        val cleanMessage = msg.trim().take(200)
        if (cleanMessage.isBlank()) return
        val sender = userAccount.value?.username ?: "KING_PLAYER"
        viewModelScope.launch {
            repository.sendChatMessage(sender, cleanMessage, _selectedChannel.value)
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
        val cleanName = friendName.trim()
        if (cleanName.isBlank() || _squadSlots.value.any { it == cleanName }) return
        val current = _squadSlots.value.toMutableList()
        val emptyIndex = current.indexOfFirst { it == null }
        if (emptyIndex != -1) {
            current[emptyIndex] = cleanName
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

    fun equipWardrobeItem(itemId: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onResult(repository.equipWardrobeItem(itemId))
        }
    }

    fun saveProfile(username: String, bio: String, presence: String, avatarPreset: String, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            onResult(repository.updateProfile(username, bio, presence, avatarPreset))
        }
    }

    fun upgradeWeapon(weapon: WeaponItem, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            val success = repository.upgradeWeapon(weapon)
            if (success) {
                onResult(true, "¡Arma mejorada al Nivel ${weapon.level + 1}!")
            } else {
                onResult(false, "Insuficientes recursos (Diamantes, Oro o energía de mejora).")
            }
        }
    }

    fun spinRoulette(onComplete: (String) -> Unit) {
        viewModelScope.launch {
            onComplete(repository.spinRoulette())
        }
    }

    fun openPaymentModal(itemName: String, priceUSD: Double) {
        _paymentItemName.value = itemName
        _paymentAmountUSD.value = priceUSD
        _paymentFeedback.value = null
        _showPaymentModal.value = true
    }

    fun closePaymentModal() {
        _showPaymentModal.value = false
    }

    fun confirmPayment(method: String, onComplete: (String) -> Unit) {
        if (_paymentBusy.value) return
        _paymentBusy.value = true
        val price = paymentAmountUSD.value
        val item = paymentItemName.value
        viewModelScope.launch {
            val result = repository.completeDemoPurchase(item, price, method)
            _paymentBusy.value = false
            _paymentFeedback.value = result
            if (result.startsWith("¡Pago DEMO")) _showPaymentModal.value = false
            onComplete(result)
        }
    }

    fun recordBattleResult(victory: Boolean, kills: Int) {
        viewModelScope.launch {
            repository.recordBattleResult(victory, kills)
        }
    }

    fun saveDiscoTrack(trackId: String) {
        viewModelScope.launch { repository.saveDiscoSelection(trackId = trackId) }
    }

    fun triggerDiscoEmote(emoteId: String) {
        viewModelScope.launch { repository.saveDiscoSelection(emoteId = emoteId) }
    }

    fun selectMiniGame(gameId: String) {
        viewModelScope.launch { repository.saveSelectedMiniGame(gameId) }
    }

    fun settleMiniGame(gameId: String, won: Boolean, gold: Long, diamonds: Long) {
        viewModelScope.launch { repository.settleMiniGame(gameId, won, gold, diamonds) }
    }

    fun contributeToClan(onResult: (String) -> Unit = {}) {
        viewModelScope.launch { onResult(repository.contributeToClan()) }
    }

    /** Legacy reward hook remains available to older callers, but clamps all values. */
    fun grantMiniGameReward(gold: Long, diamonds: Long) {
        viewModelScope.launch {
            repository.addCurrencies(gold.coerceAtLeast(0), diamonds.coerceAtLeast(0))
        }
    }

    fun addAdminCurrencies(gold: Long, diamonds: Long) {
        grantMiniGameReward(gold, diamonds)
    }

    override fun onCleared() {
        socketClient.disconnect()
        super.onCleared()
    }
}
