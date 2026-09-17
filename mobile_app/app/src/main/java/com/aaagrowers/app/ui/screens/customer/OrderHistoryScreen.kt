package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.StatusBadge
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.OrderViewModel

@Composable
fun OrderHistoryScreen(
    orderViewModel: OrderViewModel,
    onNavigateToOrderTracking: (Int) -> Unit,
    onNavigateToCatalog: (() -> Unit)? = null
) {
    val orders by orderViewModel.orders.collectAsState()
    val isLoading by orderViewModel.isLoading.collectAsState()
    val errorMessage by orderViewModel.errorMessage.collectAsState()

    LaunchedEffect(Unit) {
        orderViewModel.loadOrderHistory()
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "My Order History",
                    fontWeight = FontWeight.Black,
                    fontSize = 22.sp,
                    color = TextPrimary
                )
                Text(
                    text = "Track status and view details of your previous orders.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            IconButton(
                onClick = { orderViewModel.loadOrderHistory() },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (errorMessage != null) {
            Surface(
                color = androidx.compose.ui.graphics.Color(0xFFFFE4E6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = errorMessage!!,
                        color = StatusError,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = { orderViewModel.loadOrderHistory() }) {
                        Text("Retry", color = StatusError, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    Icon(
                        Icons.Default.ReceiptLong,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(64.dp)
                    )
                    Text(
                        text = "No past orders found.",
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        fontSize = 16.sp,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                    Text(
                        text = "When you place produce orders, you can track their packing and cold-chain transit progress right here.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
                    )

                    if (onNavigateToCatalog != null) {
                        AAAButton(
                            text = "Browse Fresh Produce",
                            icon = Icons.Default.ShoppingBag,
                            onClick = onNavigateToCatalog
                        )
                    }
                }
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToOrderTracking(ord.id) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = ord.orderNumber,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = TextPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    StatusBadge(status = ord.status)
                                }

                                Text(
                                    text = "Placed: ${ord.placedAt?.take(10) ?: "Recent"}",
                                    fontSize = 12.sp,
                                    color = TextMuted,
                                    modifier = Modifier.padding(top = 4.dp)
                                )

                                Text(
                                    text = "KES ${String.format("%.2f", ord.netAmount)}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }

                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                        }
                    }
                }
            }
        }
    }
}
