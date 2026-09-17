package com.aaagrowers.app.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.StatusBadge
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.AuthViewModel
import com.aaagrowers.app.ui.viewmodel.FarmerViewModel

@Composable
fun FarmerHomeScreen(
    authViewModel: AuthViewModel,
    farmerViewModel: FarmerViewModel,
    onNavigateToTrainings: () -> Unit,
    onNavigateToTrainingDetail: (Int) -> Unit,
    onNavigateToBookings: () -> Unit,
    onNavigateToCertificates: () -> Unit,
    onNavigateToCertificateDetail: (Int) -> Unit
) {
    val user by authViewModel.user.collectAsState()
    val sessions by farmerViewModel.sessions.collectAsState()
    val bookings by farmerViewModel.myBookings.collectAsState()
    val certificates by farmerViewModel.certificates.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Welcome Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Outgrower Farmer Portal", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.SemiBold)
                Text(user?.fullName ?: "Farmer", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
            }

            Surface(
                color = EarthGold.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = "${user?.farmerProfile?.farmSizeAcres ?: 5.0} Acres",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EarthDark,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        // Farm Overview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = EarthDark)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = user?.farmerProfile?.farmName ?: "Rift Valley Farm Cluster",
                        fontWeight = FontWeight.Black,
                        fontSize = 17.sp,
                        color = Color.White
                    )
                    Icon(Icons.Default.Agriculture, contentDescription = null, tint = EarthGold)
                }

                Text(
                    text = "Location: ${user?.farmerProfile?.farmLocation ?: "Naivasha, Kenya"}",
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(top = 2.dp)
                )

                Text(
                    text = "Crops: ${user?.farmerProfile?.cropsGrown ?: "French Beans, Baby Corn, Herbs"}",
                    fontSize = 12.sp,
                    color = EarthGold,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    FarmStat(count = bookings.size.toString(), label = "Bookings", onClick = onNavigateToBookings)
                    FarmStat(count = certificates.size.toString(), label = "Certificates", onClick = onNavigateToCertificates)
                    FarmStat(count = "${user?.farmerProfile?.farmingExperienceYears ?: 3} yrs", label = "Experience", onClick = {})
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Upcoming Agronomic Training Sessions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Upcoming Training Programs", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text(
                text = "View All",
                color = EmeraldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onNavigateToTrainings() }
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(sessions) { session ->
                Card(
                    modifier = Modifier
                        .width(260.dp)
                        .clickable { onNavigateToTrainingDetail(session.id) },
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = EmeraldContainer,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = session.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldDark,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "${session.availableSlots} slots left",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (session.isFull) StatusError else EmeraldPrimary
                            )
                        }

                        Text(
                            text = session.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = TextPrimary,
                            maxLines = 2,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Text(
                            text = "📅 ${session.trainingDate} (${session.startTime})",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = "📍 ${session.location}",
                            fontSize = 11.sp,
                            color = TextMuted,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // My Certificates Preview
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("My Verified Certifications", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextPrimary)
            Text(
                text = "View All",
                color = EmeraldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                modifier = Modifier.clickable { onNavigateToCertificates() }
            )
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (certificates.isEmpty()) {
                Surface(
                    color = CardBg,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No certificates earned yet. Complete training masterclasses to receive verified credentials.",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                certificates.take(2).forEach { cert ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToCertificateDetail(cert.id) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(cert.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
                                Text(cert.certificateNumber, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = EmeraldPrimary, modifier = Modifier.padding(top = 2.dp))
                                Text("Issued: ${cert.issueDate}", fontSize = 11.sp, color = TextMuted)
                            }
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = EarthGold, modifier = Modifier.size(28.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FarmStat(count: String, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(count, fontWeight = FontWeight.Black, fontSize = 16.sp, color = Color.White)
        Text(label, fontSize = 11.sp, color = EarthGold)
    }
}
