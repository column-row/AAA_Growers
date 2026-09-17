package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.OrderProgressStepper
import com.aaagrowers.app.ui.components.StatusBadge
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.OrderViewModel

@Composable
fun OrderTrackingScreen(
    orderId: Int,
    orderViewModel: OrderViewModel,
    onBack: () -> Unit
) {
    val order by orderViewModel.currentOrder.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()

    LaunchedEffect(orderId) {
        orderViewModel.loadOrderDetail(orderId)
    }

    if (isLoading || order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = EmeraldPrimary)
        }
        return
    }

    val ord = order!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Order #${ord.orderNumber}",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Live Status Stepper Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Fulfillment Status",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary
                        )
                        StatusBadge(status = ord.status)
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    OrderProgressStepper(currentStatus = ord.status)
                }
            }

            // Dispatch & Driver Details
            if (ord.dispatch != null) {
                Card(
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = EmeraldDark)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Cold-Chain Dispatch Details",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = EmeraldDark
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Courier: ${ord.dispatch?.driverName ?: "Fleet Assigned"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = TextPrimary
                        )
                        Text(
                            text = "Vehicle: ${ord.dispatch?.vehicleReg ?: "Cold-Truck 01"}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        if (!ord.dispatch?.trackingNotes.isNullOrBlank()) {
                            Text(
                                text = "Notes: ${ord.dispatch?.trackingNotes}",
                                fontSize = 12.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Items Purchased Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Produce Items",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )

                    ord.items?.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.productName, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = TextPrimary)
                                Text("${item.quantity} x KES ${String.format("%.2f", item.unitPrice)}", fontSize = 11.sp, color = TextMuted)
                            }
                            Text(
                                "KES ${String.format("%.2f", item.subtotal)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = TextPrimary
                            )
                        }
                    }

                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount Paid", fontWeight = FontWeight.Black, fontSize = 14.sp, color = TextPrimary)
                        Text(
                            "KES ${String.format("%.2f", ord.netAmount)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = EmeraldPrimary
                        )
                    }
                }
            }

            // Delivery Destination Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "Delivery Address",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(ord.deliveryAddress ?: "N/A", fontSize = 13.sp, color = TextSecondary)
                    Text("${ord.deliveryCity ?: ""} • ${ord.deliveryPhone ?: ""}", fontSize = 12.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                }
            }
        }
    }
}
