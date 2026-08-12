package com.aistudio.empireofkings.game.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.aistudio.empireofkings.game.data.MiniGameProgress
import com.aistudio.empireofkings.game.data.UserAccount
import com.aistudio.empireofkings.game.ui.ScreenRoute
import com.aistudio.empireofkings.game.ui.components.BottomNavBar
import com.aistudio.empireofkings.game.ui.components.Mystic3DBackground
import com.aistudio.empireofkings.game.ui.components.ReferencePanel
import com.aistudio.empireofkings.game.ui.components.ReferenceTitle
import com.aistudio.empireofkings.game.ui.components.TopBar
import com.aistudio.empireofkings.game.ui.theme.*

private data class ArcadeGame(val id: String, val title: String, val description: String, val reward: String, val gold: Long, val diamonds: Long, val control: String)
private val arcadeGames = listOf(
    ArcadeGame("imperial_chess", "Ajedrez Imperial", "Resuelve la apertura del Trono.", "+5,000 Oro", 5_000, 0, "JUGAR APERTURA"),
    ArcadeGame("rune_cards", "Cartas Rúnicas", "Elige la runa que vence al vacío.", "+20 Diamantes", 0, 20, "ELEGIR RUNA"),
    ArcadeGame("lion_domino", "Dominó de los Leones", "Coloca una ficha y cierra la mesa.", "+3,500 Oro", 3_500, 0, "COLOCAR FICHA")
)

@Composable
fun GamesScreen(
    userAccount: UserAccount?,
    progress: MiniGameProgress,
    onNavigate: (ScreenRoute) -> Unit,
    onOpenPaymentModal: () -> Unit,
    onSelectGame: (String) -> Unit,
    onSettleGame: (String, Boolean, Long, Long) -> Unit
) {
    var activeGameId by remember { mutableStateOf<String?>(null) }
    var moveCount by remember { mutableIntStateOf(0) }
    var feedback by remember { mutableStateOf<String?>(null) }
    val selectedGame = arcadeGames.firstOrNull { it.id == (activeGameId ?: progress.selectedGameId) } ?: arcadeGames.first()

    fun begin(game: ArcadeGame) {
        activeGameId = game.id
        moveCount = 0
        feedback = "Partida iniciada: ${game.title}. Completa dos controles para liquidar la recompensa."
        onSelectGame(game.id)
    }
    fun act(game: ArcadeGame) {
        if (activeGameId != game.id) return
        moveCount += 1
        if (moveCount >= 2) {
            onSettleGame(game.id, true, game.gold, game.diamonds)
            feedback = "Victoria confirmada en ${game.title}: ${game.reward}. El progreso queda guardado."
            activeGameId = null
            moveCount = 0
        } else {
            feedback = "Control aceptado. Falta una jugada para resolver la partida."
        }
    }

    Mystic3DBackground {
        Column(Modifier.fillMaxSize()) {
            TopBar(userAccount = userAccount, onOpenProfile = { onNavigate(ScreenRoute.Profile) }, onOpenPayment = onOpenPaymentModal)
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth().padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(9.dp),
                contentPadding = PaddingValues(vertical = 10.dp)
            ) {
                item {
                    ReferenceTitle("SALA DE JUEGOS IMPERIAL", "Partidas cortas · recompensa liquidada una vez por resolución")
                }
                item {
                    ReferencePanel(Modifier.padding(top = 1.dp)) {
                        Column(Modifier.padding(10.dp)) {
                            Text("PROGRESO GUARDADO", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Text("${progress.gamesWon} victorias · ${progress.gamesPlayed} partidas", color = CyanMagic, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(progress.lastResult, color = TextMuted, fontSize = 10.sp)
                            if (progress.lastRewardGold > 0 || progress.lastRewardDiamonds > 0) {
                                Text("Última recompensa: ${progress.lastRewardGold} oro · ${progress.lastRewardDiamonds} diamantes", color = GoldGlow, fontSize = 10.sp)
                            }
                        }
                    }
                }
                feedback?.let { message ->
                    item {
                        Card(Modifier.fillMaxWidth().border(1.dp, CyanMagic, RoundedCornerShape(8.dp)), colors = CardDefaults.cardColors(containerColor = Color(0xEE1E1430))) {
                            Text(message, Modifier.padding(10.dp), color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
                items(arcadeGames, key = { it.id }) { game ->
                    val isActive = activeGameId == game.id
                    ReferencePanel {
                        Column(Modifier.padding(10.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(Modifier.weight(1f)) {
                                    Text(game.title, color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(game.description, color = TextLight, fontSize = 10.sp)
                                }
                                Text(game.reward, color = CyanMagic, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                            }
                            Spacer(Modifier.height(7.dp))
                            if (isActive) {
                                Text("JUGADA ${moveCount + 1}/2 · control real de partida local", color = GoldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Button(
                                    onClick = { act(game) },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                                    contentPadding = PaddingValues(vertical = 7.dp)
                                ) { Text(game.control, color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black) }
                            } else {
                                OutlinedButton(
                                    onClick = { begin(game) },
                                    modifier = Modifier.fillMaxWidth(),
                                    border = BorderStroke(1.dp, GoldBorder),
                                    contentPadding = PaddingValues(vertical = 6.dp)
                                ) { Text("INICIAR PARTIDA", color = GoldGlow, fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                            }
                        }
                    }
                }
                item {
                    Text("Las recompensas son moneda de demostración local; no se realiza dinero real.", color = TextMuted, fontSize = 9.sp, modifier = Modifier.padding(bottom = 4.dp))
                }
            }
            BottomNavBar(currentRoute = ScreenRoute.Games, onNavigate = onNavigate)
        }
    }
}
