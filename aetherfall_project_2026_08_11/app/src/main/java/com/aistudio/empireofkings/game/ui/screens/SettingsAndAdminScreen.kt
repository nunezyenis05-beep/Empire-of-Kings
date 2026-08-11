package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.data.AppSettings
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.data.remote.OnlineSessionStatus
import com.aistudio.empireofkings.game.data.remote.ServerConnectionStatus
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.BottomNavBar
import com.aistudio.empireofkings.game.ui.components.Mystic3DBackground
import com.aistudio.empireofkings.game.ui.components.ReferenceBadge
import com.aistudio.empireofkings.game.ui.components.ReferencePanel
import com.aistudio.empireofkings.game.ui.components.ReferenceTitle
import com.aistudio.empireofkings.game.ui.components.TopBar
import com.aistudio.empireofkings.game.ui.theme.*

@Composable
fun SettingsAndAdminScreen(
    userAccount: UserAccount?,
    appSettings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onAddAdminCurrencies: (Long, Long) -> Unit,
    onSignOut: () -> Unit,
    serverStatus: ServerConnectionStatus,
    onCheckServer: () -> Unit,
    onlineSessionStatus: OnlineSessionStatus,
    matchPlayers: List<String>
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var adminGoldInput by remember { mutableStateOf("100000") }
    var adminDiamondsInput by remember { mutableStateOf("1000") }
    var adminMsg by remember { mutableStateOf<String?>(null) }

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = onOpenPaymentModal
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    SettingsTab("⚙ AJUSTES", selectedTab == 0, Modifier.weight(1f)) { selectedTab = 0 }
                    SettingsTab("♜ ADMINISTRACIÓN", selectedTab == 1, Modifier.weight(1f)) { selectedTab = 1 }
                }
                Spacer(modifier = Modifier.height(8.dp))

                if (selectedTab == 0) {
                    SettingsContent(
                        settings = appSettings,
                        onSettingsChanged = onSettingsChanged,
                        onSignOut = onSignOut
                    )
                } else {
                    AdminContent(
                        adminGoldInput = adminGoldInput,
                        onGoldChanged = { adminGoldInput = it },
                        adminDiamondsInput = adminDiamondsInput,
                        onDiamondsChanged = { adminDiamondsInput = it },
                        adminMsg = adminMsg,
                        onAddCurrencies = { g, d -> onAddAdminCurrencies(g, d); adminMsg = "Modo local actualizado: +$g Oro / +$d Diamantes." },
                        serverStatus = serverStatus,
                        onCheckServer = onCheckServer,
                        onlineSessionStatus = onlineSessionStatus,
                        matchPlayers = matchPlayers
                    )
                }
            }
            BottomNavBar(currentRoute = ScreenRoute.SettingsAdmin, onNavigate = onNavigate)
        }
    }
}

@Composable
private fun SettingsTab(title: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, if (selected) GoldPrimary else GoldBorder.copy(alpha = 0.55f)),
        colors = ButtonDefaults.buttonColors(containerColor = if (selected) GoldPrimary else Color(0xE9101C35)),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(title, color = if (selected) Color.Black else GoldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SettingsContent(
    settings: AppSettings,
    onSettingsChanged: (AppSettings) -> Unit,
    onSignOut: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReferenceTitle("AJUSTES DE LA APLICACIÓN", "Controles guardados en tu perfil local.")

        ReferencePanel(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                SectionLabel("SONIDO Y RESPUESTA")
                SettingSwitch("Música del reino", "Ambiente y música de las zonas", settings.musicEnabled) { onSettingsChanged(settings.copy(musicEnabled = it)) }
                SettingSwitch("Efectos de sonido", "Botones, combate y recompensas", settings.effectsEnabled) { onSettingsChanged(settings.copy(effectsEnabled = it)) }
                SettingSwitch("Sonido general", "Interruptor maestro de audio", settings.soundEnabled) { onSettingsChanged(settings.copy(soundEnabled = it)) }
                SettingSwitch("Vibración táctil", "Respuesta al equipar, comprar o reclamar", settings.hapticsEnabled) { onSettingsChanged(settings.copy(hapticsEnabled = it)) }
            }
        }

        ReferencePanel(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                SectionLabel("NOTIFICACIONES Y PANTALLA")
                SettingSwitch("Notificaciones", "Misiones, eventos y recompensas", settings.notificationsEnabled) { onSettingsChanged(settings.copy(notificationsEnabled = it)) }
                Text("CALIDAD GRÁFICA", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                ChoiceRow(listOf("BAJA", "MEDIA", "ALTA", "ULTRA"), settings.graphicsQuality) { onSettingsChanged(settings.copy(graphicsQuality = it)) }
                Text("MODO DE PANTALLA", color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
                ChoiceRow(listOf("ADAPTABLE", "VERTICAL", "HORIZONTAL"), settings.screenMode) { onSettingsChanged(settings.copy(screenMode = it)) }
            }
        }

        ReferencePanel(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                SectionLabel("CUENTA Y DATOS")
                Text("Persistencia Room: activa", color = CyanMagic, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("Las preferencias se conservan al cerrar y abrir la aplicación.", color = TextMuted, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, ImperialCrimson),
                    shape = RoundedCornerShape(5.dp)
                ) { Text("CERRAR SESIÓN LOCAL", color = ImperialCrimson, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            }
        }
    }
}

@Composable
private fun SettingSwitch(label: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(description, color = TextMuted, fontSize = 9.sp)
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = GoldPrimary, uncheckedThumbColor = TextMuted, uncheckedTrackColor = Color(0xFF0B1428))
        )
    }
}

