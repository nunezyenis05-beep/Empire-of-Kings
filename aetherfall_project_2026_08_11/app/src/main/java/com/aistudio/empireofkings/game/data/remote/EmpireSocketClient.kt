package com.aistudio.empireofkings.game.data.remote

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject

/** Minimal online session state. The local battle remains available if this is offline. */
enum class OnlineSessionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    MATCHMAKING,
    MATCH_FOUND
}

object OnlineActionNames {
    const val ATTACK = "attack"
    const val READY = "ready"
    const val NOT_READY = "not_ready"
}

data class RemotePlayerSnapshot(
    val id: String,
    val x: Float,
    val y: Float,
    val health: Int,
    val connected: Boolean,
    val timestampMs: Long = System.currentTimeMillis()
)

data class RemoteActionEvent(
    val id: String,
    val action: String,
    val timestampMs: Long = System.currentTimeMillis()
)

data class PlayerLoadout(
    val outfit: String = "explorer",
    val weapon: String = "none",
    val armor: String = "leather",
    val accessory: String = "none"
)

data class RemotePlayerLoadout(
    val id: String,
    val outfit: String,
    val weapon: String,
    val armor: String,
    val accessory: String,
    val timestampMs: Long = System.currentTimeMillis()
)

/** Socket.IO bridge for movement, actions, matchmaking and safe loadout previews. */
class EmpireSocketClient {
    private var socket: Socket? = null
    private var matchmakingRequested = false
    private var lastAction: String? = null
    private var lastActionAtMs = 0L
    private val allowedActions = setOf(
        "idle", "walk", "run", "sneak", "jump",
        "attack", "hurt", "death", "celebrate", "look"
    )

    fun isConnected(): Boolean = socket?.connected() == true

