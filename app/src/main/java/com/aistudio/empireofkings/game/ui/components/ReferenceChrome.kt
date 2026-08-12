package com.aistudio.empireofkings.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.ui.theme.GoldBorder
import com.aistudio.empireofkings.game.ui.theme.GoldGlow
import com.aistudio.empireofkings.game.ui.theme.GoldPrimary
import com.aistudio.empireofkings.game.ui.theme.ObsidianSurface
import com.aistudio.empireofkings.game.ui.theme.TextMuted

/** Shared visual language from the original mobile reference: compact, imperial,
 * blue-black panels with restrained gold framing. */
@Composable
fun ReferencePanel(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xF0162440), ObsidianSurface.copy(alpha = 0.96f))
                ),
                RoundedCornerShape(8.dp)
            )
            .border(1.dp, GoldBorder.copy(alpha = 0.82f), RoundedCornerShape(8.dp)),
        content = content
    )
}

@Composable
fun ReferenceTitle(
    title: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 2.dp)) {
        Text(
            text = title,
            color = GoldPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Serif,
            letterSpacing = 0.8.sp
        )
        subtitle?.let {
            Text(
                text = it,
                color = TextMuted,
                fontSize = 10.sp,
                lineHeight = 13.sp
            )
        }
    }
}

@Composable
fun ReferenceBadge(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier
            .background(Color(0xCC0A1225), RoundedCornerShape(4.dp))
            .border(1.dp, GoldBorder.copy(alpha = 0.7f), RoundedCornerShape(4.dp))
            .padding(horizontal = 6.dp, vertical = 3.dp),
        color = GoldGlow,
        fontSize = 9.sp,
        fontWeight = FontWeight.Bold
    )
}
