package com.aistudio.empireofkings.game.ui.components

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.theme.*

/** The nine fixed sections of the portrait reference navigation. */
data class NavTabItem(val route: ScreenRoute, val title: String, val icon: ImageVector)

@Composable
fun BottomNavBar(
    currentRoute: ScreenRoute,
    onNavigate: (ScreenRoute) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        NavTabItem(ScreenRoute.Lobby, "LOBBY", Icons.Default.Castle),
        NavTabItem(ScreenRoute.Shop, "TIENDA", Icons.Default.ShoppingBag),
        NavTabItem(ScreenRoute.Inventory, "INVENTARIO", Icons.Default.Backpack),
        NavTabItem(ScreenRoute.Wardrobe, "VESTUARIO", Icons.Default.Shield),
        NavTabItem(ScreenRoute.Profile, "PERFIL", Icons.Default.AccountCircle),
        NavTabItem(ScreenRoute.Disco, "DISCOTECA", Icons.Default.MusicNote),
        NavTabItem(ScreenRoute.Clan, "CLAN", Icons.Default.Group),
        NavTabItem(ScreenRoute.Games, "JUEGOS", Icons.Default.SportsEsports),
        NavTabItem(ScreenRoute.SettingsAdmin, "AJUSTES", Icons.Default.Settings)
    )

    Column(
        modifier = modifier.fillMaxWidth().background(Color(0xFA080B14))
    ) {
        // Two compact rows keep all nine destinations visible on a narrow phone;
        // the navigation remains fixed rather than becoming an overflow menu.
        listOf(tabs.take(5), tabs.drop(5)).forEach { rowTabs ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(43.dp)
                    .background(
                        Brush.verticalGradient(listOf(Color(0xD0080B14), Color(0xFA101628)))
                    )
                    .border(1.dp, GoldBorder.copy(alpha = 0.65f))
                    .padding(horizontal = 3.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                rowTabs.forEach { tab ->
                    val selected = tab.route::class == currentRoute::class
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (selected) GoldPrimary.copy(alpha = 0.16f) else Color.Transparent)
                            .clickable { onNavigate(tab.route) }
                            .padding(vertical = 2.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (selected) GoldPrimary else TextMuted,
                            modifier = Modifier.size(17.dp)
                        )
                        Text(
                            text = tab.title,
                            color = if (selected) GoldGlow else TextMuted,
                            fontSize = 7.sp,
                            lineHeight = 8.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (selected) {
                            Box(Modifier.width(18.dp).height(2.dp).background(GoldPrimary, RoundedCornerShape(1.dp)))
                        }
                    }
                }
                // Balance the second row so its four tabs do not stretch into
                // an accidental fifth destination.
                repeat(5 - rowTabs.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Text(
            text = "ECONOMIA DEMO • NO DINERO REAL",
            modifier = Modifier.fillMaxWidth().background(Color(0xFF06080F)).padding(vertical = 3.dp),
            color = GoldBorder.copy(alpha = 0.7f),
            fontSize = 8.sp,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 1.sp,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
    }
}