@Composable
private fun ChoiceRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(5.dp)) {
        options.forEach { option ->
            val active = option == selected
            Text(
                text = option,
                modifier = Modifier
                    .weight(1f)
                    .border(1.dp, if (active) GoldPrimary else GoldBorder.copy(alpha = 0.45f), RoundedCornerShape(4.dp))
                    .padding(vertical = 7.dp)
                    .clickableWithoutRipple { onSelect(option) },
                color = if (active) GoldGlow else TextMuted,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
private fun AdminContent(
    adminGoldInput: String,
    onGoldChanged: (String) -> Unit,
    adminDiamondsInput: String,
    onDiamondsChanged: (String) -> Unit,
    adminMsg: String?,
    onAddCurrencies: (Long, Long) -> Unit,
    serverStatus: ServerConnectionStatus,
    onCheckServer: () -> Unit,
    onlineSessionStatus: OnlineSessionStatus,
    matchPlayers: List<String>
) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        ReferenceTitle("PANEL ADMINISTRATIVO", "Herramientas locales separadas de la experiencia del jugador.")
        ReferencePanel(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("PANEL FINANCIERO LOCAL", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("No procesa pagos reales ni modifica el servidor.", color = TextMuted, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = adminGoldInput, onValueChange = onGoldChanged, label = { Text("Oro (DM)") }, modifier = Modifier.weight(1f), singleLine = true)
                    OutlinedTextField(value = adminDiamondsInput, onValueChange = onDiamondsChanged, label = { Text("Diamantes (COR)") }, modifier = Modifier.weight(1f), singleLine = true)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        val g = adminGoldInput.toLongOrNull(); val d = adminDiamondsInput.toLongOrNull()
                        if (g != null && d != null && g >= 0 && d >= 0 && (g > 0 || d > 0)) onAddCurrencies(g, d)
                    },
                    modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) { Text("ACTUALIZAR RECURSOS LOCALES", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
                adminMsg?.let { Text(it, color = CyanMagic, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp)) }
            }
        }
        ReferencePanel(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                Text("ESTADO DEL JUEGO", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Text("• Persistencia Room: activa", color = CyanMagic, fontSize = 11.sp)
                Text("• Pagos reales: desactivados", color = TextLight, fontSize = 11.sp)
                val serverLabel = when (serverStatus) { ServerConnectionStatus.CHECKING -> "comprobando..."; ServerConnectionStatus.ONLINE -> "online"; ServerConnectionStatus.OFFLINE -> "sin conexión (modo local disponible)" }
                val serverColor = when (serverStatus) { ServerConnectionStatus.ONLINE -> Color(0xFF65E572); ServerConnectionStatus.CHECKING -> GoldGlow; ServerConnectionStatus.OFFLINE -> TextMuted }
                Text("• Servidor remoto: $serverLabel", color = serverColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("• Socket.IO: ${socketLabel(onlineSessionStatus, matchPlayers.size)}", color = TextLight, fontSize = 11.sp)
                OutlinedButton(onClick = onCheckServer, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, GoldBorder)) { Text("COMPROBAR SERVIDOR", color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 11.sp) }
            }
        }
    }
}

private fun socketLabel(status: OnlineSessionStatus, count: Int): String = when (status) {
    OnlineSessionStatus.DISCONNECTED -> "desconectado"
    OnlineSessionStatus.CONNECTING -> "conectando..."
    OnlineSessionStatus.CONNECTED -> "conectado"
    OnlineSessionStatus.MATCHMAKING -> "buscando partida..."
    OnlineSessionStatus.MATCH_FOUND -> "partida encontrada ($count jugadores)"
}

@Composable
private fun SectionLabel(text: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text, color = GoldGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        ReferenceBadge("GUARDADO")
    }
}

private fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier = clickable(onClick = onClick)
