package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserAccount
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*

import com.example.ui.components.Mystic3DBackground

@Composable
fun SettingsAndAdminScreen(
    userAccount: UserAccount?,
    onNavigate: (ScreenRoute) -> Unit,
    onAddAdminCurrencies: (Long, Long) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Config, 1: Panel Admin & Financiero
    var adminGoldInput by remember { mutableStateOf("100000") }
    var adminDiamondsInput by remember { mutableStateOf("1000") }
    var adminMsg by remember { mutableStateOf<String?>(null) }

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = { }
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { selectedTab = 0 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 0) GoldPrimary else Color(0xFF101628))
                    ) {
                        Text("⚙️ CONFIGURACIÓN Y CONTROLES", color = if (selectedTab == 0) Color.Black else GoldGlow, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { selectedTab = 1 },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selectedTab == 1) GoldPrimary else Color(0xFF101628))
                    ) {
                        Text("🛡️ PANEL ADMINISTRATIVO Y FINANCIERO", color = if (selectedTab == 1) Color.Black else GoldGlow, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (selectedTab == 0) {
                    Card(
                        modifier = Modifier.fillMaxSize().border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("AJUSTES DE JUEGO Y CONTROLES", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                            Spacer(modifier = Modifier.height(8.dp))

                            Text("Modo de Pantalla: Horizontal 16:9 Adaptable (Móvil / Tablet / PC)", color = TextLight, fontSize = 12.sp)
                            Text("Controles Móvil: Joystick Táctil + Botones Rúnicos", color = TextLight, fontSize = 12.sp)
                            Text("Controles PC: Teclas WASD, Apuntar Ratón, Clic Disparo, R Recargar, Q Habilidad", color = TextLight, fontSize = 12.sp)
                            Text("Calidad Gráfica: Ultra 60FPS / 120FPS con Luces y Humo Místico", color = CyanMagic, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Card(
                        modifier = Modifier.fillMaxSize().border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("PANEL FINANCIERO Y MODERACIÓN EN VIVO", color = GoldPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                            Text("Herramientas de Servidor y Finanzas de EMPIRE OF KINGS", color = TextMuted, fontSize = 11.sp)

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = adminGoldInput,
                                    onValueChange = { adminGoldInput = it },
                                    label = { Text("Añadir Oro (DM)") },
                                    modifier = Modifier.weight(1f)
                                )

                                OutlinedTextField(
                                    value = adminDiamondsInput,
                                    onValueChange = { adminDiamondsInput = it },
                                    label = { Text("Añadir Diamantes (COR)") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Button(
                                onClick = {
                                    val g = adminGoldInput.toLongOrNull() ?: 0L
                                    val d = adminDiamondsInput.toLongOrNull() ?: 0L
                                    onAddAdminCurrencies(g, d)
                                    adminMsg = "¡Inyección administrativa completada! +$g Oro / +$d Diamantes."
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("INYECTAR FONDOS AL JUGADOR", color = Color.Black, fontWeight = FontWeight.Bold)
                            }

                            adminMsg?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(it, color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Spacer(modifier = Modifier.height(14.dp))
                            Divider(color = GoldBorder.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(10.dp))

                            Text("REGISTROS DE PAGOS Y MODERACIÓN DE USUARIOS:", color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("• Pago #1042: $9.99 USD vía CubaPay — Completado", color = TextLight, fontSize = 11.sp)
                            Text("• Pago #1043: $24.99 USD vía WhatsApp CUP — Verificado", color = TextLight, fontSize = 11.sp)
                            Text("• Estado de Servidor: 100% Operativo | Anti-Cheat Activo", color = Color.Green, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            BottomNavBar(currentRoute = ScreenRoute.SettingsAdmin, onNavigate = onNavigate)
        }
    }
}
