package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.OrderViewModel

@Composable
fun OrderConfirmationScreen(
    orderId: Int,
    orderViewModel: OrderViewModel,
    onTrackOrder: (Int) -> Unit,
    onContinueShopping: () -> Unit
) {
    val order by orderViewModel.currentOrder.collectAsState()

    LaunchedEffect(orderId) {
        orderViewModel.loadOrderDetail(orderId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Success Circle
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(EmeraldContainer),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(EmeraldPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(36.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Order Placed & Paid!",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = TextPrimary
        )

        Text(
            text = "Thank you! Your agricultural produce order has been authorized and forwarded to warehouse dispatch.",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Order Number", fontSize = 12.sp, color = TextMuted)
                    Text(order?.orderNumber ?: "#AAA-$orderId", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total Paid", fontSize = 12.sp, color = TextMuted)
                    Text("KES ${String.format("%.2f", order?.netAmount ?: 0.0)}", fontWeight = FontWeight.Black, fontSize = 14.sp, color = EmeraldPrimary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Payment Status", fontSize = 12.sp, color = TextMuted)
                    Text("PAID (Verified)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = EmeraldPrimary)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Delivery Destination", fontSize = 12.sp, color = TextMuted)
                    Text(order?.deliveryCity ?: "Nairobi", fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = TextPrimary)
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        AAAButton(
            text = "Track Live Dispatch",
            onClick = { onTrackOrder(orderId) },
            icon = Icons.Default.LocalShipping
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onContinueShopping,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.ShoppingBag, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Continue Shopping", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
        }
    }
}
