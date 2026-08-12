package com.aistudio.empireofkings.game.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aistudio.empireofkings.game.data.Weapon3DDefinition
import com.aistudio.empireofkings.game.data.Weapon3DCatalog
import com.aistudio.empireofkings.game.ui.theme.GoldGlow
import com.aistudio.empireofkings.game.ui.theme.GoldPrimary
import com.aistudio.empireofkings.game.ui.theme.TextLight
import com.aistudio.empireofkings.game.ui.theme.TextMuted

@Composable
fun WeaponGallerySection(
    weapon: Weapon3DDefinition = Weapon3DCatalog.pilotInfernalDragon,
    modifier: Modifier = Modifier
) {
    ReferencePanel(modifier = modifier.fillMaxWidth().height(238.dp)) {
        Column(modifier = Modifier.fillMaxWidth().padding(9.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("ARSENAL 3D", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    Text("MODELO PILOTO INTEGRADO", color = TextMuted, fontSize = 8.sp)
                }
                Text("${weapon.rarity.uppercase()}  •  NIVEL ${weapon.level}", color = Color(0xFFFF6A2A), fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))

            Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                Box(
                    modifier = Modifier
                        .weight(1.25f)
                        .fillMaxHeight()
                        .border(1.dp, Color(0xFF5A2917), RoundedCornerShape(6.dp))
                        .background(Color(0xFF050308), RoundedCornerShape(6.dp))
                ) {
                    Weapon3DPreview(
                        assetPath = weapon.modelAsset,
                        modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                        allowCameraGestures = true
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(0.9f).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
                    Text(weapon.name, color = GoldGlow, fontSize = 12.sp, fontWeight = FontWeight.Black, maxLines = 2)
                    Text(weapon.category, color = TextMuted, fontSize = 8.sp)
                    Text(weapon.description, color = TextLight, fontSize = 8.sp, lineHeight = 11.sp, maxLines = 4)
                    Text("DAÑO ${weapon.damage}  •  CADENCIA ${weapon.fireRate}", color = Color(0xFFFF9B38), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("ALCANCE ${weapon.range}  •  PRECISIÓN ${weapon.accuracy}", color = Color(0xFFFFC56B), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    Text("ARRASTRA PARA GIRAR  •  PINZA PARA ZOOM", color = TextMuted, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
