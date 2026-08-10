package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.KingdomBattleResult
import com.example.data.UserAccount
import com.example.ui.ScreenRoute
import com.example.ui.components.BottomNavBar
import com.example.ui.components.TopBar
import com.example.ui.theme.*
import com.example.ui.components.Mystic3DBackground
import kotlinx.coroutines.delay

@Composable
fun KingdomScreen(
    userAccount: UserAccount?,
    onNavigate: (ScreenRoute) -> Unit,
    onCollectResources: () -> Unit,
    onUpgradeBuilding: (String, (Boolean, String) -> Unit) -> Unit,
    onTrainTroop: (String, (Boolean, String) -> Unit) -> Unit,
    onSimulateBattle: (Int, Int, Int, String, (KingdomBattleResult) -> Unit) -> Unit
) {
    var feedbackMessage by remember { mutableStateOf<String?>(null) }
    var feedbackColor by remember { mutableStateOf(GoldPrimary) }

    // Troop Deployment Selection State for Battle
    var deploySoldiers by remember { mutableIntStateOf(0) }
    var deployArchers by remember { mutableIntStateOf(0) }
    var deployMages by remember { mutableIntStateOf(0) }
    var selectedDifficulty by remember { mutableStateOf("EASY") }

    // Battle Result Logs State
    var battleInProgress by remember { mutableStateOf(false) }
    var battleResultLog by remember { mutableStateOf<List<String>?>(null) }
    var battleSuccessResult by remember { mutableStateOf<Boolean?>(null) }

    // Auto dismiss feedback helper
    LaunchedEffect(feedbackMessage) {
        if (feedbackMessage != null) {
            delay(4000)
            feedbackMessage = null
        }
    }

    Mystic3DBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            // TOP BAR
            TopBar(
                userAccount = userAccount,
                onOpenProfile = { onNavigate(ScreenRoute.Profile) },
                onOpenPayment = {}
            )

            if (userAccount == null) {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GoldPrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // TITLE & SUBTITLE
                    item {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "GESTIÓN DEL REINO IMPERIAL",
                            color = GoldPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Serif
                        )
                        Text(
                            text = "Expande tus minas, entrena a tus tropas y conquista las tierras de Aetherfall.",
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    // MAIN KINGDOM LEVEL & PROGRESS BAR
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("👑 Nivel de Reino: ", color = TextLight, fontSize = 13.sp)
                                        Text("${userAccount.kingdomLevel}", color = GoldGlow, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    val reqXp = userAccount.kingdomLevel * 100
                                    Text("${userAccount.kingdomExp} / $reqXp XP", color = CyanMagic, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                val progress = (userAccount.kingdomExp.toFloat() / (userAccount.kingdomLevel * 100).toFloat()).coerceIn(0f, 1f)
                                LinearProgressIndicator(
                                    progress = { progress },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = GoldPrimary,
                                    trackColor = Color(0xFF161028)
                                )
                            }
                        }
                    }

                    // TACTILE FEEDBACK ALERTS
                    item {
                        feedbackMessage?.let { msg ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, feedbackColor, RoundedCornerShape(10.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xEE1A102A))
                            ) {
                                Text(
                                    text = msg,
                                    color = feedbackColor,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(12.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    // RECOLLECT EXTRA BUTTON & EXTRA RESOURCE DISPLAYS
                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, CyanMagic.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1528))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🪵 Madera en Reserva: ", color = TextMuted, fontSize = 12.sp)
                                        Text("${userAccount.woodCount} u", color = Color(0xFFD7A15C), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                    Text("Producción: +${userAccount.goldMineLevel * 300} Oro | +${userAccount.castleLevel * 150} Madera por ciclo.", color = TextMuted, fontSize = 10.sp)
                                }
                                Button(
                                    onClick = {
                                        onCollectResources()
                                        feedbackMessage = "🪵 ¡Has recolectado 💰 ${userAccount.goldMineLevel * 300} Oro y 🪵 ${userAccount.castleLevel * 150} Madera de tus posesiones!"
                                        feedbackColor = CyanMagic
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                                ) {
                                    Icon(Icons.Default.VolunteerActivism, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("RECOLECTAR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    // INTERACTIVE BUILDINGS LIST (UPGRADES)
                    item {
                        Text("🏛️ EDIFICIOS DEL IMPERIO", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // CASTLE
                            BuildingCard(
                                name = "Castillo Real (Ayuntamiento)",
                                level = userAccount.castleLevel,
                                description = "El corazón de tu reino. Determina el estatus de tu civilización y producción mística.",
                                costGold = userAccount.castleLevel * 10000L,
                                costWood = userAccount.castleLevel * 5000L,
                                onUpgrade = {
                                    onUpgradeBuilding("CASTILLO") { success, msg ->
                                        feedbackMessage = msg
                                        feedbackColor = if (success) GoldPrimary else ImperialCrimson
                                    }
                                }
                            )

                            // GOLD MINE
                            BuildingCard(
                                name = "Mina de Oro Imperial",
                                level = userAccount.goldMineLevel,
                                description = "Pozos de extracción rúnica de oro puro. Produce oro de forma pasiva.",
                                costGold = userAccount.goldMineLevel * 4000L,
                                costWood = userAccount.goldMineLevel * 2000L,
                                onUpgrade = {
                                    onUpgradeBuilding("MINA") { success, msg ->
                                        feedbackMessage = msg
                                        feedbackColor = if (success) GoldPrimary else ImperialCrimson
                                    }
                                }
                            )

                            // BARRACKS
                            BuildingCard(
                                name = "Cuartel Militar de León",
                                level = userAccount.barracksLevel,
                                description = "Permite desbloquear y reclutar mejores tropas (Arqueros a Nv.2, Magos a Nv.3).",
                                costGold = userAccount.barracksLevel * 6000L,
                                costWood = userAccount.barracksLevel * 3000L,
                                onUpgrade = {
                                    onUpgradeBuilding("CUARTEL") { success, msg ->
                                        feedbackMessage = msg
                                        feedbackColor = if (success) GoldPrimary else ImperialCrimson
                                    }
                                }
                            )
                        }
                    }

                    // MILITARY RESERVES & RECRUITMENT
                    item {
                        Text("⚔️ ACADEMIA MILITAR & RESERVAS", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Reserva Militar Actual:", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceAround
                                ) {
                                    ReservesIndicator("🗡️ Infantería", userAccount.soldiersCount)
                                    ReservesIndicator("🏹 Arqueros", userAccount.archersCount)
                                    ReservesIndicator("🔮 Magos", userAccount.magesCount)
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFF1E1430))

                                Text("Entrenar Unidades:", color = TextLight, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(10.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    // RECRUIT SOLDIER
                                    RecruitRow(
                                        troopName = "Soldado (Infantería)",
                                        cost = "💰 1000 Oro | 🪵 500 Madera",
                                        onRecruit = {
                                            onTrainTroop("SOLDIER") { success, msg ->
                                                feedbackMessage = msg
                                                feedbackColor = if (success) GoldPrimary else ImperialCrimson
                                            }
                                        }
                                    )

                                    // RECRUIT ARCHER
                                    RecruitRow(
                                        troopName = "Arquero (Rango)",
                                        cost = "💰 2000 Oro | 🪵 1000 Madera (Cuartel Nv.2)",
                                        onRecruit = {
                                            onTrainTroop("ARCHER") { success, msg ->
                                                feedbackMessage = msg
                                                feedbackColor = if (success) GoldPrimary else ImperialCrimson
                                            }
                                        }
                                    )

                                    // RECRUIT MAGE
                                    RecruitRow(
                                        troopName = "Mago Místico (Magia)",
                                        cost = "💰 5000 Oro | 🪵 2000 Madera | 🔮 10 Esencia (Cuartel Nv.3)",
                                        onRecruit = {
                                            onTrainTroop("MAGE") { success, msg ->
                                                feedbackMessage = msg
                                                feedbackColor = if (success) GoldPrimary else ImperialCrimson
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // STRATEGIC THREATS & COMBAT CAMPAIGN
                    item {
                        Text("🛡️ CAMPAÑA DE CONQUISTA Y DUELOS", color = GoldPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, ImperialCrimson.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF17091B))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Amenazas en las Tierras Salvajes", color = TextLight, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                // Select Difficulty
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    listOf("EASY", "MEDIUM", "HARD").forEach { diff ->
                                        val isSelected = selectedDifficulty == diff
                                        val label = when (diff) {
                                            "EASY" -> "Campamento Orco (Fácil)"
                                            "MEDIUM" -> "Dragón Rúnico (Medio)"
                                            "HARD" -> "Ciudadela Rival (Difícil)"
                                            else -> diff
                                        }
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) ImperialCrimson else Color(0xFF0C0F1B))
                                                .border(1.dp, if (isSelected) GoldPrimary else Color.Transparent, RoundedCornerShape(8.dp))
                                                .clickable { selectedDifficulty = diff }
                                                .padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(label, color = if (isSelected) Color.White else TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Select Deploy Troops
                                Text("Seleccionar Fuerza de Despliegue:", color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    DeployStepper("Soldados de Infantería", deploySoldiers, userAccount.soldiersCount) { deploySoldiers = it }
                                    DeployStepper("Arqueros Reales", deployArchers, userAccount.archersCount) { deployArchers = it }
                                    DeployStepper("Magos Místicos", deployMages, userAccount.magesCount) { deployMages = it }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Launch Attack
                                Button(
                                    onClick = {
                                        battleInProgress = true
                                        battleResultLog = null
                                        battleSuccessResult = null
                                        onSimulateBattle(deploySoldiers, deployArchers, deployMages, selectedDifficulty) { result ->
                                            battleInProgress = false
                                            battleResultLog = result.log
                                            battleSuccessResult = result.success
                                            if (result.success) {
                                                deploySoldiers = 0
                                                deployArchers = 0
                                                deployMages = 0
                                            } else {
                                                // Adjust selectors down to surviving troop count
                                                deploySoldiers = deploySoldiers.coerceAtMost(result.survivorsSoldiers)
                                                deployArchers = deployArchers.coerceAtMost(result.survivorsArchers)
                                                deployMages = deployMages.coerceAtMost(result.survivorsMages)
                                            }
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = ImperialCrimson),
                                    modifier = Modifier.fillMaxWidth(),
                                    enabled = !battleInProgress && (deploySoldiers + deployArchers + deployMages > 0)
                                ) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("DESPLEGAR EJÉRCITO E INICIAR", color = Color.White, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }

                    // BATTLE LOGS CONTAINER
                    item {
                        if (battleInProgress) {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(1.dp, GoldPrimary, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1F102B))
                            ) {
                                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(color = GoldPrimary)
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text("¡Combate en curso! Las tropas imperiales atacan las líneas enemigas...", color = GoldGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        battleResultLog?.let { logLines ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(2.dp, if (battleSuccessResult == true) GoldGlow else ImperialCrimson, RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1223))
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = if (battleSuccessResult == true) "🛡️ PARTE DE VICTORIA MILITAR" else "💀 REPORTE DE DERROTA",
                                        color = if (battleSuccessResult == true) GoldGlow else ImperialCrimson,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        fontFamily = FontFamily.Serif
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    logLines.forEach { line ->
                                        Text(line, color = TextLight, fontSize = 11.sp, modifier = Modifier.padding(vertical = 2.dp))
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            // BOTTOM NAVIGATION
            BottomNavBar(currentRoute = ScreenRoute.Kingdom, onNavigate = onNavigate)
        }
    }
}

@Composable
private fun BuildingCard(
    name: String,
    level: Int,
    description: String,
    costGold: Long,
    costWood: Long,
    onUpgrade: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, GoldBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = ObsidianSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(name, color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Box(
                    modifier = Modifier
                        .background(Color(0xFF231A3D), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text("Nv. $level", color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(description, color = TextLight, fontSize = 10.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Coste: 💰 $costGold | 🪵 $costWood", color = TextMuted, fontSize = 10.sp)
                Button(
                    onClick = onUpgrade,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("MEJORAR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun ReservesIndicator(label: String, count: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextMuted, fontSize = 11.sp)
        Text("$count", color = GoldGlow, fontSize = 16.sp, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun RecruitRow(
    troopName: String,
    cost: String,
    onRecruit: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0B0F20), RoundedCornerShape(8.dp))
            .border(0.5.dp, GoldBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(troopName, color = TextLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(cost, color = TextMuted, fontSize = 9.sp)
        }
        Button(
            onClick = onRecruit,
            colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
            modifier = Modifier.height(26.dp)
        ) {
            Text("ENTRENAR", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 9.sp)
        }
    }
}

@Composable
private fun DeployStepper(
    label: String,
    current: Int,
    max: Int,
    onValueChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("$label (Max: $max)", color = TextLight, fontSize = 11.sp)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = { if (current > 0) onValueChange(current - 1) },
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF1E1430), CircleShape)
            ) {
                Text("-", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Text("$current", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)

            IconButton(
                onClick = { if (current < max) onValueChange(current + 1) },
                modifier = Modifier
                    .size(24.dp)
                    .background(Color(0xFF1E1430), CircleShape)
            ) {
                Text("+", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}
