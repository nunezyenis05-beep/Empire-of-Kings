package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.R
import com.example.ui.theme.CyanMagic
import com.example.ui.theme.GoldGlow
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MysticPurple
import kotlin.math.sin

private data class Particle(
    val xRatio: Float,
    var yRatio: Float,
    val speed: Float,
    val radius: Float,
    val color: Color,
    val phase: Float
)

@Composable
fun Mystic3DBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "Mystic3DMotion")

    val runeRotationState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "runeRotation"
    )

    val pulseGlowState = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    val particleTimeState = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 100f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particleTime"
    )

    // Generate fixed particle set
    val particles = remember {
        List(35) { index ->
            Particle(
                xRatio = (index * 0.029f) % 1.0f,
                yRatio = (index * 0.033f) % 1.0f,
                speed = 0.002f + (index % 5) * 0.001f,
                radius = 3f + (index % 4) * 2.5f,
                color = if (index % 2 == 0) GoldPrimary else CyanMagic,
                phase = index * 0.5f
            )
        }
    }

    Box(modifier = modifier.fillMaxSize().background(Color(0xFF080B14))) {
        // 1. FULL LOBBY BACKGROUND IMAGE
        Image(
            painter = painterResource(id = R.drawable.img_empire_lobby_bg_1786249131349),
            contentDescription = "Full Empire Lobby Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. ATMOSPHERIC GRADIENTS & PULSING LIGHTS
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0x88080B14),
                            Color(0x33080B14),
                            Color(0xDD080B14)
                        )
                    )
                )
        )

        // Radial Magic Glow In Center with Draw-phase state read
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = pulseGlowState.value
                }
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            MysticPurple.copy(alpha = 0.45f),
                            GoldGlow.copy(alpha = 0.20f),
                            Color.Transparent
                        )
                    )
                )
        )

        // 3. CANVAS FOR FLOATING 3D EMBERS & ROTATING MAGICAL RUNES
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val currentRotation = runeRotationState.value
            val pTime = particleTimeState.value

            // Draw Background Rotating Magic Rune Circles
            rotate(currentRotation, pivot = Offset(width * 0.85f, height * 0.25f)) {
                drawCircle(
                    color = GoldPrimary.copy(alpha = 0.15f),
                    radius = 180f,
                    center = Offset(width * 0.85f, height * 0.25f)
                )
                drawCircle(
                    color = CyanMagic.copy(alpha = 0.10f),
                    radius = 140f,
                    center = Offset(width * 0.85f, height * 0.25f)
                )
            }

            rotate(-currentRotation * 0.7f, pivot = Offset(width * 0.15f, height * 0.75f)) {
                drawCircle(
                    color = MysticPurple.copy(alpha = 0.20f),
                    radius = 220f,
                    center = Offset(width * 0.15f, height * 0.75f)
                )
            }

            // Draw Embers floating upward
            particles.forEach { p ->
                val currentY = ((p.yRatio - (pTime * p.speed)) % 1.0f + 1.0f) % 1.0f
                val sineX = sin(pTime * 0.1f + p.phase) * 20f
                val px = (p.xRatio * width) + sineX
                val py = currentY * height

                drawCircle(
                    color = p.color.copy(alpha = 0.75f),
                    radius = p.radius,
                    center = Offset(px, py)
                )
            }
        }

        // 4. MAIN CONTENT
        content()
    }
}
