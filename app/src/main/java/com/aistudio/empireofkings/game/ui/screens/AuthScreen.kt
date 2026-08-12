package com.aistudio.empireofkings.game.ui.screens

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
import com.aistudio.empireofkings.game.R
import com.aistudio.empireofkings.game.ui.theme.*

import com.aistudio.empireofkings.game.ui.components.HumanAvatar3D
import com.aistudio.empireofkings.game.ui.components.Mystic3DBackground

@Composable
fun AuthScreen(
    onAuthSuccess: (username: String, password: String, register: Boolean, avatarPreset: String) -> Unit
) {
    var isRegisterTab by remember { mutableStateOf(false) }
    var username by remember { mutableStateOf("") }
    var emailOrPhone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var acceptedTerms by remember { mutableStateOf(false) }
    var selectedAvatarIndex by remember { mutableIntStateOf(0) }
    var validationMessage by remember { mutableStateOf<String?>(null) }

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
                                HumanAvatar3D(
                                    preset = when (idx) {
                                        1 -> "royal_guard"
                                        2 -> "arcane_queen"
                                        else -> "king_warrior"
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    showLoadingLabel = false
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

                validationMessage?.let {
                    Text(
                        text = it,
                        color = ImperialCrimson,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val cleanUsername = username.trim()
                        when {
                            !acceptedTerms -> validationMessage = "Debes aceptar los términos para continuar."
                            isRegisterTab && cleanUsername.length < 3 -> validationMessage = "El nombre debe tener al menos 3 caracteres."
                            emailOrPhone.trim().length < 3 -> validationMessage = "Escribe tu correo o teléfono."
                            password.length < 6 -> validationMessage = "La contraseña debe tener al menos 6 caracteres."
                            else -> {
                                validationMessage = null
                                onAuthSuccess(
                                    cleanUsername.ifBlank { emailOrPhone.trim().ifBlank { "KING_PLAYER" } },
                                    password,
                                    isRegisterTab,
                                    when (selectedAvatarIndex) {
                                        1 -> "royal_guard"
                                        2 -> "arcane_queen"
                                        else -> "king_warrior"
                                    }
                                )
                            }
                        }
                    },
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
