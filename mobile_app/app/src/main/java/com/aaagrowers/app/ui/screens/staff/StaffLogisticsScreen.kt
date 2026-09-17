package com.aaagrowers.app.ui.screens.staff

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.data.model.DispatchInfo
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.components.StatusBadge
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.StaffViewModel

@Composable
fun StaffLogisticsScreen(
    staffViewModel: StaffViewModel
) {
    val dispatches by staffViewModel.dispatches.collectAsState()
    val isLoading by staffViewModel.isLoading.collectAsState()
    var selectedDispatch by remember { mutableStateOf<DispatchInfo?>(null) }
    var newStatus by remember { mutableStateOf("IN_TRANSIT") }
    var trackingNotes by remember { mutableStateOf("") }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        staffViewModel.loadDispatches()
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
                Text("Logistics & Cold-Chain Dispatch", fontWeight = FontWeight.Black, fontSize = 20.sp, color = TextPrimary)
                Text("Live delivery tracking, courier assignments & transit updates", fontSize = 12.sp, color = TextSecondary)
            }
            IconButton(onClick = { staffViewModel.loadDispatches() }) {
                Icon(Icons.Default.Refresh, contentDescription = "Refresh", tint = EmeraldPrimary)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
        } else if (dispatches.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No active dispatches found.", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(dispatches) { dsp ->
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
                                    text = dsp.dispatchNumber,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                StatusBadge(status = dsp.status)
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Driver: ${dsp.driverName ?: "Unassigned"}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                if (dsp.vehicleReg != null) {
                                    Text(" (${dsp.vehicleReg})", fontSize = 12.sp, color = TextMuted)
                                }
                            }

                            if (!dsp.deliveryAddress.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Destination: ${dsp.deliveryAddress}", fontSize = 12.sp, color = TextSecondary)
                                }
                            }

                            if (!dsp.trackingNotes.isNullOrBlank()) {
                                Row(
                                    modifier = Modifier.padding(top = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Notes, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Milestone: ${dsp.trackingNotes}", fontSize = 11.sp, color = TextMuted)
                                }
                            }

                            HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Button(
                                    onClick = {
                                        selectedDispatch = dsp
                                        newStatus = if (dsp.status == "PENDING" || dsp.status == "ASSIGNED") "IN_TRANSIT" else "DELIVERED"
                                        trackingNotes = dsp.trackingNotes ?: ""
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                                    shape = RoundedCornerShape(10.dp),
                                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Update Transit Status", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Status Update Modal Sheet
        if (selectedDispatch != null) {
            AlertDialog(
                onDismissRequest = { selectedDispatch = null },
                title = { Text("Update Dispatch #${selectedDispatch?.dispatchNumber}", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Transit Status", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("ASSIGNED", "IN_TRANSIT", "DELIVERED").forEach { st ->
                                FilterChip(
                                    selected = newStatus == st,
                                    onClick = { newStatus = st },
                                    label = { Text(st.replace("_", " "), fontSize = 10.sp) }
                                )
                            }
                        }

                        AAATextField(
                            value = trackingNotes,
                            onValueChange = { trackingNotes = it },
                            label = "Courier / Route Notes",
                            placeholder = "e.g. Arrived at customer gate"
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            staffViewModel.updateDispatchStatus(selectedDispatch!!.id, newStatus, trackingNotes) { success, msg ->
                                resultMessage = msg
                                selectedDispatch = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                    ) {
                        Text("Save Status", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedDispatch = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}
