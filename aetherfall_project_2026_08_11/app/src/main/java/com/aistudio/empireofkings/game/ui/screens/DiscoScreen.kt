package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.PlayArrow
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
import com.aistudio.empireofkings.game.R
import com.aistudio.empireofkings.game.data.DiscoState
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.BottomNavBar
import com.aistudio.empireofkings.game.ui.components.HumanAvatar3D
import com.aistudio.empireofkings.game.ui.components.Mystic3DBackground
import com.aistudio.empireofkings.game.ui.components.ReferencePanel
import com.aistudio.empireofkings.game.ui.components.ReferenceTitle
import com.aistudio.empireofkings.game.ui.components.TopBar
import com.aistudio.empireofkings.game.ui.theme.*

private data class DiscoTrack(val id: String, val title: String, val source: String)
private data class DiscoEmote(val id: String, val label: String, val message: String)

private val tracks = listOf(
    DiscoTrack("anthem", "Himno de los Reyes", "Audio reemplazable · Aetherfall"),
    DiscoTrack("thrones", "Batalla de los Tronos", "Electro remix · biblioteca local"),
    DiscoTrack("lions", "Marcha Mística de los Leones", "Marcha · biblioteca local"),
    DiscoTrack("sanctuary", "Noche Imperial en el Santuario", "Ambient · biblioteca local")
)
private val emotes = listOf(
    DiscoEmote("throne_dance", "BAILE DEL TRONO", "El Rey baila en la Discoteca Imperial."),
    DiscoEmote("rune_spin", "GIRO RÚNICO", "El Rey activa un giro rúnico."),
    DiscoEmote("blue_fire", "FUEGO AZUL", "El Rey invoca una llamarada azul."),
    DiscoEmote("royal_wave", "SALUDO REAL", "El Rey saluda a sus súbditos.")
)

@Composable
fun DiscoScreen(
    userAccount: UserAccount?,
    discoState: DiscoState,
    musicEnabled: Boolean,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onSelectTrack: (String) -> Unit,
    onSelectEmote: (String) -> Unit
) {
    val selectedTrack = tracks.firstOrNull { it.id == discoState.selectedTrackId } ?: tracks.first()
    val selectedEmote = emotes.firstOrNull { it.id == discoState.selectedEmoteId } ?: emotes.first()

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(userAccount = userAccount, onOpenProfile = { onNavigate(ScreenRoute.Profile) }, onOpenPayment = onOpenPaymentModal)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                item {
                    ReferenceTitle("DISCOTECA IMPERIAL")
                    Text("Pista social · estado guardado en este dispositivo", color = TextMuted, fontSize = 10.sp)
                }
                item {
                    ReferencePanel {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            HumanAvatar3D(
                                preset = userAccount?.avatarPreset ?: "king_warrior",
                                modifier = Modifier.size(78.dp).clip(CircleShape).border(2.dp, GoldBorder, CircleShape),
                                showLoadingLabel = false
                            )
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text("PISTA DEL TRONO", color = GoldPrimary, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text(selectedEmote.message, color = GoldGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Text("Emotes realizados: ${discoState.emoteCount}", color = TextMuted, fontSize = 10.sp)
                            }
                            Icon(Icons.Default.MusicNote, contentDescription = "Música", tint = CyanMagic, modifier = Modifier.size(26.dp))
                        }
                    }
                }
                item {
                    ReferencePanel {
                        Text("EMOTES IMPERIALES", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(Modifier.height(6.dp))
                        emotes.chunked(2).forEach { row ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                row.forEach { emote ->
                                    OutlinedButton(
                                        onClick = { onSelectEmote(emote.id) },
                                        modifier = Modifier.weight(1f),
                                        border = BorderStroke(1.dp, if (emote.id == selectedEmote.id) GoldPrimary else GoldBorder),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = if (emote.id == selectedEmote.id) GoldPrimary else TextLight),
                                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                                    ) { Text(emote.label, fontSize = 9.sp, fontWeight = FontWeight.Bold, maxLines = 1) }
                                }
                                if (row.size == 1) Spacer(Modifier.weight(1f))
                            }
                            Spacer(Modifier.height(5.dp))
                        }
                    }
                }
                item {
                    ReferencePanel {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("REPRODUCTOR", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(if (musicEnabled) "AUDIO ACTIVO" else "AUDIO DESACTIVADO", color = if (musicEnabled) CyanMagic else TextMuted, fontSize = 9.sp)
                        }
                        Text("El paquete de audio es reemplazable; esta selección no inventa una pista instalada.", color = TextMuted, fontSize = 9.sp)
                        Spacer(Modifier.height(6.dp))
                        tracks.forEach { track ->
                            val selected = track.id == selectedTrack.id
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp).clip(RoundedCornerShape(7.dp))
                                    .background(if (selected) Color(0xFF231A3D) else Color(0xFF0F1526))
                                    .border(1.dp, if (selected) GoldBorder else Color.Transparent, RoundedCornerShape(7.dp))
                                    .clickable { onSelectTrack(track.id) }.padding(9.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.PlayArrow, contentDescription = if (selected) "Seleccionado" else "Seleccionar", tint = if (selected) GoldPrimary else TextMuted, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(7.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(track.title, color = if (selected) GoldGlow else TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(track.source, color = TextMuted, fontSize = 9.sp)
                                }
                                if (selected) Text("ACTIVA", color = CyanMagic, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            BottomNavBar(currentRoute = ScreenRoute.Disco, onNavigate = onNavigate)
        }
    }
}
