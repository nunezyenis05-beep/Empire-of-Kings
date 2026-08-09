package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.InventoryItem
import com.example.data.UserAccount
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*

import com.example.ui.components.Mystic3DBackground

@Composable
fun InventoryScreen(
    userAccount: UserAccount?,
    inventoryItems: List<InventoryItem>,
    onNavigate: (ScreenRoute) -> Unit
) {
    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = { }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Text("INVENTARIO Y MOCHILA RÚNICA DE BATALLA", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                Text("Cofres, Pociones, Cristales y Fragmentos Místicos almacenados.", color = TextMuted, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(10.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(inventoryItems) { item ->
                        Card(
                            modifier = Modifier.border(1.dp, GoldBorder, RoundedCornerShape(10.dp)),
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(item.name, color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text("x${item.quantity}", color = CyanMagic, fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                                Text("${item.type} • ${item.rarity}", color = TextMuted, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(item.description, color = TextLight, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.Inventory, onNavigate = onNavigate)
        }
    }
}
