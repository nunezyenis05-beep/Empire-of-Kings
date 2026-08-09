package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccount
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*

import com.example.ui.components.Mystic3DBackground

@Composable
fun GamesScreen(
    userAccount: UserAccount?,
    onNavigate: (ScreenRoute) -> Unit
) {
    var gameLog by remember { mutableStateOf<String?>(null) }

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
                Text("SALA DE JUEGOS Y ARENAS SECUNDARIAS", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                Text("Compite en juegos de mesa imperiales para ganar oro y coronas extra.", color = TextMuted, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(10.dp))

                gameLog?.let {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1430))
                    ) {
                        Text(it, color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 12.sp, modifier = Modifier.padding(10.dp))
                    }
                }

                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        MiniGameCard("Ajedrez Imperial", "Estrategia por el Trono", "💰 +5,000 Oro") {
                            gameLog = "¡Has ganado la partida de Ajedrez Imperial! Recompensa: +5,000 Oro."
                        }
                    }

                    item {
                        MiniGameCard("Duelo de Cartas Rúnicas", "Mazo de Hechizos Místicos", "💎 +20 Diamantes") {
                            gameLog = "¡Duelo de Cartas Rúnicas completado! Recompensa: +20 Diamantes."
                        }
                    }

                    item {
                        MiniGameCard("Dominó de los Leones", "Mesa Tradicional Cubana Mística", "💰 +3,500 Oro") {
                            gameLog = "¡Dominó Imperial Dominado! Recompensa: +3,500 Oro."
                        }
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.Games, onNavigate = onNavigate)
        }
    }
}

@Composable
private fun MiniGameCard(title: String, desc: String, reward: String, onPlay: () -> Unit) {
    Card(
        modifier = Modifier
            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(title, color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(desc, color = TextLight, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(reward, color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onPlay,
                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("JUGAR MESA", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
            }
        }
    }
}
