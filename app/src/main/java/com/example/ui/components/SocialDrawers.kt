package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.ChatMessage
import com.example.data.FriendUser
import com.example.ui.theme.*

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box {
                                Image(
                                    painter = painterResource(id = R.drawable.img_king_warrior_1786249144739),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, GoldBorder, CircleShape),
                                    contentScale = ContentScale.Crop
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
                                Text(
                                    text = friend.name,
                                    color = TextLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Text(
                                    text = friend.status,
                                    color = if (friend.isOnline) CyanMagic else TextMuted,
                                    fontSize = 9.sp
                                )
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
                                Text(
                                    text = "+ INVITAR",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
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
            .width(260.dp)
            .height(200.dp)
            .border(1.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xF0080B14)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(6.dp)) {
            // Channels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf("GLOBAL", "CLAN", "SQUAD").forEach { ch ->
                    val isSel = selectedChannel == ch
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) GoldPrimary else Color(0xFF101628))
                            .clickable { onSelectChannel(ch) }
                            .padding(vertical = 3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = ch,
                            color = if (isSel) Color.Black else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Messages list
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                reverseLayout = false
            ) {
                items(chatMessages) { msg ->
                    Text(
                        text = "${msg.senderName}: ${msg.message}",
                        color = GoldGlow,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(vertical = 1.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = textInput,
                    onValueChange = { textInput = it },
                    placeholder = { Text("Escribir...", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(36.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldBorder.copy(alpha = 0.4f)
                    )
                )

                Spacer(modifier = Modifier.width(4.dp))

                IconButton(
                    onClick = {
                        onSendMessage(textInput)
                        textInput = ""
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = GoldPrimary, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
