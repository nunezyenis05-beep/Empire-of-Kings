package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun WardrobeScreen(
    userAccount: UserAccount?,
    weapons: List<WeaponItem>,
    onNavigate: (ScreenRoute) -> Unit,
    onEquipWeapon: (String) -> Unit,
    onUpgradeWeapon: (WeaponItem, (Boolean, String) -> Unit) -> Unit
) {
    var selectedWeapon by remember { mutableStateOf(weapons.firstOrNull()) }
    var feedbackMsg by remember { mutableStateOf<String?>(null) }

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = { }
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // LEFT: WEAPONS INVENTORY LIST
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text("MIS ARMAS Y VESTUARIO", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                        Spacer(modifier = Modifier.height(6.dp))

                        LazyColumn(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(weapons) { wpn ->
                                val isSelected = selectedWeapon?.id == wpn.id
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) Color(0xFF231A3D) else Color(0xFF0F1526))
                                        .border(1.dp, if (wpn.isEquipped) GoldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                        .clickable { selectedWeapon = wpn }
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(wpn.name, color = if (isSelected) GoldGlow else TextLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("${wpn.category} • Nivel ${wpn.level}/15", color = TextMuted, fontSize = 10.sp)
                                    }

                                    if (wpn.isEquipped) {
                                        Box(
                                            modifier = Modifier
                                                .background(GoldPrimary, RoundedCornerShape(4.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("EQUIPADA", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // RIGHT: FORGE & UPGRADE DETAILS (LVL 1 - 15)
                Card(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    selectedWeapon?.let { wpn ->
                        Column(
                            modifier = Modifier
                                .padding(14.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(wpn.name, color = GoldPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                                        Text(if (wpn.isMysticalPremium) "✨ Arma Mística Premium" else "⚔️ Arma Normal de Mando", color = CyanMagic, fontSize = 11.sp)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1E1430))
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text("NIVEL ${wpn.level} / 15", color = GoldGlow, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                    }
                                }

                                Divider(color = GoldBorder.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 10.dp))

                                Text(wpn.description, color = TextLight, fontSize = 12.sp)

                                Spacer(modifier = Modifier.height(12.dp))

                                // Stats progress
                                Text("ESTADÍSTICAS IMPERIALES:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))

                                StatBar("Daño Místico", wpn.damage, 120, GoldPrimary)
                                StatBar("Cadencia de Disparo", wpn.fireRate, 100, CyanMagic)
                                StatBar("Alcance Mágico", wpn.range, 100, MysticPurple)
                                StatBar("Precisión", wpn.accuracy, 100, GoldGlow)

                                Spacer(modifier = Modifier.height(10.dp))

                                // Unlocked Auras
                                Text("EFECTOS DESBLOQUEABLES:", color = TextMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                                    EffectBadge("Aura (Niv. 5)", wpn.auraEffectUnlocked)
                                    EffectBadge("Runas (Niv. 10)", wpn.floatRunesUnlocked)
                                    EffectBadge("Maestría Dorada (Niv. 15)", wpn.goldenMasteryUnlocked)
                                }
                            }

                            feedbackMsg?.let {
                                Text(it, color = GoldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            // Actions: Equip & Upgrade
                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                Button(
                                    onClick = { onEquipWeapon(wpn.id) },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF231A3D)),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)
                                ) {
                                    Text("EQUIPAR ARMA", color = GoldGlow, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = {
                                        onUpgradeWeapon(wpn) { ok, msg -> feedbackMsg = msg }
                                    },
                                    modifier = Modifier
                                        .weight(1.2f)
                                        .height(44.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                ) {
                                    Text(
                                        text = if (wpn.isMysticalPremium) "MEJORAR (💎 ${20 * wpn.level} Diamantes)" else "MEJORAR (💰 ${500 * wpn.level} Oro)",
                                        color = Color.Black,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.Wardrobe, onNavigate = onNavigate)
        }
    }
}

@Composable
private fun StatBar(label: String, valCurrent: Int, valMax: Int, color: Color) {
    Column(modifier = Modifier.padding(vertical = 2.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = TextLight, fontSize = 10.sp)
            Text("$valCurrent/$valMax", color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        LinearProgressIndicator(
            progress = (valCurrent.toFloat() / valMax.toFloat()).coerceIn(0f, 1f),
            modifier = Modifier
                .fillMaxWidth()
                .height(5.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = Color(0xFF101628)
        )
    }
}

@Composable
private fun EffectBadge(label: String, unlocked: Boolean) {
    Box(
        modifier = Modifier
            .background(if (unlocked) Color(0xFF1E1430) else Color(0xFF0F1526), RoundedCornerShape(6.dp))
            .border(1.dp, if (unlocked) GoldPrimary else Color.Gray, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(
            text = (if (unlocked) "✓ " else "🔒 ") + label,
            color = if (unlocked) GoldGlow else TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
