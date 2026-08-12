package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.data.ChatMessage
import com.aistudio.empireofkings.game.data.FriendUser
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.BottomNavBar
import com.aistudio.empireofkings.game.ui.components.ChatDrawer
import com.aistudio.empireofkings.game.ui.components.FriendsPanel
import com.aistudio.empireofkings.game.ui.components.Mystic3DBackground
import com.aistudio.empireofkings.game.ui.components.TopBar
import com.aistudio.empireofkings.game.ui.theme.*

@Composable
fun MainLobbyScreen(
    userAccount: UserAccount?,
    squadSlots: List<String?>,
    friends: List<FriendUser>,
    selectedChannel: String,
    chatMessages: List<ChatMessage>,
    activeActionText: String?,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onInviteFriend: (String) -> Unit,
    onRemoveSquadMember: (Int) -> Unit,
    onSelectChatChannel: (String) -> Unit,
    onSendChatMessage: (String) -> Unit,
    onTriggerAction: (String) -> Unit
) {
    var showFriendsDrawer by remember { mutableStateOf(false) }
    var showChatDrawer by remember { mutableStateOf(false) }

    Mystic3DBackground(
        modifier = Modifier.fillMaxSize(),
        showLobbyImage = true
    ) {
        // MAIN VIEWPORT CONTENT LAYOUT
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // TOP BAR (~60dp)
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = onOpenPaymentModal
            )

            // CENTER AREA (Main Sanctuary Lobby View with Action Menu on the Right)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Floating Emote Notification Toast in Center
                activeActionText?.let { emoteMsg ->
                    Card(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 16.dp)
                            .border(1.5.dp, GoldPrimary, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = Color(0xEE1E1035))
                    ) {
                        Text(
                            text = emoteMsg,
                            color = GoldGlow,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                        )
                    }
                }

                // Left Side: Small Social Quick Actions (Amigos / Chat)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { showFriendsDrawer = !showFriendsDrawer },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xDD0D1222), CircleShape)
                            .border(1.dp, GoldBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Group,
                            contentDescription = "Amigos",
                            tint = GoldGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    IconButton(
                        onClick = { showChatDrawer = !showChatDrawer },
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xDD0D1222), CircleShape)
                            .border(1.dp, GoldBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = "Chat",
                            tint = GoldGlow,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // RIGHT SIDE: ACTION BUTTONS (JUGAR, BAILAR, SALUDAR, REGALAR)
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .width(180.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.End
                ) {
                    // MAIN JUGAR BUTTON (Yellow / Gold)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .shadow(12.dp, spotColor = GoldGlow)
                            .border(2.5.dp, GoldGlow, RoundedCornerShape(14.dp))
                            .clickable { onNavigate(ScreenRoute.Battle) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(
                                            Color(0xFFFFDF00),
                                            Color(0xFFFFB700)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .background(Color.Black.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
                                        contentDescription = "Jugar",
                                        tint = Color.Black,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "JUGAR",
                                    color = Color.Black,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }
                    }

                    // BAILAR BUTTON
                    LobbyActionButton(
                        title = "BAILAR",
                        icon = Icons.Default.AccessibilityNew,
                        onClick = { onTriggerAction("🕺 ¡El Rey baila en el Trono Real!") }
                    )

                    // SALUDAR BUTTON
                    LobbyActionButton(
                        title = "SALUDAR",
                        icon = Icons.Default.WavingHand,
                        onClick = { onTriggerAction("👋 ¡El Rey saluda a sus Súbditos!") }
                    )

                    // REGALAR BUTTON
                    LobbyActionButton(
                        title = "REGALAR",
                        icon = Icons.Default.CardGiftcard,
                        onClick = { onOpenPaymentModal() }
                    )
                }

                // OVERLAY DRAWERS (FRIENDS & CHAT)
                androidx.compose.animation.AnimatedVisibility(
                    visible = showFriendsDrawer,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 50.dp)
                ) {
                    FriendsPanel(
                        friends = friends,
                        onInvite = { friendName ->
                            onInviteFriend(friendName)
                            showFriendsDrawer = false
                        }
                    )
                }

                androidx.compose.animation.AnimatedVisibility(
                    visible = showChatDrawer,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(bottom = 50.dp)
                ) {
                    ChatDrawer(
                        selectedChannel = selectedChannel,
                        chatMessages = chatMessages,
                        onSelectChannel = onSelectChatChannel,
                        onSendMessage = onSendChatMessage
                    )
                }
            }

            // BOTTOM NAVIGATION BAR
            BottomNavBar(
                currentRoute = ScreenRoute.Lobby,
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
private fun LobbyActionButton(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .border(1.5.dp, GoldBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xEC0B1020))
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = GoldGlow,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                color = TextLight,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
    }
}

