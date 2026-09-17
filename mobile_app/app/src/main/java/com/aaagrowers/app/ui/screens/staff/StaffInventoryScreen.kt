package com.aaagrowers.app.ui.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.data.model.InventoryItem
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.StaffViewModel

@Composable
fun StaffInventoryScreen(
    staffViewModel: StaffViewModel
) {
    val inventory by staffViewModel.inventory.collectAsState()
    val isLoading by staffViewModel.isLoading.collectAsState()
    var lowStockOnly by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<InventoryItem?>(null) }
    var qtyChange by remember { mutableStateOf("50") }
    var movementType by remember { mutableStateOf("RESTOCK") }
    var adjustNotes by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(lowStockOnly) {
        staffViewModel.loadInventory(lowStockOnly = lowStockOnly)
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
                Text("Warehouse Stock Control", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextPrimary)
                Text("Real-time inventory levels, safety thresholds & restocks", fontSize = 12.sp, color = TextSecondary)
            }
            IconButton(onClick = { staffViewModel.loadInventory(lowStockOnly) }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Filter Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = lowStockOnly,
                onClick = { lowStockOnly = !lowStockOnly },
                label = { Text("⚠️ Low Stock Warnings Only", fontSize = 11.sp) }
            )
            Text("${inventory.size} SKUs", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted)
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
        } else if (inventory.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No inventory stock records found.", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(inventory) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)
                                Text("SKU: ${item.productSku ?: "N/A"} • Threshold: ${item.lowStockThreshold}", fontSize = 11.sp, color = TextMuted)

                                if (item.isOutOfStock) {
                                    Text("OUT OF STOCK", color = StatusError, fontSize = 11.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(top = 2.dp))
                                } else if (item.isLowStock) {
                                    Text("LOW STOCK WARNING", color = StatusWarning, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 2.dp))
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("${item.currentStock} units", fontWeight = FontWeight.Black, fontSize = 16.sp, color = if (item.isLowStock) StatusError else EmeraldPrimary)

                                Button(
                                    onClick = {
                                        selectedItem = item
                                        qtyChange = "50"
                                        movementType = "RESTOCK"
                                        adjustNotes = ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.padding(top = 6.dp)
                                ) {
                                    Text("Adjust", color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Adjust Stock Dialog
        if (selectedItem != null) {
            AlertDialog(
                onDismissRequest = { selectedItem = null },
                title = { Text("Adjust Stock: ${selectedItem?.productName}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Movement Reason", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("RESTOCK", "DAMAGE", "ADJUSTMENT").forEach { mv ->
                                FilterChip(
                                    selected = movementType == mv,
                                    onClick = {
                                        movementType = mv
                                        if (mv == "DAMAGE") qtyChange = "-10" else if (mv == "RESTOCK") qtyChange = "50"
                                    },
                                    label = { Text(mv, fontSize = 10.sp) }
                                )
                            }
                        }

                        AAATextField(
                            value = qtyChange,
                            onValueChange = { qtyChange = it },
                            label = "Quantity Change (+ / -)",
                            placeholder = "e.g. 50 or -10"
                        )

                        AAATextField(
                            value = adjustNotes,
                            onValueChange = { adjustNotes = it },
                            label = "Audit Remarks (Optional)",
                            placeholder = "e.g. Received from supplier depot"
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val qty = qtyChange.toIntOrNull() ?: 0
                            staffViewModel.adjustStock(selectedItem!!.productId, qty, movementType, adjustNotes) { success, msg ->
                                resultMessage = msg
                                selectedItem = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Apply Adjustment", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedItem = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
