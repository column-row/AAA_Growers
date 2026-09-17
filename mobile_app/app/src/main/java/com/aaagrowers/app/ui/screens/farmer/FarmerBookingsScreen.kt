package com.aaagrowers.app.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
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
import com.aaagrowers.app.ui.viewmodel.FarmerViewModel

@Composable
fun FarmerBookingsScreen(
    farmerViewModel: FarmerViewModel,
    onNavigateToTrainings: () -> Unit
) {
    val bookings by farmerViewModel.myBookings.collectAsState()
    val isLoading by farmerViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        farmerViewModel.loadMyBookings()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Text("My Training Bookings", fontWeight = FontWeight.Black, fontSize = 22.sp, color = TextPrimary)
        Text(
            text = "Track registered agronomy sessions, attendance and completion status.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else if (bookings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.School, contentDescription = null, tint = TextMuted, modifier = Modifier.size(56.dp))
                    Text("No session bookings yet.", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.padding(top = 12.dp))
                    TextButton(onClick = onNavigateToTrainings, modifier = Modifier.padding(top = 8.dp)) {
                        Text("Browse Training Sessions", fontWeight = FontWeight.Bold, color = EmeraldPrimary)
                    }
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(bookings) { b ->
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
                                    text = b.trainingDate ?: "Scheduled Date",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldPrimary
                                )
                                StatusBadge(status = b.status)
                            }

                            Text(
                                text = b.trainingTitle ?: "Agricultural Masterclass",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(vertical = 6.dp)
                            )

                            Text(
                                text = "Venue: ${b.trainingLocation ?: "AAA Academy"}",
                                fontSize = 12.sp,
                                color = TextSecondary
                            )

                            if (b.status == "BOOKED") {
                                HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = {
                                            farmerViewModel.cancelBooking(b.id) {}
                                        }
                                    ) {
                                        Text("Cancel Booking", color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
