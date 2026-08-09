package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.Mystic3DBackground
import com.example.ui.ScreenRoute
import com.example.ui.theme.*
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
    onNavigate: (ScreenRoute) -> Unit
) {
    var playerHp by remember { mutableIntStateOf(100) }
    var playerArmor by remember { mutableIntStateOf(100) }
    var alivePlayers by remember { mutableIntStateOf(50) }
    var kills by remember { mutableIntStateOf(0) }
    var glooWallsCount by remember { mutableIntStateOf(5) }
    var ammo by remember { mutableIntStateOf(30) }
    var safeZoneRadius by remember { mutableFloatStateOf(450f) }
    var isVictory by remember { mutableStateOf(false) }
    var isDefeat by remember { mutableStateOf(false) }

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
        while (!isVictory && !isDefeat) {
            delay(30)

            // Update player pos from joystick
            if (joystickOffset != Offset.Zero) {
                playerX += joystickOffset.x * 0.12f
                playerY += joystickOffset.y * 0.12f
            }

            // Shrink purple fire safe zone
            if (safeZoneRadius > 100f) {
                safeZoneRadius -= 0.15f
            }

            // Move projectiles
            val toRemoveProj = mutableListOf<Projectile>()
            projectiles.forEach { p ->
                p.x += p.vx
                p.y += p.vy

                // Collision with bots
                if (p.isPlayer) {
                    bots.forEach { b ->
                        if (kotlin.math.hypot(p.x - b.x, p.y - b.y) < 25f) {
                            b.hp -= 35
                            toRemoveProj.add(p)
                            if (b.hp <= 0) {
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
            val center = Offset(size.width / 2f, size.height / 2f)

            // Grid Map lines
            for (i in 0..size.width.toInt() step 80) {
                drawLine(Color(0xFF101628), Offset(i.toFloat(), 0f), Offset(i.toFloat(), size.height), strokeWidth = 1f)
            }
            for (j in 0..size.height.toInt() step 80) {
                drawLine(Color(0xFF101628), Offset(0f, j.toFloat()), Offset(size.width, j.toFloat()), strokeWidth = 1f)
            }

            // Purple Fire Safe Zone
            drawCircle(
                color = MysticPurple.copy(alpha = 0.25f),
                radius = safeZoneRadius,
                center = center
            )

            // Gloo Walls
            glooWalls.forEach { wall ->
                drawRect(
                    color = CyanMagic,
                    topLeft = Offset(wall.x - 20f, wall.y - 8f),
                    size = androidx.compose.ui.geometry.Size(40f, 16f)
                )
            }

            // Bots
            bots.forEach { b ->
                drawCircle(color = ImperialCrimson, radius = 14f, center = Offset(b.x, b.y))
            }

            // Projectiles
            projectiles.forEach { p ->
                drawCircle(
                    color = if (p.isPlayer) GoldPrimary else MysticPurple,
                    radius = 6f,
                    center = Offset(p.x, p.y)
                )
            }

            // Player character
            drawCircle(color = GoldPrimary, radius = 16f, center = Offset(playerX, playerY))
            drawLine(
                color = CyanMagic,
                start = Offset(playerX, playerY),
                end = Offset(playerX + cos(aimAngle) * 35f, playerY + sin(aimAngle) * 35f),
                strokeWidth = 4f
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
                    .padding(8.dp)
            ) {
                Text(text = "VIDA DE REY: $playerHp/100", color = Color.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "MANTO DE CORONA: $playerArmor/100", color = CyanMagic, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            // Match Info
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "BATALLA DE LOS TRONOS", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
                Text(text = "👑 VIVOS: $alivePlayers/50  |  ⚔️ KILLS: $kills", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                Text(text = "Corona del Rey (Lv.10)", color = GoldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = "MUNICIÓN: $ammo/180", color = TextMuted, fontSize = 10.sp)
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
                                joystickOffset += dragAmount
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

                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("💰 +15,000 Oro", color = GoldGlow, fontWeight = FontWeight.Bold)
                        Text("💎 +50 Diamantes", color = CyanMagic, fontWeight = FontWeight.Bold)
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
