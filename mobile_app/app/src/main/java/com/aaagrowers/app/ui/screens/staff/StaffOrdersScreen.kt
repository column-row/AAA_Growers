package com.aaagrowers.app.ui.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.StatusBadge
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.StaffViewModel

@Composable
fun StaffOrdersScreen(
    staffViewModel: StaffViewModel
) {
    val orders by staffViewModel.orders.collectAsState()
    val isLoading by staffViewModel.isLoading.collectAsState()
    var selectedStatus by remember { mutableStateOf<String?>(null) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedStatus) {
        staffViewModel.loadOrders(status = selectedStatus)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Store Orders & Fulfillment", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextPrimary)
                Text("Order dispatch, warehouse packing & status transitions", fontSize = 12.sp, color = TextSecondary)
            }
            IconButton(onClick = { staffViewModel.loadOrders(selectedStatus) }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status Filter Chips
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            FilterChip(
                selected = selectedStatus == null,
                onClick = { selectedStatus = null },
                label = { Text("All", fontSize = 11.sp) }
            )
            listOf("PENDING", "PAID", "PROCESSING", "DISPATCHED", "DELIVERED").forEach { st ->
                FilterChip(
                    selected = selectedStatus == st,
                    onClick = { selectedStatus = st },
                    label = { Text(st, fontSize = 10.sp) }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (resultMessage != null) {
            Surface(
                color = EmeraldContainer,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = resultMessage!!,
                    color = EmeraldDark,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders found.", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(orders) { ord ->
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = ord.orderNumber,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    StatusBadge(status = ord.paymentStatus)
                                    StatusBadge(status = ord.status)
                                }
                            }

                            Text(
                                text = "Customer: ${ord.customerName} • ${ord.deliveryCity}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                modifier = Modifier.padding(top = 4.dp)
                            )

                            Text(
                                text = "Amount: KES ${String.format("%.2f", ord.netAmount)} (${ord.items?.size ?: 0} items)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldPrimary,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Advance Stage:", fontSize = 11.sp, color = TextMuted)

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    if (ord.status == "PENDING" || ord.status == "PAID") {
                                        Button(
                                            onClick = {
                                                staffViewModel.updateOrderStatus(ord.id, "PROCESSING") { success, msg ->
                                                    resultMessage = msg
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Pack / Process", color = EmeraldDark, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (ord.status == "PROCESSING") {
                                        Button(
                                            onClick = {
                                                staffViewModel.updateOrderStatus(ord.id, "DISPATCHED") { success, msg ->
                                                    resultMessage = msg
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Mark Dispatched", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }

                                    if (ord.status == "DISPATCHED") {
                                        Button(
                                            onClick = {
                                                staffViewModel.updateOrderStatus(ord.id, "DELIVERED") { success, msg ->
                                                    resultMessage = msg
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldDark),
                                            shape = RoundedCornerShape(8.dp),
                                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                        ) {
                                            Text("Mark Delivered", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
