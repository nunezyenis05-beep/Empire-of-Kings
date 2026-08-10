package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.ScreenRoute
import com.example.ui.theme.*

data class NavTabItem(
    val route: ScreenRoute,
    val title: String,
    val icon: ImageVector
)

@Composable
fun BottomNavBar(
    currentRoute: ScreenRoute,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        NavTabItem(ScreenRoute.Lobby, "LOBBY", Icons.Default.Castle),
        NavTabItem(ScreenRoute.Shop, "TIENDA", Icons.Default.ShoppingBag),
        NavTabItem(ScreenRoute.Wardrobe, "ARMARIO", Icons.Default.Shield),
        NavTabItem(ScreenRoute.Kingdom, "REINO", Icons.Default.Map),
        NavTabItem(ScreenRoute.Games, "JUEGOS", Icons.Default.SportsEsports),
        NavTabItem(ScreenRoute.Inventory, "INVENTARIO", Icons.Default.Backpack),
        NavTabItem(ScreenRoute.Profile, "PERFIL", Icons.Default.AccountCircle),
        NavTabItem(ScreenRoute.SettingsAdmin, "CONFIG", Icons.Default.Settings)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFA080B14))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xD0080B14),
                            Color(0xFA101628)
                        )
                    )
                )
                .border(
                    width = 1.dp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(GoldBorder, MysticPurple, GoldBorder)
                    ),
                    shape = androidx.compose.ui.graphics.RectangleShape
                )
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val isSelected = tab.route::class == currentRoute::class

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .padding(vertical = 2.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) Brush.verticalGradient(
                                    colors = listOf(GoldPrimary.copy(alpha = 0.30f), Color.Transparent)
                                ) else Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent)
                                )
                            )
                            .clickable { onNavigate(tab.route) }
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected) GoldPrimary else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(modifier = Modifier.height(2.dp))

                        Text(
                            text = tab.title,
                            color = if (isSelected) GoldGlow else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )

                        if (isSelected) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Box(
                                modifier = Modifier
                                    .width(24.dp)
                                    .height(2.dp)
                                    .background(GoldPrimary, RoundedCornerShape(1.dp))
                            )
                        }
                    }
                }
            }
        }

        // Subtitle disclaimer from reference image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF06080F))
                .padding(vertical = 3.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "ECONOMIA DEMO - NO DINERO REAL",
                color = GoldBorder.copy(alpha = 0.7f),
                fontSize = 8.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.sp
            )
        }
    }
}
