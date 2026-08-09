package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*

import com.example.ui.components.Mystic3DBackground

@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit
) {
    var isRegisterTab by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("KING_PLAYER") }
    var emailOrPhone by remember { mutableStateOf("king@empire.com") }
    var password by remember { mutableStateOf("******") }
    var acceptedTerms by remember { mutableStateOf(true) }
    var selectedAvatarIndex by remember { mutableIntStateOf(0) }

    Mystic3DBackground {
        Card(
            modifier = Modifier
                .align(Alignment.Center)
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .padding(16.dp)
                .border(2.dp, GoldBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color(0xF00F1526)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "AETHERFALL: EMPIRE OF KINGS",
                    color = GoldPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Serif
                )
                Text(
                    text = if (isRegisterTab) "REGISTRO DE NUEVO REY" else "INICIAR SESIÓN IMPERIAL",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isRegisterTab = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isRegisterTab) GoldPrimary else Color(0xFF1E1430)
                        )
                    ) {
                        Text("LOGIN", color = if (!isRegisterTab) Color.Black else TextLight, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isRegisterTab = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isRegisterTab) GoldPrimary else Color(0xFF1E1430)
                        )
                    ) {
                        Text("REGISTRO", color = if (isRegisterTab) Color.Black else TextLight, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isRegisterTab) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Nombre de Usuario (Rey)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = GoldBorder
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                OutlinedTextField(
                    value = emailOrPhone,
                    onValueChange = { emailOrPhone = it },
                    label = { Text("Correo o Teléfono (+53 / Internacional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = GoldBorder
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (isRegisterTab) {
                    Text("Seleccione Avatar Inicial de Rey:", color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(3) { idx ->
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = if (selectedAvatarIndex == idx) 2.5.dp else 1.dp,
                                        color = if (selectedAvatarIndex == idx) GoldPrimary else Color.Gray,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedAvatarIndex = idx }
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.img_king_warrior_1786249144739),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Checkbox(
                        checked = acceptedTerms,
                        onCheckedChange = { acceptedTerms = it },
                        colors = CheckboxDefaults.colors(checkedColor = GoldPrimary)
                    )
                    Text(
                        text = "Acepto los Términos de Uso, Privacidad y Reglas de la Comunidad.",
                        color = TextLight,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { onAuthSuccess() },
                    enabled = acceptedTerms,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(22.dp)
                ) {
                    Text(
                        text = if (isRegisterTab) "CREAR REINO Y ENTRAR" else "ENTRAR AL LOBBY",
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
