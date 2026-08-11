package com.aistudio.empireofkings.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.data.ChatMessage
import com.aistudio.empireofkings.game.data.FriendUser
import com.aistudio.empireofkings.game.ui.theme.*

@Composable
fun FriendsPanel(
    friends: List<FriendUser>,
    onInvite: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(220.dp)
            .fillMaxHeight()
            .border(1.dp, GoldBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xEC0B0E1B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = "AMIGOS EN LÍNEA (${friends.count { it.isOnline }}/${friends.size})",
                color = GoldPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 4.dp)
            )
            Divider(color = GoldBorder.copy(alpha = 0.3f))
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(vertical = 6.dp)
            ) {
                items(friends) { friend ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x80101628))
                            .padding(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box {
                                HumanAvatar3D(
                                    preset = friendAvatarPreset(friend.name),
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, GoldBorder, CircleShape),
                                    showLoadingLabel = false
                                )
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(if (friend.isOnline) Color.Green else Color.Gray)
                                        .align(Alignment.BottomEnd)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(friend.name, color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                Text(friend.status, color = if (friend.isOnline) CyanMagic else TextMuted, fontSize = 9.sp)
                            }
                        }
                        if (friend.isOnline) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(GoldPrimary)
                                    .clickable { onInvite(friend.name) }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("+ INVITAR", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChatDrawer(
    selectedChannel: String,
    chatMessages: List<ChatMessage>,
    onSelectChannel: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var textInput by remember { mutableStateOf("") }
    Card(
        modifier = modifier
            .width(300.dp)
            .heightIn(min = 260.dp, max = 430.dp)
            .border(1.dp, GoldBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xEC0B0E1B)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text("CHAT IMPERIAL", color = GoldPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                listOf("GLOBAL", "CLAN", "SQUAD").forEach { channel ->
                    FilterChip(
                        selected = channel == selectedChannel,
                        onClick = { onSelectChannel(channel) },
                        label = { Text(channel, fontSize = 8.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Divider(color = GoldBorder.copy(alpha = 0.3f))
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 5.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(chatMessages) { message ->
                    Column(Modifier.fillMaxWidth().background(Color(0x40101628), RoundedCornerShape(4.dp)).padding(4.dp)) {
                        Text(message.senderName, color = GoldGlow, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(message.message, color = TextLight, fontSize = 10.sp)
                    }
                }
            }
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it.take(240) },
                    placeholder = { Text("Escribir...", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f).height(42.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldBorder.copy(alpha = 0.4f)
                    )
                )
                IconButton(
                    onClick = {
                        val message = textInput.trim()
                        if (message.isNotBlank()) onSendMessage(message)
                        textInput = ""
                    },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Enviar", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

private fun friendAvatarPreset(name: String): String = when (name.hashCode().absoluteValue % 3) {
    1 -> "royal_guard"
    2 -> "arcane_queen"
    else -> "king_warrior"
}

private val Int.absoluteValue: Int
    get() = if (this == Int.MIN_VALUE) 0 else kotlin.math.abs(this)
