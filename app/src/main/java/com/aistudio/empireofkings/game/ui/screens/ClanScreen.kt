package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.data.ClanState
import com.aistudio.empireofkings.game.data.FriendUser
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.*
import com.aistudio.empireofkings.game.ui.theme.*

@Composable
fun ClanScreen(
    userAccount: UserAccount?,
    clanState: ClanState,
    friends: List<FriendUser>,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onContribute: ((String) -> Unit) -> Unit,
    onOpenClanChat: () -> Unit
) {
    var feedback by remember { mutableStateOf<String?>(null) }
    val progress = (clanState.weeklyPoints.toFloat() / clanState.weeklyGoal.coerceAtLeast(1L).toFloat()).coerceIn(0f, 1f)

    Mystic3DBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(userAccount, onOpenProfile = { onNavigate(ScreenRoute.Profile) }, onOpenPayment = onOpenPaymentModal)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(10.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { ReferenceTitle("CLAN IMPERIAL", "Progreso persistente y acciones reales del reino.") }
                item {
                    ReferencePanel(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(clanState.clanName, color = GoldPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                                    Text(clanState.announcement, color = TextMuted, fontSize = 10.sp)
                                }
                                ReferenceBadge("NIVEL ${clanState.level}")
                            }
                            Text("GLORIA ${clanState.gloryPoints}  •  APORTES ${clanState.contributionCount}", color = GoldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth().height(7.dp), color = GoldPrimary, trackColor = ObsidianSurface)
                            Text("OBJETIVO SEMANAL ${clanState.weeklyPoints}/${clanState.weeklyGoal}", color = CyanMagic, fontSize = 9.sp)
                            feedback?.let { Text(it, color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(onClick = { onContribute { feedback = it } }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary), contentPadding = PaddingValues(vertical = 6.dp)) {
                                    Text("APORTAR 500 ORO", color = androidx.compose.ui.graphics.Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                                OutlinedButton(onClick = onOpenClanChat, modifier = Modifier.weight(1f), contentPadding = PaddingValues(vertical = 6.dp)) { Text("CHAT CLAN", color = GoldGlow, fontSize = 10.sp) }
                            }
                        }
                    }
                }
                item {
                    ReferencePanel(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(12.dp)) {
                            Text("MIEMBROS DEL CLAN", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(6.dp))
                            MemberRow(userAccount?.username ?: "KING_PLAYER", "Tú • ${userAccount?.presenceStatus ?: "En Lobby"}", true)
                            friends.take(12).forEach { friend -> MemberRow(friend.name, "Nivel ${friend.level} • ${friend.status}", friend.isOnline) }
                        }
                    }
                }
                item { Text("Las invitaciones y mensajes se validan localmente; no se finge una conexión de clan en servidor.", color = TextMuted, fontSize = 9.sp) }
            }
            BottomNavBar(ScreenRoute.Clan, onNavigate)
        }
    }
}

@Composable
private fun MemberRow(name: String, subtitle: String, online: Boolean) {
    Row(Modifier.fillMaxWidth().padding(vertical = 3.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.weight(1f)) {
            Text(name, color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(subtitle, color = TextMuted, fontSize = 9.sp)
        }
        Text(if (online) "● ONLINE" else "○ OFFLINE", color = if (online) CyanMagic else TextMuted, fontSize = 8.sp)
    }
}