    fun connect(
        playerId: String,
        avatarPreset: String,
        onStatus: (OnlineSessionStatus) -> Unit,
        onPlayerMoved: (RemotePlayerSnapshot) -> Unit,
        onPlayerAction: (RemoteActionEvent) -> Unit,
        onMatchFound: (List<String>) -> Unit,
        loadout: PlayerLoadout = PlayerLoadout(),
        onPlayerLoadout: (RemotePlayerLoadout) -> Unit = {}
    ) {
        disconnect()
        onStatus(OnlineSessionStatus.CONNECTING)

        val options = IO.Options().apply {
            transports = arrayOf(WebSocket.NAME)
            reconnection = true
            reconnectionAttempts = 5
            reconnectionDelay = 1000L
            reconnectionDelayMax = 5000L
            timeout = 10000L
            forceNew = true
            auth = mapOf(
                "playerId" to playerId,
                "avatarPreset" to avatarPreset
            )
        }

        val newSocket = IO.socket(EmpireRemoteClient.BASE_URL, options)
        socket = newSocket
        fun emitLoadout() {
            newSocket.emit(
                "playerLoadout",
                JSONObject()
                    .put("outfit", loadout.outfit)
                    .put("weapon", loadout.weapon)
                    .put("armor", loadout.armor)
                    .put("accessory", loadout.accessory)
            )
        }
        newSocket.on(Socket.EVENT_CONNECT) {
            onStatus(OnlineSessionStatus.CONNECTED)
            emitLoadout()
            if (matchmakingRequested) {
                newSocket.emit("joinMatchmaking")
                onStatus(OnlineSessionStatus.MATCHMAKING)
            }
        }
        // The server confirms the handshake registration with this event.
        // Keep the listener explicit even though the local player id is already known.
        newSocket.on("playerRegistered") { args ->
            val json = args.firstOrNull() as? JSONObject ?: return@on
            json.optString("playerId").trim().take(40)
        }
        newSocket.on(Socket.EVENT_CONNECT_ERROR) {
            onStatus(OnlineSessionStatus.DISCONNECTED)
        }
        newSocket.on(Socket.EVENT_DISCONNECT) {
            onStatus(OnlineSessionStatus.DISCONNECTED)
        }
        newSocket.on("playerMoved") { args ->
            val json = args.firstOrNull() as? JSONObject ?: return@on
            val id = json.optString("id").trim().take(40)
            val x = json.optDouble("x", Double.NaN)
            val y = json.optDouble("y", Double.NaN)
            if (id.isBlank() || !x.isFinite() || !y.isFinite()) return@on
            onPlayerMoved(
                RemotePlayerSnapshot(
                    id = id,
                    x = x.toFloat().coerceIn(0f, 1000f),
                    y = y.toFloat().coerceIn(0f, 1000f),
                    health = json.optInt("health", 100).coerceIn(0, 100),
                    connected = json.optBoolean("connected", true)
                )
            )
        }
        newSocket.on("playerAction") { args ->
            val json = args.firstOrNull() as? JSONObject ?: return@on
            val id = json.optString("id").trim().take(40)
            val action = json.optString("action").trim().take(40)
            val protocolActions = allowedActions + OnlineActionNames.READY + OnlineActionNames.NOT_READY
            if (id.isNotBlank() && action in protocolActions) {
                onPlayerAction(RemoteActionEvent(id = id, action = action))
            }
        }
        newSocket.on("playerLoadout") { args ->
            val json = args.firstOrNull() as? JSONObject ?: return@on
            val id = json.optString("id").trim().take(40)
            val outfit = json.optString("outfit").trim()
            val weapon = json.optString("weapon").trim()
            val armor = json.optString("armor").trim()
            val accessory = json.optString("accessory").trim()
            val validOutfits = setOf("explorer", "royal", "guard", "shadow")
            val validWeapons = setOf("sword", "bow", "axe", "staff", "none")
            val validArmor = setOf("leather", "steel", "royal", "none")
            val validAccessories = setOf("cloak", "scarf", "amulet", "none")
            if (id.isNotBlank() && outfit in validOutfits && weapon in validWeapons &&
                armor in validArmor && accessory in validAccessories
            ) {
                onPlayerLoadout(RemotePlayerLoadout(id, outfit, weapon, armor, accessory))
            }
        }
        newSocket.on("matchWaiting") {
            onStatus(OnlineSessionStatus.MATCHMAKING)
        }
        newSocket.on("matchFound") { args ->
            val json = args.firstOrNull() as? JSONObject ?: return@on
            val players = buildList {
                val array = json.optJSONArray("players") ?: return@buildList
                for (index in 0 until array.length()) {
                    val playerId = array.optString(index).trim()
                    if (playerId.isNotBlank() && playerId !in this && size < 6) {
                        add(playerId)
                    }
                }
            }
            if (players.isEmpty()) {
                onStatus(OnlineSessionStatus.MATCHMAKING)
                return@on
            }
            onStatus(OnlineSessionStatus.MATCH_FOUND)
            onMatchFound(players)
        }
        newSocket.connect()
    }

    fun sendMove(x: Float, y: Float, running: Boolean) {
        if (!x.isFinite() || !y.isFinite()) return
        socket?.takeIf { it.connected() }?.emit(
            "playerMove",
            JSONObject()
                .put("x", x.coerceIn(0f, 1000f))
                .put("y", y.coerceIn(0f, 1000f))
                .put("running", running)
        )
    }

    fun sendAction(action: String) {
        val cleanAction = action.trim().take(40)
        if (cleanAction.isBlank() || cleanAction !in allowedActions) return
        val now = System.currentTimeMillis()
        if (cleanAction == lastAction && now - lastActionAtMs < 80L) return
        lastAction = cleanAction
        lastActionAtMs = now
        socket?.takeIf { it.connected() }?.emit("playerAction", cleanAction)
    }

    fun joinMatchmaking() {
        if (matchmakingRequested) return
        matchmakingRequested = true
        socket?.takeIf { it.connected() }?.emit("joinMatchmaking")
    }

    fun cancelMatchmaking() {
        matchmakingRequested = false
        socket?.disconnect()
        lastAction = null
        lastActionAtMs = 0L
    }

    fun disconnect() {
        matchmakingRequested = false
        socket?.off()
        socket?.disconnect()
        socket = null
        lastAction = null
        lastActionAtMs = 0L
    }
}
