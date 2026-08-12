package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
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
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.*
import com.aistudio.empireofkings.game.ui.theme.*

private val avatarChoices = listOf(
    "king_warrior" to "REY GUERRERO",
    "royal_guard" to "GUARDIA REAL",
    "arcane_queen" to "REINA ARCANA"
)

@Composable
fun ProfileScreen(
    userAccount: UserAccount?,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onSaveProfile: (String, String, String, String, (Boolean) -> Unit) -> Unit
) {
    val user = userAccount ?: UserAccount()
    var username by remember { mutableStateOf(user.username) }
    var bio by remember { mutableStateOf(user.profileBio) }
    var presence by remember { mutableStateOf(user.presenceStatus) }
    var avatarPreset by remember { mutableStateOf(user.avatarPreset) }
    var feedback by remember { mutableStateOf<String?>(null) }
    var editing by remember { mutableStateOf(false) }

    LaunchedEffect(user.id, user.username, user.profileBio, user.presenceStatus, user.avatarPreset) {
        username = user.username
        bio = user.profileBio
        presence = user.presenceStatus
        avatarPreset = user.avatarPreset
    }

    Mystic3DBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(userAccount, { }, onOpenPaymentModal)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    ReferenceTitle("PERFIL DEL IMPERIO", "Identidad, progreso y avatar guardados localmente")
                }
                item {
                    ReferencePanel(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                AvatarPreview3D(avatarPreset, Modifier.size(84.dp))
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(user.username, color = GoldPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                                    Text(user.title, color = CyanMagic, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Text("${user.presenceStatus} • NIVEL ${user.level}", color = TextMuted, fontSize = 10.sp)
                                    Text("CLAN: ${user.clanName}", color = TextMuted, fontSize = 10.sp)
                                }
                                ReferenceBadge(if (user.isVip) "VIP ACTIVO" else "GUARDIÁN")
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(user.profileBio, color = TextLight, fontSize = 11.sp, lineHeight = 14.sp)
                        }
                    }
                }
                item {
                    ReferencePanel(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                ReferenceTitle("IDENTIDAD", "Los cambios se validan y persisten en Room", Modifier.weight(1f))
                                TextButton(onClick = { editing = !editing }) { Text(if (editing) "CERRAR" else "EDITAR", color = GoldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            }
                            if (editing) {
                                OutlinedTextField(
                                    value = username, onValueChange = { username = it.take(24) }, label = { Text("Nombre") }, singleLine = true,
                                    modifier = Modifier.fillMaxWidth(), colors = profileFieldColors()
                                )
                                Spacer(Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = bio, onValueChange = { bio = it.take(120) }, label = { Text("Bio") }, minLines = 2, maxLines = 3,
                                    modifier = Modifier.fillMaxWidth(), colors = profileFieldColors()
                                )
                                Spacer(Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = presence, onValueChange = { presence = it.take(24) }, label = { Text("Estado") }, singleLine = true,
                                    modifier = Modifier.fillMaxWidth(), colors = profileFieldColors()
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("AVATAR HUMANO", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Text("Cada recuadro muestra un solo avatar; render nativo GLB animado.", color = TextMuted, fontSize = 9.sp)
                                Row(Modifier.fillMaxWidth().padding(top = 6.dp), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    avatarChoices.forEach { (preset, label) ->
                                        AvatarChoice(preset, label, avatarPreset == preset) { avatarPreset = preset }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Button(
                                    onClick = { onSaveProfile(username, bio, presence, avatarPreset) { ok -> feedback = if (ok) "Perfil guardado en este dispositivo." else "Revisa nombre, bio y estado." } },
                                    modifier = Modifier.fillMaxWidth().height(40.dp), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                ) { Text("GUARDAR PERFIL", color = Color.Black, fontWeight = FontWeight.Black, fontSize = 10.sp) }
                                feedback?.let { Text(it, color = GoldGlow, fontSize = 10.sp, modifier = Modifier.padding(top = 5.dp)) }
                            } else {
                                Text("Pulsa EDITAR para cambiar nombre, bio, estado o avatar. La selección se recupera al volver a abrir la app.", color = TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
                item {
                    ReferencePanel(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            ReferenceTitle("ESTADÍSTICAS DEL IMPERIO")
                            Divider(color = GoldBorder.copy(alpha = .3f), modifier = Modifier.padding(vertical = 7.dp))
                            StatRow("Victorias totales", "${user.totalWins}")
                            StatRow("Bajas totales", "${user.totalKills}")
                            StatRow("Proporción K/D", "${(user.totalKills.toFloat() / user.totalWins.coerceAtLeast(1)).let { "%.1f".format(it) }}")
                            StatRow("Pase de batalla", "Nivel ${user.battlePassLevel}")
                            LinearProgressIndicator(progress = (user.battlePassLevel / 50f).coerceIn(0f, 1f), modifier = Modifier.fillMaxWidth().height(6.dp), color = GoldPrimary, trackColor = Color(0xFF101628))
                            StatRow("Esencia mística", "${user.mysticalEssence}")
                            StatRow("Oro imperial", "${user.goldCoins}")
                            StatRow("Coronas", "${user.coronasDiamonds}")
                        }
                    }
                }
            }
            BottomNavBar(ScreenRoute.Profile, onNavigate)
        }
    }
}

@Composable
private fun AvatarPreview3D(preset: String, modifier: Modifier = Modifier) {
    Box(modifier.clip(CircleShape).border(2.dp, GoldPrimary, CircleShape).background(Color(0xFF1E1430))) {
        HumanAvatar3D(
            preset = preset,
            modifier = Modifier.fillMaxSize(),
            showLoadingLabel = false
        )
    }
}

@Composable
private fun RowScope.AvatarChoice(preset: String, label: String, selected: Boolean, onClick: () -> Unit) {
    Column(
        Modifier.weight(1f).clip(RoundedCornerShape(7.dp)).background(if (selected) Color(0xFF241B40) else Color(0xFF0D1425)).border(1.dp, if (selected) GoldPrimary else GoldBorder.copy(alpha = .5f), RoundedCornerShape(7.dp)).clickable { onClick() }.padding(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvatarPreview3D(preset, Modifier.size(50.dp))
        Text(label, color = if (selected) GoldGlow else TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun profileFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextLight, unfocusedTextColor = TextLight,
    focusedBorderColor = GoldPrimary, unfocusedBorderColor = GoldBorder.copy(alpha = .6f),
    focusedLabelColor = GoldGlow, unfocusedLabelColor = TextMuted,
    cursorColor = GoldPrimary
)

@Composable
private fun StatRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = TextLight, fontSize = 11.sp)
        Text(value, color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    }
}
