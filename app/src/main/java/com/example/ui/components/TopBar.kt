package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.UserAccount
import com.example.ui.theme.*

@Composable
fun TopBar(
    userAccount: UserAccount?,
    onOpenProfile: () -> Unit,
    onOpenPayment: () -> Unit,
    modifier: Modifier = Modifier
) {
    val gold = userAccount?.goldCoins ?: 1250000L
    val diamonds = userAccount?.coronasDiamonds ?: 500L
    val name = userAccount?.username ?: "KING_PLAYER"
    val level = userAccount?.level ?: 99

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Color(0xFA080B14))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(GoldBorder.copy(alpha = 0.8f), MysticPurple.copy(alpha = 0.5f), GoldBorder.copy(alpha = 0.8f))
                ),
                shape = androidx.compose.ui.graphics.RectangleShape
            )
            .padding(horizontal = 12.dp, vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // LEFT: Profile Avatar & Level (Matching reference image)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenProfile() }
            ) {
                // Circular Crown Avatar Frame
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .border(2.dp, GoldPrimary, CircleShape)
                        .background(Color(0xFF1E1430)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_king_warrior_1786249144739),
                        contentDescription = "Avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = name,
                        color = TextLight,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Serif,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "LVL $level • ELITE ✦",
                        color = GoldGlow,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // CENTER: EMPIRE OF KINGS Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "EMPIRE ",
                    color = GoldPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "OF ",
                    color = GoldBorder,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = "KINGS",
                    color = GoldPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
            }

            // RIGHT: Currency Badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Gold Coin Badge (DM 1,250,000)
                CurrencyBadge(
                    tag = "DM",
                    isGold = true,
                    amountText = String.format("%,d", gold),
                    onAdd = { onOpenPayment() }
                )

                // Diamond Badge (COR 500)
                CurrencyBadge(
                    tag = "COR",
                    isGold = false,
                    amountText = "$diamonds",
                    onAdd = { onOpenPayment() }
                )
            }
        }
    }
}

@Composable
private fun CurrencyBadge(
    tag: String,
    isGold: Boolean,
    amountText: String,
    onAdd: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xDD0C101E))
            .border(1.5.dp, GoldBorder, RoundedCornerShape(8.dp))
            .clickable { onAdd() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(if (isGold) GoldPrimary else CyanMagic),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = tag,
                color = Color.Black,
                fontSize = 8.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = if (isGold) amountText else "COR $amountText",
            color = if (isGold) GoldGlow else CyanMagic,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

