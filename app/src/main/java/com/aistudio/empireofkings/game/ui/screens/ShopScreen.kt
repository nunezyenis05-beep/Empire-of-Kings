package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.data.WeaponItem
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.*
import com.aistudio.empireofkings.game.ui.theme.*

/** Portrait-safe shop: every purchase/equip action is delegated to Room-backed logic. */
@Composable
fun ShopScreen(
    userAccount: UserAccount?,
    weapons: List<WeaponItem>,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: (String, Double) -> Unit,
    onSpinRoulette: ((String) -> Unit) -> Unit,
    onEquipWeapon: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }
    var spinMessage by remember { mutableStateOf<String?>(null) }
    var isSpinning by remember { mutableStateOf(false) }
    var shopFeedback by remember { mutableStateOf<String?>(null) }
    val animatedRotation by animateFloatAsState(rotationAngle, tween(2200), label = "rouletteSpin")

    Mystic3DBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(userAccount, onOpenProfile = { onNavigate(ScreenRoute.Profile) }, onOpenPayment = { onOpenPaymentModal("Pack de Diamantes", 9.99) })
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { ReferenceTitle("TIENDA IMPERIAL", "Compra demo local: los packs no cobran dinero real.") }
                item {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        listOf("RULETA", "ARMAS", "RECARGA").forEachIndexed { index, label ->
                            TextButton(
                                onClick = { selectedTab = index },
                                modifier = Modifier.weight(1f).border(1.dp, if (selectedTab == index) GoldPrimary else GoldBorder.copy(alpha = .55f), RoundedCornerShape(6.dp)),
                                colors = ButtonDefaults.textButtonColors(contentColor = if (selectedTab == index) GoldPrimary else TextMuted),
                                contentPadding = PaddingValues(vertical = 5.dp)
                            ) { Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                        }
                    }
                }
                shopFeedback?.let { message -> item { ReferenceBadge(message, Modifier.fillMaxWidth()) } }
                when (selectedTab) {
                    0 -> {
                        item {
                            ReferencePanel(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(Modifier.size(150.dp).rotate(animatedRotation).clip(CircleShape).border(3.dp, GoldPrimary, CircleShape).background(Color(0xFF1E1430)), contentAlignment = Alignment.Center) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.Star, null, tint = GoldPrimary, modifier = Modifier.size(36.dp))
                                            Text("RULETA", color = GoldGlow, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                            Text("25 COR", color = CyanMagic, fontSize = 10.sp)
                                        }
                                    }
                                    Spacer(Modifier.height(8.dp))
                                    Text("Premios: Esencia, Oro o Fragmentos", color = TextLight, fontSize = 11.sp)
                                    spinMessage?.let { Text(it, color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                                    Spacer(Modifier.height(6.dp))
                                    Button(
                                        enabled = !isSpinning,
                                        onClick = {
                                            isSpinning = true
                                            rotationAngle += 1080f + (0..360).random()
                                            onSpinRoulette { result -> spinMessage = result; isSpinning = false }
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                    ) { Text(if (isSpinning) "GIRANDO…" else "GIRAR POR 25 DIAMANTES", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 11.sp) }
                                }
                            }
                        }
                    }
                    1 -> {
                        items(weapons, key = { it.id }) { weapon ->
                            ReferencePanel(Modifier.fillMaxWidth()) {
                                Column(Modifier.padding(10.dp)) {
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(Modifier.weight(1f)) {
                                            Text(weapon.name, color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                            Text("${weapon.category} • NIVEL ${weapon.level}/15", color = TextMuted, fontSize = 10.sp)
                                        }
                                        ReferenceBadge(if (weapon.isEquipped) "EQUIPADA" else if (weapon.isMysticalPremium) "MÍSTICA" else "IMPERIAL")
                                    }
                                    Spacer(Modifier.height(3.dp))
                                    Text("Daño ${weapon.damage}  •  Precisión ${weapon.accuracy}  •  Alcance ${weapon.range}", color = TextLight, fontSize = 10.sp)
                                    Spacer(Modifier.height(6.dp))
                                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(onClick = { onEquipWeapon(weapon.id); shopFeedback = "${weapon.name} seleccionada para el combate." }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = if (weapon.isEquipped) GoldVariant else GoldPrimary), contentPadding = PaddingValues(vertical = 5.dp)) {
                                            Text(if (weapon.isEquipped) "EQUIPADA" else "EQUIPAR", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                        OutlinedButton(onClick = { onNavigate(ScreenRoute.Wardrobe) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 5.dp)) { Text("MEJORAR", color = GoldGlow, fontSize = 10.sp) }
                                    }
                                }
                            }
                        }
                    }
                    else -> {
                        items(listOf("Pack Imperial Inicial" to (500L to 4.99), "Pack Rey Guerrero" to (1200L to 9.99), "Pack Emperador Místico" to (3500L to 24.99), "Cofre Supremo de Reyes" to (8000L to 49.99))) { (title, data) ->
                            val (diamonds, price) = data
                            ReferencePanel(Modifier.fillMaxWidth()) {
                                Row(Modifier.fillMaxWidth().padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(title, color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                        Text("+ $diamonds Diamantes  •  + 50,000 Oro", color = CyanMagic, fontSize = 10.sp)
                                        Text("Demo local • recibo persistido", color = TextMuted, fontSize = 9.sp)
                                    }
                                    Button(onClick = { onOpenPaymentModal(title, price) }, colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), contentPadding = PaddingValues(horizontal = 10.dp, vertical = 5.dp)) { Text("\$$price", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                                }
                            }
                        }
                    }
                }
            }
            BottomNavBar(ScreenRoute.Shop, onNavigate)
        }
    }
}
