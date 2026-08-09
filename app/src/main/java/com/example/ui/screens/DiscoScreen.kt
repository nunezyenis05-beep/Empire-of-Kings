package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
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
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*

import com.example.ui.components.Mystic3DBackground

@Composable
fun DiscoScreen(
    userAccount: UserAccount?,
    onNavigate: (ScreenRoute) -> Unit
) {
    var playingTrack by remember { mutableStateOf("👑 Himno de los Reyes de Aetherfall") }
    var danceActionMsg by remember { mutableStateOf("💃 ¡El Rey baila en la Discoteca Imperial!") }

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
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LEFT: DISCO DANCE FLOOR & LIGHTING
                Card(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("DISCOTECA IMPERIAL Y RELIQUIAS", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)

                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(MysticPurple.copy(alpha = 0.5f))
                                .border(2.dp, CyanMagic, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.MusicNote, contentDescription = null, tint = GoldGlow, modifier = Modifier.size(48.dp))
                                Text("DJ IMPERIAL", color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1430))
                        ) {
                            Text(
                                text = danceActionMsg,
                                color = GoldGlow,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(12.dp)
                            )
                        }

                        // Dance emotes
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("🕺 Baile del Trono", "✨ Giro Rúnico", "🔥 Fuego Azul", "👑 Saludo Real").forEach { emote ->
                                Button(
                                    onClick = { danceActionMsg = "✨ ¡Realizando emote: $emote!" },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(emote, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // RIGHT: JUKEBOX SOUNDTRACKS
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("REPRODUCTOR DE MÚSICA REPOSITORIO", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))

                        listOf(
                            "👑 Himno de los Reyes de Aetherfall",
                            "🔥 Batalla de los Tronos (Electro Remix)",
                            "🔮 Marcha Mística de los Leones",
                            "🌌 Noche Imperial en el Santuario"
                        ).forEach { track ->
                            val isPlaying = playingTrack == track
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isPlaying) Color(0xFF231A3D) else Color(0xFF0F1526))
                                    .clickable { playingTrack = track }
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(track, color = if (isPlaying) GoldGlow else TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                if (isPlaying) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = CyanMagic, modifier = Modifier.size(16.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.Disco, onNavigate = onNavigate)
        }
    }
}
