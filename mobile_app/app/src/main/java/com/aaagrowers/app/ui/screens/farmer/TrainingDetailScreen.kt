package com.aaagrowers.app.ui.screens.farmer

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.StatusBadge
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.FarmerViewModel

@Composable
fun TrainingDetailScreen(
    sessionId: Int,
    farmerViewModel: FarmerViewModel,
    onNavigateToBookings: () -> Unit,
    onBack: () -> Unit
) {
    val session by farmerViewModel.selectedSession.collectAsState()
    val isLoading by farmerViewModel.isLoading.collectAsState()
    var bookingMessage by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    LaunchedEffect(sessionId) {
        farmerViewModel.loadTrainingDetail(sessionId)
    }

    if (isLoading || session == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = EmeraldPrimary)
        }
        return
    }

    val s = session!!

    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = CardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    if (bookingMessage != null) {
                        Surface(
                            color = if (isSuccess) EmeraldContainer else Color(0xFFFFE4E6),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Text(
                                text = bookingMessage!!,
                                color = if (isSuccess) EmeraldDark else StatusError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    if (isSuccess) {
                        AAAButton(
                            text = "View My Bookings",
                            onClick = onNavigateToBookings
                        )
                    } else {
                        AAAButton(
                            text = if (s.isFull) "Session Fully Booked" else "Confirm Training Seat",
                            enabled = !s.isFull,
                            onClick = {
                                farmerViewModel.bookSession(s.id) { success, msg ->
                                    isSuccess = success
                                    bookingMessage = msg
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(paddingValues)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Text("Masterclass Curriculum", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .padding(bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Surface(
                            color = EmeraldContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = s.category,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        Text(
                            text = s.title,
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                        )

                        Text(
                            text = "Trainer: ${s.trainerName}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldPrimary,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        HorizontalDivider(color = BorderLight)

                        Spacer(modifier = Modifier.height(12.dp))

                        InfoRow(icon = Icons.Default.CalendarMonth, title = "Date", value = s.trainingDate)
                        InfoRow(icon = Icons.Default.Schedule, title = "Time", value = "${s.startTime} - ${s.endTime}")
                        InfoRow(icon = Icons.Default.LocationOn, title = "Venue", value = s.location)
                        InfoRow(icon = Icons.Default.People, title = "Capacity", value = "${s.bookedCount} / ${s.capacity} Farmers Enrolled")
                    }
                }

                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("Course Description & Competencies", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextPrimary)
                        Text(
                            text = s.description,
                            fontSize = 13.sp,
                            color = TextSecondary,
                            lineHeight = 20.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Surface(
                            color = EarthGold.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = EarthDark, modifier = Modifier.size(28.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = "Completion of this masterclass issues an official GlobalG.A.P. Competency Certificate to your digital profile.",
                                    fontSize = 12.sp,
                                    color = EarthDark,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = "$title: ", fontSize = 12.sp, color = TextSecondary)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}
