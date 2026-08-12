package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.data.InventoryItem
import com.aistudio.empireofkings.game.data.InventoryRules
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.BottomNavBar
import com.aistudio.empireofkings.game.ui.components.Mystic3DBackground
import com.aistudio.empireofkings.game.ui.components.ReferenceBadge
import com.aistudio.empireofkings.game.ui.components.ReferencePanel
import com.aistudio.empireofkings.game.ui.components.ReferenceTitle
import com.aistudio.empireofkings.game.ui.components.TopBar
import com.aistudio.empireofkings.game.ui.theme.*

@Composable
fun InventoryScreen(
    userAccount: UserAccount?,
    inventoryItems: List<InventoryItem>,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit
) {
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    var selectedType by remember { mutableStateOf("TODOS") }

    val filterOptions = remember(inventoryItems) {
        listOf("TODOS") + inventoryItems.map { it.type }.distinct().sorted()
    }
    val visibleItems = remember(inventoryItems, selectedType) {
        if (selectedType == "TODOS") inventoryItems
        else inventoryItems.filter { it.type == selectedType }
    }
    val blankSlots = if (selectedType == "TODOS") (InventoryRules.MAX_PLAYER_SLOTS - inventoryItems.size).coerceAtLeast(0) else 0

    LaunchedEffect(inventoryItems, selectedType) {
        if (selectedType !in filterOptions) selectedType = "TODOS"
        selectedItem = visibleItems.firstOrNull { it.id == selectedItem?.id } ?: visibleItems.firstOrNull()
    }

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = onOpenPaymentModal
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                ReferenceTitle(
                    title = "INVENTARIO IMPERIAL",
                    subtitle = "Objetos, recursos y tesoros almacenados en tu reino."
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    filterOptions.forEach { type ->
                        val selected = type == selectedType
                        Text(
                            text = type.uppercase(),
                            modifier = Modifier
                                .clickable { selectedType = type }
                                .border(
                                    1.dp,
                                    if (selected) GoldPrimary else GoldBorder.copy(alpha = 0.55f),
                                    RoundedCornerShape(4.dp)
                                )
                                .background(if (selected) Color(0xFF231A3D) else Color(0x660F1526), RoundedCornerShape(4.dp))
                                .padding(horizontal = 7.dp, vertical = 5.dp),
                            color = if (selected) GoldGlow else TextMuted,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(5.dp))
                Text("${visibleItems.size} objetos en ${selectedType.lowercase()}  •  capacidad ${InventoryRules.MAX_PLAYER_SLOTS}  •  ${blankSlots} libres", color = TextMuted, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(6.dp))

                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 142.dp),
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(bottom = 8.dp)
                ) {
                    items(visibleItems, key = { it.id }) { item ->
                        InventoryCard(item = item, selected = item.id == selectedItem?.id, onClick = { selectedItem = item })
                    }
                    if (selectedType == "TODOS") {
                        items(blankSlots) { index ->
                            BlankInventoryCard(slotNumber = visibleItems.size + index + 1)
                        }
                    }
                }

                selectedItem?.let { item ->
                    ReferencePanel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 78.dp, max = 112.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(itemIcon(item.type), fontSize = 29.sp, color = rarityColor(item.rarity))
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.name, color = GoldGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Text(item.description, color = TextLight, fontSize = 10.sp, maxLines = 2)
                                Text("${item.type}  •  ${item.rarity}  •  x${item.quantity}", color = TextMuted, fontSize = 9.sp)
                            }
                            Text("SELECCIONADO", color = GoldPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.Inventory, onNavigate = onNavigate)
        }
    }
}

@Composable
private fun InventoryCard(item: InventoryItem, selected: Boolean, onClick: () -> Unit) {
    ReferencePanel(
        modifier = Modifier
            .height(126.dp)
            .clickable(onClick = onClick)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) GoldPrimary else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(item.type.uppercase(), color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                Text("x${item.quantity}", color = CyanMagic, fontSize = 10.sp, fontWeight = FontWeight.Black)
            }
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(itemIcon(item.type), color = rarityColor(item.rarity), fontSize = 34.sp)
            }
            Text(item.name, color = if (selected) GoldGlow else TextLight, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(item.rarity, color = rarityColor(item.rarity), fontSize = 9.sp)
        }
    }
}

@Composable
private fun BlankInventoryCard(slotNumber: Int) {
    ReferencePanel(modifier = Modifier.height(126.dp)) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Text("SLOT ${slotNumber.toString().padStart(3, '0')}", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("LIBRE", color = GoldBorder, fontSize = 11.sp)
            Text("Asignable por creador/admin", color = TextMuted, fontSize = 8.sp)
        }
    }
}

private fun itemIcon(type: String): String = when {
    type.contains("Cristal", true) -> "◇"
    type.contains("Fragmento", true) -> "✦"
    type.contains("Vestuario", true) -> "♙"
    else -> "◈"
}

private fun rarityColor(rarity: String): Color = when {
    rarity.contains("Mít", true) -> Color(0xFFFF73E6)
    rarity.contains("Legend", true) -> GoldPrimary
    rarity.contains("Épic", true) -> MysticPurple
    rarity.contains("Rar", true) -> CyanMagic
    else -> TextMuted
}
