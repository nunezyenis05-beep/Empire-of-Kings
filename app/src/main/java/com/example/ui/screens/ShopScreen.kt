package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccount
import com.example.data.WeaponItem
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*

import com.example.ui.components.Mystic3DBackground

@Composable
fun ShopScreen(
    userAccount: UserAccount?,
    weapons: List<WeaponItem>,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Ruleta Premium, 1: Armas Místicas, 2: Recargas
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var spinMessage by remember { mutableStateOf<String?>(null) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 3000),
        label = "rouletteSpin"
    )

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = onOpenPaymentModal
            )

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Shop Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("🎡 RULETA PREMIUM", "⚔️ ARMAS MÍSTICAS", "💎 RECARGAS DE DIAMANTES").forEachIndexed { idx, title ->
                        val isSel = selectedTab == idx
                        Button(
                            onClick = { selectedTab = idx },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSel) GoldPrimary else Color(0xFF101628)
                            )
                        ) {
                            Text(title, color = if (isSel) Color.Black else GoldGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedTab) {
                    0 -> {
                        // RULETA PREMIUM GACHA
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Wheel Visual
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight(),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(240.dp)
                                        .rotate(animatedRotation)
                                        .clip(CircleShape)
                                        .border(4.dp, GoldPrimary, CircleShape)
                                        .background(Color(0xFF1E1430)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(48.dp))
                                        Text("RULETA MÍSTICA", color = GoldGlow, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                        Text("¡Consigue Armas Lv 15!", color = CyanMagic, fontSize = 10.sp)
                                    }
                                }
                            }

                            // Spin Controls
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    verticalArrangement = Arrangement.Center,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("RULETA DE ARMAS MÍSTICAS PREMIUM", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                                    Text("Premios: Corona del Rey, Escopeta del Abismo, Esencia Mística, 10,000 Oro", color = TextLight, fontSize = 12.sp)

                                    Spacer(modifier = Modifier.height(20.dp))

                                    spinMessage?.let {
                                        Text(it, color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Spacer(modifier = Modifier.height(10.dp))
                                    }

                                    Button(
                                        onClick = {
                                            rotationAngle += 1080f + (0..360).random()
                                            spinMessage = "¡Girando! Has ganado: ¡Esencia Mística x100 & Fragmentos Místicos!"
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(48.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                    ) {
                                        Text("GIRAR RULETA (💎 25 Diamantes)", color = Color.Black, fontWeight = FontWeight.Black)
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // WEAPONS CATALOG
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(3),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(weapons) { wpn ->
                                Card(
                                    modifier = Modifier.border(1.dp, if (wpn.isMysticalPremium) GoldPrimary else GoldBorder, RoundedCornerShape(10.dp)),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                                ) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text(wpn.name, color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text(wpn.category, color = TextMuted, fontSize = 10.sp)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("Daño: ${wpn.damage} | Alcance: ${wpn.range}", color = TextLight, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { },
                                            modifier = Modifier.fillMaxWidth(),
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                        ) {
                                            Text(
                                                text = if (wpn.isMysticalPremium) "💎 ${wpn.costDiamonds} Diamantes" else "💰 ${wpn.costGold} Oro",
                                                color = Color.Black,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    else -> {
                        // RECARGAS PACKS
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            listOf(
                                "Pack Imperial Inicial" to (500L to 4.99),
                                "Pack Rey Guerrero" to (1200L to 9.99),
                                "Pack Emperador Místico" to (3500L to 24.99),
                                "Cofre Supremo de Reyes" to (8000L to 49.99)
                            ).forEach { (packTitle, data) ->
                                val (diamonds, price) = data
                                Card(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(packTitle, color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("💎 $diamonds", color = CyanMagic, fontSize = 20.sp, fontWeight = FontWeight.Black)
                                        Text("+ 50,000 Oro Gratis", color = GoldGlow, fontSize = 10.sp)
                                        Button(
                                            onClick = { onOpenPaymentModal() },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text("$$price USD", color = Color.Black, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.Shop, onNavigate = onNavigate)
        }
    }
}
