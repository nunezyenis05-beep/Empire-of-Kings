package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.R
import com.aistudio.empireofkings.game.data.EquipmentCatalog
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.data.WardrobeItem
import com.aistudio.empireofkings.game.data.WeaponItem
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.*
import com.aistudio.empireofkings.game.ui.theme.*

@Composable
fun WardrobeScreen(
    userAccount: UserAccount?,
    weapons: List<WeaponItem>,
    wardrobeItems: List<WardrobeItem>,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onEquipWeapon: (String) -> Unit,
    onEquipWardrobeItem: (String) -> Unit,
    onUpgradeWeapon: (WeaponItem, (Boolean, String) -> Unit) -> Unit
) {
    var activeSection by remember { mutableStateOf("VESTUARIO") }
    var selectedItem by remember { mutableStateOf<WardrobeItem?>(null) }
    var selectedWeapon by remember { mutableStateOf<WeaponItem?>(null) }
    var feedback by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(wardrobeItems) {
        selectedItem = wardrobeItems.firstOrNull { it.id == selectedItem?.id } ?: wardrobeItems.firstOrNull()
    }
    LaunchedEffect(weapons) {
        selectedWeapon = weapons.firstOrNull { it.id == selectedWeapon?.id } ?: weapons.firstOrNull()
    }

    Mystic3DBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(userAccount, { onNavigate(ScreenRoute.Profile) }, onOpenPayment)
            ReferenceTitle(
                title = "VESTUARIO IMPERIAL",
                subtitle = "Un avatar humano por selección • render nativo GLB animado",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
            Row(
                modifier = Modifier.padding(horizontal = 12.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("VESTUARIO", "ARMAS").forEach { section ->
                    OutlinedButton(
                        onClick = { activeSection = section },
                        modifier = Modifier.weight(1f).height(36.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (activeSection == section) GoldGlow else TextMuted),
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (activeSection == section) GoldPrimary else GoldBorder.copy(alpha = .6f))
                    ) { Text(section, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(6.dp))

            if (activeSection == "VESTUARIO") {
                WardrobeContent(
                    items = wardrobeItems,
                    selected = selectedItem,
                    onSelect = { selectedItem = it },
                    onEquip = { item -> onEquipWardrobeItem(item.id); feedback = "${item.name} equipado en ${item.slot}." },
                    feedback = feedback,
                    modifier = Modifier.weight(1f)
                )
            } else {
                WeaponContent(
                    weapons = weapons,
                    selected = selectedWeapon,
                    onSelect = { selectedWeapon = it },
                    onEquip = onEquipWeapon,
                    onUpgrade = { weapon, callback -> onUpgradeWeapon(weapon) { ok, msg -> feedback = msg; callback(ok, msg) } },
                    feedback = feedback,
                    modifier = Modifier.weight(1f)
                )
            }
            BottomNavBar(ScreenRoute.Wardrobe, onNavigate)
        }
    }
}

@Composable
private fun WardrobeContent(
    items: List<WardrobeItem>,
    selected: WardrobeItem?,
    onSelect: (WardrobeItem) -> Unit,
    onEquip: (WardrobeItem) -> Unit,
    feedback: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // The collection stays compact above the detail panel on portrait phones.
        ReferencePanel(Modifier.fillMaxWidth().height(218.dp)) {
            Column(Modifier.padding(8.dp)) {
                Text("COLECCIÓN", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("${items.count { it.isOwned }} piezas disponibles", color = TextMuted, fontSize = 9.sp)
                Spacer(Modifier.height(5.dp))
                LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(items, key = { it.id }) { item ->
                        val selectedRow = selected?.id == item.id
                        Row(
                            Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp))
                                .background(if (selectedRow) Color(0xFF241B40) else Color(0xFF0D1425))
                                .border(1.dp, if (item.isEquipped) GoldPrimary else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { onSelect(item) }.padding(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(Modifier.weight(1f)) {
                                Text(item.name, color = if (selectedRow) GoldGlow else TextLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text(item.slot, color = TextMuted, fontSize = 9.sp)
                            }
                            if (item.isEquipped) Text("ACTIVO", color = GoldPrimary, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
        ReferencePanel(Modifier.fillMaxWidth().weight(1f)) {
            selected?.let { item ->
                Column(Modifier.padding(10.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        // One human avatar per selection tile, rendered through the native GLB surface.
                        Box(Modifier.fillMaxWidth().height(132.dp).clip(RoundedCornerShape(8.dp)).border(1.dp, GoldBorder, RoundedCornerShape(8.dp))) {
                            HumanAvatar3D(
                                preset = item.avatarPreset,
                                modifier = Modifier.fillMaxSize(),
                                showLoadingLabel = false,
                                allowCameraGestures = true
                            )
                            Text(
                                EquipmentCatalog.statusLabel(item.id),
                                Modifier.align(Alignment.BottomEnd).background(Color(0xCC080B14)).padding(4.dp),
                                color = if (EquipmentCatalog.definitionFor(item.id)?.modelAsset != null) GoldGlow else TextMuted,
                                fontSize = 8.sp
                            )
                        }
                        Spacer(Modifier.height(7.dp))
                        Text(item.name, color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                        Text("${item.slot} • ${item.rarity}", color = CyanMagic, fontSize = 10.sp)
                        Divider(color = GoldBorder.copy(alpha = .3f), Modifier.padding(vertical = 7.dp))
                        Text(item.description, color = TextLight, fontSize = 11.sp, lineHeight = 14.sp)
                    }
                    Column {
                        feedback?.let { Text(it, color = GoldGlow, fontSize = 10.sp, modifier = Modifier.padding(bottom = 5.dp)) }
                        Button(
                            onClick = { onEquip(item) }, enabled = item.isOwned && !item.isEquipped,
                            modifier = Modifier.fillMaxWidth().height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = if (item.isEquipped) Color(0xFF26324A) else GoldPrimary)
                        ) { Text(if (item.isEquipped) "EQUIPADO" else "EQUIPAR ${item.slot.uppercase()}", color = if (item.isEquipped) TextMuted else Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp) }
                    }
                }
            } ?: Text("Cargando colección…", color = TextMuted, modifier = Modifier.padding(12.dp))
        }
    }
}

@Composable
private fun WeaponContent(
    weapons: List<WeaponItem>, selected: WeaponItem?, onSelect: (WeaponItem) -> Unit,
    onEquip: (String) -> Unit, onUpgrade: (WeaponItem, (Boolean, String) -> Unit) -> Unit,
    feedback: String?, modifier: Modifier = Modifier
) {
    Column(modifier.fillMaxWidth().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReferencePanel(Modifier.fillMaxWidth().height(218.dp)) {
            Column(Modifier.padding(8.dp)) {
                Text("FORJA", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                    items(weapons, key = { it.id }) { weapon ->
                        Row(Modifier.fillMaxWidth().clip(RoundedCornerShape(6.dp)).background(if (selected?.id == weapon.id) Color(0xFF241B40) else Color(0xFF0D1425)).clickable { onSelect(weapon) }.padding(6.dp)) {
                            Column(Modifier.weight(1f)) {
                                Text(weapon.name, color = TextLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("${weapon.category} • N${weapon.level}", color = TextMuted, fontSize = 9.sp)
                            }
                            if (weapon.isEquipped) Text("ACTIVA", color = GoldPrimary, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
        ReferencePanel(Modifier.fillMaxWidth().weight(1f)) {
            selected?.let { weapon ->
                Column(Modifier.padding(10.dp).fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(weapon.name, color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                        Text("NIVEL ${weapon.level}/15 • ${if (weapon.isMysticalPremium) "MÍSTICA" else "NORMAL"}", color = CyanMagic, fontSize = 10.sp)
                        Divider(color = GoldBorder.copy(alpha = .3f), Modifier.padding(vertical = 7.dp))
                        Text(weapon.description, color = TextLight, fontSize = 11.sp)
                        Spacer(Modifier.height(8.dp))
                        StatBar("Daño", weapon.damage, 120, GoldPrimary)
                        StatBar("Cadencia", weapon.fireRate, 100, CyanMagic)
                        StatBar("Alcance", weapon.range, 100, MysticPurple)
                        StatBar("Precisión", weapon.accuracy, 100, GoldGlow)
                    }
                    Column {
                        feedback?.let { Text(it, color = GoldGlow, fontSize = 10.sp, modifier = Modifier.padding(bottom = 5.dp)) }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            OutlinedButton(onClick = { onEquip(weapon.id) }, Modifier.weight(1f).height(40.dp), border = androidx.compose.foundation.BorderStroke(1.dp, GoldBorder)) { Text("EQUIPAR", color = GoldGlow, fontSize = 10.sp) }
                            Button(onClick = { onUpgrade(weapon) { _, _ -> } }, Modifier.weight(1.2f).height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)) { Text("MEJORAR", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatBar(label: String, current: Int, max: Int, color: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextLight, fontSize = 9.sp, modifier = Modifier.width(58.dp))
        LinearProgressIndicator(progress = (current.toFloat() / max).coerceIn(0f, 1f), modifier = Modifier.weight(1f).height(5.dp), color = color, trackColor = Color(0xFF101628))
        Text("$current", color = color, fontSize = 9.sp, modifier = Modifier.padding(start = 5.dp))
    }
}
