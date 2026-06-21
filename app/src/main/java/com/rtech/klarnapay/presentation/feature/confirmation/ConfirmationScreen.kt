package com.rtech.klarnapay.presentation.feature.confirmation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rtech.klarnapay.domain.model.Order
import com.rtech.klarnapay.presentation.feature.products.KlarnaPink

/**
 * Simple confirmation screen shown after a successful Klarna order.
 * Stateless — receives the Order domain model directly from the nav back-stack.
 */
@Composable
fun ConfirmationScreen(
    order: Order,
    onBackToShop: () -> Unit
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ── Success icon ──────────────────────────────────────────────
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = Color(0xFF4CAF50),
                modifier = Modifier.size(96.dp)
            )

            Spacer(Modifier.height(24.dp))

            Text(
                text = "Payment Successful!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Your order has been placed with Klarna.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(32.dp))

            // ── Order details card ────────────────────────────────────────
            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        "Order Details", style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.height(12.dp))

                    DetailRow(label = "Order ID", value = order.orderId)
                    Spacer(Modifier.height(8.dp))
                    DetailRow(
                        label = "Status",
                        value = order.status.replaceFirstChar { it.uppercase() })
                }
            }

            Spacer(Modifier.height(40.dp))

            Button(
                onClick = onBackToShop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(containerColor = KlarnaPink),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Back to Shop", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value, style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
    }
}
