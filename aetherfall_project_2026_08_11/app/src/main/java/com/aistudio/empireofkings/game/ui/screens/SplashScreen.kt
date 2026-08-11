package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.R
import com.aistudio.empireofkings.game.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var progress by remember { mutableFloatStateOf(0f) }
    var statusText by remember { mutableStateOf("Conectando con el Servidor Imperial...") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "logoPulse"
    )

    LaunchedEffect(Unit) {
        delay(300)
        progress = 0.25f
        statusText = "Verificando Credenciales de Rey..."
        delay(600)
        progress = 0.60f
        statusText = "Cargando Texturas de Santuario y Armas Místicas..."
        delay(700)
        progress = 0.90f
        statusText = "Sincronizando Estado del Reino..."
        delay(500)
        progress = 1.0f
        statusText = "¡Bienvenido a EMPIRE OF KINGS!"
        delay(400)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0B0E1B),
                        Color(0xFF101628),
                        Color(0xFF05070E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_empire_lobby_bg_1786249131349),
            contentDescription = "Splash BG",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            alpha = 0.35f
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .scale(logoScale)
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(3.dp, GoldPrimary, CircleShape)
                    .background(Color(0x99000000)),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_app_icon_1786249120706),
                    contentDescription = "App Icon Logo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "EMPIRE OF KINGS",
                color = GoldPrimary,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Serif,
                letterSpacing = 2.sp
            )

            Text(
                text = "AETHERFALL • JUEGO MULTI-JUGADOR MÍSTICO",
                color = CyanMagic,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(36.dp))

            Column(
                modifier = Modifier.width(320.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .border(1.dp, GoldBorder, RoundedCornerShape(4.dp)),
                    color = GoldPrimary,
                    trackColor = Color(0xFF1E1430)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = statusText,
                    color = TextLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
