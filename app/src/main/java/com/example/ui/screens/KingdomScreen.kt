package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun KingdomScreen(
    userAccount: UserAccount?,
    kingdomState: KingdomGameState,
    onNavigate: (ScreenRoute) -> Unit,
    onSaveKingdom: (KingdomGameState) -> Unit,
    onResetKingdom: () -> Unit
) {
    // Keep local interactive state for changes before saving, or we can write-back immediately.
    // To allow "GUARDAR PARTIDA" and "CARGAR PARTIDA" explicitly, we can maintain a local copy of state
    // initialized from the database flow, and let the user modify it locally, then save.
    var localState by remember(kingdomState) { mutableStateOf(kingdomState) }

    // Selected item inside the map: either a coordinate, a resource id, or a building id
    var selectedResourceId by remember { mutableStateOf<String?>(null) }
    var selectedBuildingId by remember { mutableStateOf<String?>(null) }

    // Harvesting toast message feedback
    var harvestFeedbackMessage by remember { mutableStateOf<String?>(null) }

    // Trigger effect to clear feedback message after 2.5 seconds
    LaunchedEffect(harvestFeedbackMessage) {
        if (harvestFeedbackMessage != null) {
            delay(2500)
            harvestFeedbackMessage = null
        }
    }

    Scaffold(
        bottomBar = {
            BottomNavBar(currentRoute = ScreenRoute.Kingdom, onNavigate = onNavigate)
        },
        containerColor = ImperialDarkBg
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header TopBar
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = {}
            )

            // Active Game Resources panel (Wood, Stone, Gold, Mana)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0C101E))
                    .border(1.dp, GoldBorder.copy(alpha = 0.5f))
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ResourceStatBadge("🌲 Madera", localState.woodStored.toString(), CyanMagic)
                ResourceStatBadge("🪨 Piedra", localState.stoneStored.toString(), TextLight)
                ResourceStatBadge("🪙 Oro Reino", localState.goldStored.toString(), GoldPrimary)
                ResourceStatBadge("🔮 Esencia", localState.manaStored.toString(), MysticPurple)
            }

            // Central Game area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Screen Title
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "MI REINO PROVINCIAL",
                            color = GoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Beta móvil - Gestiona recursos y expande tu imperio",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = {
                                onSaveKingdom(localState)
                                harvestFeedbackMessage = "💾 ¡Partida guardada localmente!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("GUARDAR", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                onResetKingdom()
                                harvestFeedbackMessage = "🔄 ¡Mapa reiniciado al estado inicial!"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ImperialCrimson),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("REINICIAR", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Floating Action / Harvest toast
                AnimatedVisibility(
                    visible = harvestFeedbackMessage != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .border(1.dp, GoldBorder, RoundedCornerShape(8.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1035))
                    ) {
                        Text(
                            text = harvestFeedbackMessage ?: "",
                            color = GoldGlow,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

                // 2D 10x10 lightweight Map Grid container
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .border(1.5.dp, GoldBorder, RoundedCornerShape(8.dp))
                        .background(Color(0xFF080B11))
                        .padding(4.dp)
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(10),
                        modifier = Modifier.fillMaxSize(),
                        userScrollEnabled = false
                    ) {
                        // 100 tiles (10x10)
                        items(localState.tiles) { tile ->
                            // Find if there is a resource node on this tile
                            val node = localState.resources.find { it.x == tile.x && it.y == tile.y }
                            // Find if there is a building on this tile
                            val building = localState.buildings.find { it.x == tile.x && it.y == tile.y }

                            val isSelected = (node != null && node.id == selectedResourceId) ||
                                             (building != null && building.id == selectedBuildingId)

                            KingdomTileCell(
                                tile = tile,
                                resourceNode = node,
                                building = building,
                                isSelected = isSelected,
                                onClick = {
                                    if (node != null) {
                                        selectedResourceId = node.id
                                        selectedBuildingId = null
                                    } else if (building != null) {
                                        selectedBuildingId = building.id
                                        selectedResourceId = null
                                    } else {
                                        selectedResourceId = null
                                        selectedBuildingId = null
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Interaction HUD details panel
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(95.dp)
                        .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                    ) {
                        if (selectedResourceId != null) {
                            val node = localState.resources.find { it.id == selectedResourceId }
                            if (node != null) {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(node.name, color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text("Recurso: ${node.type.name}", color = TextLight, fontSize = 11.sp)
                                        Text("Cantidad restante: ${node.currentAmount}/${node.initialAmount}", color = TextMuted, fontSize = 10.sp)
                                    }

                                    Button(
                                        onClick = {
                                            if (node.currentAmount > 0) {
                                                val harvestAmount = 15.coerceAtMost(node.currentAmount)
                                                node.currentAmount -= harvestAmount

                                                // Update matching resources stores
                                                localState = when (node.type) {
                                                    ResourceType.WOOD -> localState.copy(
                                                        woodStored = localState.woodStored + harvestAmount
                                                    )
                                                    ResourceType.GOLD -> localState.copy(
                                                        goldStored = localState.goldStored + harvestAmount
                                                    )
                                                    ResourceType.STONE -> localState.copy(
                                                        stoneStored = localState.stoneStored + harvestAmount
                                                    )
                                                    ResourceType.MANA -> localState.copy(
                                                        manaStored = localState.manaStored + harvestAmount
                                                    )
                                                }
                                                harvestFeedbackMessage = "⛏️ ¡Recolectado +$harvestAmount de ${node.name}!"
                                            } else {
                                                harvestFeedbackMessage = "❌ ¡Este recurso está completamente agotado!"
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = CyanMagic),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text("RECOLECTAR", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        } else if (selectedBuildingId != null) {
                            val building = localState.buildings.find { it.id == selectedBuildingId }
                            if (building != null) {
                                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(building.name, color = GoldGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                            Text("Estructura - Nivel: ${building.level}", color = TextLight, fontSize = 11.sp)
                                        }

                                        Button(
                                            onClick = {
                                                if (localState.woodStored >= 50 && localState.goldStored >= 200) {
                                                    val updatedBuildings = localState.buildings.map {
                                                        if (it.id == building.id) it.copy(level = it.level + 1) else it
                                                    }
                                                    localState = localState.copy(
                                                        buildings = updatedBuildings,
                                                        woodStored = localState.woodStored - 50,
                                                        goldStored = localState.goldStored - 200
                                                    )
                                                    harvestFeedbackMessage = "🏛️ ¡Edificio ${building.name} mejorado al Nivel ${building.level + 1}!"
                                                } else {
                                                    harvestFeedbackMessage = "❌ ¡Insuficientes recursos para mejorar! (Requiere 50 Madera, 200 Oro)"
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                        ) {
                                            Text("MEJORAR (🪵50 🪙200)", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        } else {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(Icons.Default.TouchApp, contentDescription = null, tint = TextMuted, modifier = Modifier.size(24.dp))
                                Text(
                                    "Toca un recurso (árbol, veta) o edificio para interactuar",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun KingdomTileCell(
    tile: KingdomTile,
    resourceNode: ResourceNode?,
    building: KingdomBuilding?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val baseColor = when (tile.terrain) {
        TerrainType.GRASS -> Color(0xFF1E3514)   // Grass
        TerrainType.FOREST -> Color(0xFF0F1E10)  // Dense Forest
        TerrainType.MOUNTAIN -> Color(0xFF2C2F33) // Rocky mountain
        TerrainType.WATER -> Color(0xFF102A45)    // Blue River
    }

    // Combine terrain visual, highlight territory with golden border, node overlay or building overlay
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(1.dp)
            .background(baseColor, RoundedCornerShape(2.dp))
            .border(
                width = if (isSelected) 1.5.dp else if (tile.isStartingTerritory) 1.dp else 0.dp,
                color = if (isSelected) GoldPrimary else if (tile.isStartingTerritory) GoldBorder.copy(alpha = 0.5f) else Color.Transparent,
                shape = RoundedCornerShape(2.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        // Icon / Emoji overlays representing contents
        when {
            building != null -> {
                val buildingEmoji = when (building.type) {
                    BuildingType.CASTLE -> "🏰"
                    BuildingType.WOODCUTTER -> "🪓"
                    BuildingType.BARRACKS -> "🛡️"
                    BuildingType.GOLD_MINE -> "⛏️"
                    BuildingType.MAGE_TOWER -> "🧙"
                }
                Text(buildingEmoji, fontSize = 14.sp)
            }
            resourceNode != null -> {
                if (resourceNode.currentAmount > 0) {
                    val resourceEmoji = when (resourceNode.type) {
                        ResourceType.WOOD -> "🌲"
                        ResourceType.GOLD -> "💎"
                        ResourceType.STONE -> "🪨"
                        ResourceType.MANA -> "🔮"
                    }
                    Text(resourceEmoji, fontSize = 14.sp)
                } else {
                    // Exhausted resource stump
                    Text("🪵", fontSize = 10.sp, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
private fun ResourceStatBadge(
    label: String,
    value: String,
    tintColor: Color
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(text = label, color = TextLight, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = tintColor, fontSize = 11.sp, fontWeight = FontWeight.Black)
    }
}
