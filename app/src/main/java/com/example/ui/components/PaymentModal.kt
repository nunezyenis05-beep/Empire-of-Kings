package com.example.ui.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun PaymentModal(
    itemName: String,
    amountUSD: Double,
    onDismiss: () -> Unit,
    onConfirmPayment: (String) -> Unit
) {
    val context = LocalContext.current
    var selectedMethod by remember { mutableStateOf("CubaPay") }
    var cardNumber by remember { mutableStateOf("") }
    var statusText by remember { mutableStateOf<String?>(null) }

    val methods = listOf(
        "CubaPay" to "CubaPay / Transfermóvil",
        "Zelle" to "Zelle Direct",
        "PayPal" to "PayPal Express",
        "Card" to "Tarjeta Crédito / Débito",
        "WhatsApp" to "Pago en CUP por WhatsApp"
    )

    Surface(
        color = Color(0xCC000000),
        modifier = Modifier.fillMaxSize()
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Card(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .border(2.dp, GoldBorder, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = ObsidianSurface),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "RECARGA IMPERIAL DE DIAMANTES",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "$itemName — $$amountUSD USD",
                                color = TextLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    Divider(
                        color = GoldBorder.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = 12.dp)
                    )

                    Text(
                        text = "Seleccione Método de Pago Seguro:",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Payment Methods list
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        methods.forEach { (key, label) ->
                            val isSelected = selectedMethod == key
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) Color(0xFF231A3D) else Color(0xFF0F1526))
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) GoldPrimary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { selectedMethod = key }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) GoldGlow else TextLight,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )

                                RadioButton(
                                    selected = isSelected,
                                    onClick = { selectedMethod = key },
                                    colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (selectedMethod == "Card") {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = { cardNumber = it },
                            label = { Text("Número de Tarjeta (16 dígitos)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = GoldBorder
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    } else if (selectedMethod == "WhatsApp") {
                        Text(
                            text = "ℹ️ Se abrirá un chat directo con el Agente de Ventas Imperial en WhatsApp para realizar el pago en CUP vía Transfermóvil/EnZona.",
                            color = CyanMagic,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .background(Color(0x3300E5FF), RoundedCornerShape(6.dp))
                                .padding(8.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    statusText?.let {
                        Text(
                            text = it,
                            color = GoldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }

                    // Action Button
                    Button(
                        onClick = {
                            if (selectedMethod == "WhatsApp") {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=5350000000&text=Hola!%20Deseo%20comprar%20$itemName%20de%20EMPIRE%20OF%20KINGS%20en%20CUP"))
                                context.startActivity(intent)
                                onConfirmPayment("WhatsApp CUP")
                            } else {
                                statusText = "Procesando pago seguro..."
                                onConfirmPayment(selectedMethod)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Lock, contentDescription = "Secure", tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PROCESAR PAGO ($$amountUSD USD)",
                                color = Color.Black,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
