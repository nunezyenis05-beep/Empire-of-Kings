package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.R
import com.example.data.UserAccount
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*

import com.example.ui.components.Mystic3DBackground

@Composable
fun ProfileScreen(
    userAccount: UserAccount?,
    onNavigate: (ScreenRoute) -> Unit
) {
    val user = userAccount ?: UserAccount()

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { },
                onOpenPayment = { }
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // AVATAR & PASS CARD
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp).fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .border(3.dp, GoldPrimary, CircleShape)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.img_king_warrior_1786249144739),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(user.username, color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                        Text(user.title, color = CyanMagic, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("CLAN: ${user.clanName}", color = TextMuted, fontSize = 11.sp)

                        Spacer(modifier = Modifier.height(12.dp))

                        Text("PASE DE BATALLA REYES: NIVEL ${user.battlePassLevel}", color = GoldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        LinearProgressIndicator(
                            progress = 0.85f,
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = GoldPrimary,
                            trackColor = Color(0xFF101628)
                        )
                    }
                }

                // STATS
                Card(
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("ESTADÍSTICAS DEL IMPERIO", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                        Divider(color = GoldBorder.copy(alpha = 0.3f), modifier = Modifier.padding(vertical = 8.dp))

                        StatRow("Victorias Totales en Batalla", "${user.totalWins} B victorias")
                        StatRow("Bajas Totales (Kills)", "${user.totalKills} eliminaciones")
                        StatRow("Proporción K/D", "11.3")
                        StatRow("Rango Actual", "Gran Rey Supremo V")
                        StatRow("Esencia Mística Almacenada", "${user.mysticalEssence} esencia")
                        StatRow("Estado VIP Imperial", if (user.isVip) "ACTIVO" else "INACTIVO")
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.Profile, onNavigate = onNavigate)
        }
    }
}

@Composable
private fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = TextLight, fontSize = 12.sp)
        Text(value, color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
