package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.R
import com.aistudio.empireofkings.game.ui.components.HumanAvatar3D
import com.aistudio.empireofkings.game.ui.components.Mystic3DBackground
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.theme.*
import com.aistudio.empireofkings.game.data.remote.OnlineActionNames
import com.aistudio.empireofkings.game.data.remote.OnlineSessionStatus
import com.aistudio.empireofkings.game.data.remote.RemoteActionEvent
import com.aistudio.empireofkings.game.data.remote.RemotePlayerLoadout
import com.aistudio.empireofkings.game.data.remote.RemotePlayerSnapshot
import com.aistudio.empireofkings.game.data.remote.ServerConnectionStatus
import kotlinx.coroutines.delay
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class BattleEntity(
    val id: String,
    var x: Float,
    var y: Float,
    var hp: Int = 100,
    val isBot: Boolean = true,
    val name: String
)

data class Projectile(
    var x: Float,
    var y: Float,
    val vx: Float,
    val vy: Float,
    val isPlayer: Boolean
)

data class GlooWall(
    val x: Float,
    val y: Float,
    var hp: Int = 200
)

@Composable
fun BattleRoyaleScreen(
    onNavigate: (ScreenRoute) -> Unit,
    onBattleFinished: (Boolean, Int) -> Unit,
    remotePlayers: List<RemotePlayerSnapshot> = emptyList(),
    remoteActions: List<RemoteActionEvent> = emptyList(),
    remoteLoadouts: List<RemotePlayerLoadout> = emptyList(),
    onlineSessionStatus: OnlineSessionStatus = OnlineSessionStatus.DISCONNECTED,
    serverStatus: ServerConnectionStatus = ServerConnectionStatus.CHECKING,
    matchPlayers: List<String> = emptyList(),
    readyPlayers: Set<String> = emptySet(),
    localPlayerId: String? = null,
    localAvatarPreset: String = "king_warrior",
    equippedWeaponName: String = "Corona del Rey",
    outfitName: String = "Vestuario Imperial",
    onOnlineMove: (x: Float, y: Float, running: Boolean) -> Unit = { _, _, _ -> },
    onOnlineAction: (String) -> Unit = {},
    onOnlineReady: (Boolean) -> Unit = {},
    onRetryOnline: () -> Unit = {},
    onCancelOnlineSearch: () -> Unit = {}
) {
    var playerHp by remember { mutableIntStateOf(100) }
    var playerArmor by remember { mutableIntStateOf(100) }
    // The local prototype simulates five combatants (the player plus four bots).
    // Keeping the simulated roster honest makes victory reachable during offline play.
    val totalPlayers = 5
    var alivePlayers by remember { mutableIntStateOf(totalPlayers) }
    var kills by remember { mutableIntStateOf(0) }
    var glooWallsCount by remember { mutableIntStateOf(5) }
    var ammo by remember { mutableIntStateOf(30) }
    var safeZoneRadius by remember { mutableFloatStateOf(450f) }
    var isVictory by remember { mutableStateOf(false) }
    var isDefeat by remember { mutableStateOf(false) }
    var rewardRecorded by remember { mutableStateOf(false) }
    var matchmakingSeconds by remember { mutableIntStateOf(0) }
    var localReady by remember { mutableStateOf(false) }
    var launchCountdown by remember { mutableIntStateOf(0) }
    val rosterPlayers = buildList {
        localPlayerId?.takeIf { it.isNotBlank() }?.let(::add)
        matchPlayers.filterNot { it == localPlayerId }.forEach { add(it) }
    }.take(6)
    val allPlayersReady = onlineSessionStatus == OnlineSessionStatus.MATCH_FOUND &&
        rosterPlayers.size > 1 &&
        rosterPlayers.all { it in readyPlayers || (it == localPlayerId && localReady) }

    LaunchedEffect(onlineSessionStatus) {
        if (onlineSessionStatus != OnlineSessionStatus.MATCH_FOUND) {
            localReady = false
        }
        if (onlineSessionStatus == OnlineSessionStatus.MATCHMAKING) {
            matchmakingSeconds = 0
            // The effect is cancelled automatically when the session changes,
            // so the counter can keep running without an arbitrary upper limit.
            while (true) {
                delay(1000)
                matchmakingSeconds += 1
            }
        } else {
            matchmakingSeconds = 0
        }
    }

    LaunchedEffect(onlineSessionStatus, allPlayersReady) {
        if (allPlayersReady) {
            launchCountdown = 3
            repeat(3) {
                delay(1000)
                launchCountdown -= 1
            }
        } else {
            launchCountdown = 0
        }
    }

    LaunchedEffect(isVictory, isDefeat) {
        if ((isVictory || isDefeat) && !rewardRecorded) {
            rewardRecorded = true
            onBattleFinished(isVictory, kills)
        }
    }

    // Player position
    var playerX by remember { mutableFloatStateOf(400f) }
    var playerY by remember { mutableFloatStateOf(300f) }
    var aimAngle by remember { mutableFloatStateOf(0f) }

    // Joystick offset
    var joystickOffset by remember { mutableStateOf(Offset.Zero) }

    // Entities in match
    val bots = remember {
        mutableStateListOf(
            BattleEntity("b1", 200f, 150f, name = "Sombra_Bot_1"),
            BattleEntity("b2", 600f, 200f, name = "Guardián_Rúnico"),
            BattleEntity("b3", 550f, 500f, name = "Cazador_Abisal"),
            BattleEntity("b4", 150f, 450f, name = "Lobo_Fuego_Azul")
        )
    }

    val projectiles = remember { mutableStateListOf<Projectile>() }
    val glooWalls = remember { mutableStateListOf<GlooWall>() }

    // Game loop simulation
    LaunchedEffect(Unit) {
        var zoneDamageTick = 0
        var networkTick = 0
        while (!isVictory && !isDefeat) {
            delay(30)
            networkTick = (networkTick + 1) % 4

            // Update player pos from joystick and keep the local arena bounded.
            if (joystickOffset != Offset.Zero) {
                playerX = (playerX + joystickOffset.x * 0.12f).coerceIn(24f, 776f)
                playerY = (playerY + joystickOffset.y * 0.12f).coerceIn(24f, 576f)
            }
            if (networkTick == 0) {
                onOnlineMove(playerX, playerY, joystickOffset != Offset.Zero)
            }

            // Shrink purple fire safe zone and punish players who leave it.
            if (safeZoneRadius > 100f) {
                safeZoneRadius -= 0.15f
            }
            zoneDamageTick = (zoneDamageTick + 1) % 20
            if (kotlin.math.hypot(playerX - 400f, playerY - 300f) > safeZoneRadius && zoneDamageTick == 0) {
                playerHp = (playerHp - 3).coerceAtLeast(0)
            }

            // Move projectiles. Remove misses once they leave the arena so a long
            // match cannot grow the in-memory projectile list without a bound.
            val toRemoveProj = mutableListOf<Projectile>()
            projectiles.forEach { p ->
                p.x += p.vx
                p.y += p.vy
                if (p.x !in -40f..840f || p.y !in -40f..640f) {
                    toRemoveProj.add(p)
                    return@forEach
                }

                // Collision with bots
                if (p.isPlayer) {
                    bots.forEach { b ->
                        // A bot that was already eliminated by another projectile
                        // in the same tick must not award a second kill.
                        if (b.hp > 0 && kotlin.math.hypot(p.x - b.x, p.y - b.y) < 25f) {
                            val wasAlive = b.hp > 0
                            b.hp -= 35
                            toRemoveProj.add(p)
                            if (wasAlive && b.hp <= 0) {
                                kills += 1
                                alivePlayers = (alivePlayers - 1).coerceAtLeast(1)
                            }
                        }
                    }
                } else {
                    // Bot projectile hit player
                    if (kotlin.math.hypot(p.x - playerX, p.y - playerY) < 25f) {
                        if (playerArmor > 0) playerArmor = (playerArmor - 15).coerceAtLeast(0)
                        else playerHp = (playerHp - 15).coerceAtLeast(0)
                        toRemoveProj.add(p)
                    }
                }
            }
            projectiles.removeAll(toRemoveProj)
            bots.removeAll { it.hp <= 0 }

            // Bot simple AI shooting
            bots.forEach { b ->
                val dist = kotlin.math.hypot(playerX - b.x, playerY - b.y)
                if (dist < 280f && (0..30).random() == 1) {
                    val angle = atan2(playerY - b.y, playerX - b.x)
                    projectiles.add(Projectile(b.x, b.y, cos(angle) * 8f, sin(angle) * 8f, isPlayer = false))
                }
            }

            // Check victory/defeat condition
            if (alivePlayers <= 1) {
                isVictory = true
            }
            if (playerHp <= 0) {
                isDefeat = true
            }
        }
    }

    Mystic3DBackground {
        // MATCH CANVAS (MAP, CHARACTERS, PROJECTILES, PURPLE FIRE ZONE)
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        aimAngle = atan2(dragAmount.y, dragAmount.x)
                    }
                }
        ) {
            // The simulation remains deterministic in 800x600 world units, while rendering
            // scales to the actual portrait canvas instead of stretching one axis by magic numbers.
            val scaleX = size.width / 800f
            val scaleY = size.height / 600f
            val scale = minOf(scaleX, scaleY)
            fun arenaPoint(x: Float, y: Float) = Offset(x * scaleX, y * scaleY)
            val center = arenaPoint(400f, 300f)

            // Grid Map lines
            for (i in 0..800 step 80) {
                drawLine(Color(0xFF101628), arenaPoint(i.toFloat(), 0f), arenaPoint(i.toFloat(), 600f), strokeWidth = 1f)
            }
            for (j in 0..600 step 80) {
                drawLine(Color(0xFF101628), arenaPoint(0f, j.toFloat()), arenaPoint(800f, j.toFloat()), strokeWidth = 1f)
            }

            // Purple Fire Safe Zone
            drawCircle(
                color = MysticPurple.copy(alpha = 0.25f),
                radius = safeZoneRadius * scale,
                center = center
            )

            // Gloo Walls
            glooWalls.forEach { wall ->
                drawRect(
                    color = CyanMagic,
                    topLeft = arenaPoint(wall.x - 20f, wall.y - 8f),
                    size = androidx.compose.ui.geometry.Size(40f * scaleX, 16f * scaleY)
                )
            }

            // Players received from the online room (bots remain local for offline play).
            remotePlayers
                .filter {
                    it.id != localPlayerId && it.connected &&
                        System.currentTimeMillis() - it.timestampMs < 10_000L
                }
                .forEach { remote ->
                    val remotePoint = arenaPoint(remote.x.coerceIn(16f, 784f), remote.y.coerceIn(16f, 584f))
                    drawCircle(color = CyanMagic, radius = 11f * scale, center = remotePoint)
                    drawCircle(color = Color.White.copy(alpha = 0.7f), radius = 4f * scale, center = remotePoint)
                    drawRect(
                        color = Color(0x99000000),
                        topLeft = remotePoint - Offset(18f * scale, 21f * scale),
                        size = androidx.compose.ui.geometry.Size(36f * scale, 4f * scale)
                    )
                    drawRect(
                        color = Color(0xFF55E879),
                        topLeft = remotePoint - Offset(18f * scale, 21f * scale),
                        size = androidx.compose.ui.geometry.Size(36f * scale * (remote.health / 100f), 4f * scale)
                    )
                    val recentAttack = remoteActions.firstOrNull {
                        it.id == remote.id && it.action == OnlineActionNames.ATTACK &&
                            System.currentTimeMillis() - it.timestampMs < 600L
                    }
                    if (recentAttack != null) {
                        drawCircle(
                            color = GoldPrimary.copy(alpha = 0.8f),
                            radius = 23f * scale,
                            center = remotePoint,
                            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f)
                        )
                    }
                }

            // Bots
            bots.forEach { b ->
                drawCircle(color = ImperialCrimson, radius = 14f * scale, center = arenaPoint(b.x, b.y))
            }

            // Projectiles
            projectiles.forEach { p ->
                drawCircle(
                    color = if (p.isPlayer) GoldPrimary else MysticPurple,
                    radius = 6f * scale,
                    center = arenaPoint(p.x, p.y)
                )
            }

            // Player character
            drawCircle(color = GoldPrimary, radius = 16f * scale, center = arenaPoint(playerX, playerY))
            drawLine(
                color = CyanMagic,
                start = arenaPoint(playerX, playerY),
                end = arenaPoint(playerX + cos(aimAngle) * 35f, playerY + sin(aimAngle) * 35f),
                strokeWidth = 4f * scale
            )
        }

        // TOP MATCH STATUS BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Player HP & Armor
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC0F1526))
                    .border(1.dp, GoldBorder, RoundedCornerShape(8.dp))
                    .padding(5.dp)
            ) {
                IconButton(onClick = { onNavigate(ScreenRoute.Lobby) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver al Lobby", tint = GoldGlow, modifier = Modifier.size(17.dp))
                }
                Text(text = "VIDA DE REY: $playerHp/100", color = Color.Green, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "MANTO DE CORONA: $playerArmor/100", color = CyanMagic, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Match Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "BATALLA DE LOS TRONOS", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
                val onlineLabel = when (onlineSessionStatus) {
                    OnlineSessionStatus.DISCONNECTED -> "LOCAL / SIN CONEXIÓN"
                    OnlineSessionStatus.CONNECTING -> "CONECTANDO..."
                    OnlineSessionStatus.CONNECTED -> "ONLINE"
                    OnlineSessionStatus.MATCHMAKING -> "BUSCANDO RIVALES..."
                    OnlineSessionStatus.MATCH_FOUND -> "SQUAD ONLINE: ${rosterPlayers.size}"
                }
                Text(text = "🌐 $onlineLabel", color = CyanMagic, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                if (serverStatus != ServerConnectionStatus.ONLINE) {
                    val serverLabel = if (serverStatus == ServerConnectionStatus.CHECKING) "COMPROBANDO SERVIDOR" else "RESPALDO LOCAL ACTIVO"
                    Text(text = "▣ $serverLabel", color = TextMuted, fontSize = 8.sp)
                }
                Text(text = "👑 VIVOS: $alivePlayers/$totalPlayers  |  ⚔️ KILLS: $kills", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                if (onlineSessionStatus == OnlineSessionStatus.MATCH_FOUND) {
                    Text(text = "⚡ SEÑALES REMOTAS: ${remotePlayers.count { it.connected }}", color = CyanMagic, fontSize = 9.sp)
                }
            }

            // Weapon & Ammo
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xCC0F1526))
                    .border(1.dp, GoldBorder, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalAlignment = Alignment.End
            ) {
                Text(text = equippedWeaponName.take(22), color = GoldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(text = "MUNICIÓN: $ammo/180", color = TextMuted, fontSize = 10.sp)
            }
        }

        AnimatedVisibility(
            visible = onlineSessionStatus == OnlineSessionStatus.MATCHMAKING ||
                onlineSessionStatus == OnlineSessionStatus.MATCH_FOUND,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 76.dp)
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xEE0F1526)),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (onlineSessionStatus == OnlineSessionStatus.MATCHMAKING) {
                        Text("BUSCANDO RIVAL ONLINE", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${matchmakingSeconds}s · el combate local sigue activo", color = TextLight, fontSize = 10.sp)
                        if (matchmakingSeconds >= 8) {
                            Text("El servidor gratuito puede estar despertando", color = TextMuted, fontSize = 9.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = onRetryOnline,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("REINTENTAR", fontSize = 9.sp)
                                }
                                OutlinedButton(
                                    onClick = onCancelOnlineSearch,
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
                                ) {
                                    Text("JUGAR LOCAL", fontSize = 9.sp)
                                }
                            }
                        }
                    } else {
                        Text("¡PARTIDA ONLINE ENCONTRADA!", color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("${rosterPlayers.size} jugadores conectados", color = TextLight, fontSize = 10.sp)
                        val readyCount = rosterPlayers.count { it in readyPlayers || (it == localPlayerId && localReady) }
                        Text("LISTOS: $readyCount/${rosterPlayers.size}", color = TextLight, fontSize = 9.sp)
                        if (rosterPlayers.size > 1) {
                            Text("Listo remoto: pendiente del protocolo del servidor", color = TextMuted, fontSize = 8.sp)
                        }
                        if (launchCountdown > 0) {
                            Text("DESPLIEGUE EN $launchCountdown", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        rosterPlayers.forEachIndexed { index, playerId ->
                            val isLocalPlayer = playerId == localPlayerId
                            val remoteLoadout = remoteLoadouts.firstOrNull { it.id == playerId }
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 3.dp),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                HumanAvatar3D(
                                    preset = if (isLocalPlayer) {
                                        localAvatarPreset
                                    } else {
                                        when (remoteLoadout?.outfit) {
                                            "guard" -> "royal_guard"
                                            "shadow" -> "arcane_queen"
                                            else -> "king_warrior"
                                        }
                                    },
                                    modifier = Modifier.size(28.dp).clip(CircleShape),
                                    showLoadingLabel = false
                                )
                                Text(if (index == 0) "👑" else "⚔️", fontSize = 10.sp)
                                val loadoutLabel = when {
                                    isLocalPlayer -> "$outfitName · $equippedWeaponName"
                                    remoteLoadout != null -> "${remoteLoadout.outfit} · ${remoteLoadout.weapon} · ${remoteLoadout.armor} · ${remoteLoadout.accessory}"
                                    else -> "Avatar humano · equipamiento remoto pendiente"
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(playerId.take(24), color = GoldGlow, fontSize = 10.sp)
                                    Text(
                                        loadoutLabel,
                                        color = TextMuted,
                                        fontSize = 8.sp,
                                        maxLines = 1
                                    )
                                }
                                val isReady = playerId in readyPlayers || (isLocalPlayer && localReady)
                                val hasRecentSignal = isLocalPlayer || remotePlayers.any {
                                    it.id == playerId && it.connected &&
                                        System.currentTimeMillis() - it.timestampMs < 10_000L
                                }
                                val playerStatus = when {
                                    isReady -> "LISTO"
                                    hasRecentSignal -> "CONECTADO"
                                    else -> "SIN SEÑAL"
                                }
                                Text(
                                    playerStatus,
                                    color = when {
                                        isReady -> GoldPrimary
                                        hasRecentSignal -> Color(0xFF65E572)
                                        else -> Color(0xFFE36A6A)
                                    },
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Button(
                            onClick = {
                                localReady = !localReady
                                onOnlineReady(localReady)
                            },
                            enabled = launchCountdown == 0,
                            modifier = Modifier.padding(top = 8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (localReady) Color(0xFF5B1F2A) else GoldPrimary,
                                contentColor = if (localReady) TextLight else Color.Black
                            )
                        ) {
                            Text(if (localReady) "CANCELAR LISTO" else "ESTOY LISTO", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // MOBILE CONTROLS OVERLAY
        Box(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            // JOYSTICK LEFT
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .size(110.dp)
                    .clip(CircleShape)
                    .background(Color(0x60000000))
                    .border(2.dp, GoldBorder, CircleShape)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = { joystickOffset = Offset.Zero },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                joystickOffset = (joystickOffset + dragAmount).let { offset ->
                                    val length = kotlin.math.hypot(offset.x, offset.y)
                                    if (length > 48f) offset * (48f / length) else offset
                                }
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(GoldPrimary)
                )
            }

            // ACTION BUTTONS RIGHT (FIRE, AIM, GLOO WALL, HEAL, RELOAD)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gloo Wall Shield
                FloatingActionButton(
                    onClick = {
                        if (glooWallsCount > 0) {
                            glooWallsCount -= 1
                            glooWalls.add(GlooWall(playerX + cos(aimAngle) * 50f, playerY + sin(aimAngle) * 50f))
                        }
                    },
                    containerColor = CyanMagic,
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("🛡️", fontSize = 18.sp)
                }

                // Heal Potion
                FloatingActionButton(
                    onClick = {
                        playerHp = (playerHp + 40).coerceAtMost(100)
                    },
                    containerColor = MysticPurple,
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("🧪", fontSize = 18.sp)
                }

                // Reload
                FloatingActionButton(
                    onClick = { ammo = 30 },
                    containerColor = Color(0xFF231A3D),
                    modifier = Modifier.size(48.dp)
                ) {
                    Text("🔄", fontSize = 18.sp)
                }

                // FIRE BUTTON (PRIMARY)
                FloatingActionButton(
                    onClick = {
                        if (ammo > 0) {
                            ammo -= 1
                            val vx = cos(aimAngle) * 12f
                            val vy = sin(aimAngle) * 12f
                            projectiles.add(Projectile(playerX, playerY, vx, vy, isPlayer = true))
                            onOnlineAction(OnlineActionNames.ATTACK)
                        }
                    },
                    containerColor = GoldPrimary,
                    modifier = Modifier.size(64.dp)
                ) {
                    Text("💥", fontSize = 24.sp)
                }
            }
        }

        // VICTORY / DEFEAT MODAL
        AnimatedVisibility(
            visible = isVictory || isDefeat,
            modifier = Modifier.align(Alignment.Center)
        ) {
            Card(
                modifier = Modifier
                    .width(360.dp)
                    .border(2.dp, GoldBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isVictory) "¡REINADO SUPREMO!" else "ELIMINADO EN BATALLA",
                        color = if (isVictory) GoldPrimary else ImperialCrimson,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(text = "Partida Finalizada", color = TextLight, fontSize = 14.sp)
                    Text(text = "Eliminaciones: $kills", color = CyanMagic, fontSize = 13.sp, fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isVictory) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text("💰 +15,000 Oro", color = GoldGlow, fontWeight = FontWeight.Bold)
                            Text("💎 +50 Diamantes", color = CyanMagic, fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Text("Sin recompensa por esta partida.", color = TextMuted, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onNavigate(ScreenRoute.Lobby) },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("VOLVER AL LOBBY", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
