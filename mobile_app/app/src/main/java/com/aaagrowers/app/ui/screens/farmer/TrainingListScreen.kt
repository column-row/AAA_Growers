package com.aaagrowers.app.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.StatusBadge
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.FarmerViewModel

@Composable
fun TrainingListScreen(
    farmerViewModel: FarmerViewModel,
    onNavigateToDetail: (Int) -> Unit,
    onBack: () -> Unit
) {
    val sessions by farmerViewModel.sessions.collectAsState()
    val isLoading by farmerViewModel.isLoading.collectAsState()
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Export Standards", "Good Agricultural Practices", "Pest Management", "Irrigation & Soil")

    LaunchedEffect(selectedCategory) {
        farmerViewModel.loadTrainings(category = selectedCategory)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
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
            Text("Agronomic Training Programs", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All Masterclasses", fontSize = 12.sp) }
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategory == cat,
                    onClick = { selectedCategory = cat },
                    label = { Text(cat, fontSize = 12.sp) }
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.padding(bottom = 60.dp)
            ) {
                items(sessions) { s ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToDetail(s.id) },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
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
                                StatusBadge(status = if (s.isFull) "FULL" else "OPEN")
                            }

                            Text(
                                text = s.title,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                color = TextPrimary,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )

                            Text(
                                text = s.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                maxLines = 2,
                                lineHeight = 18.sp
                            )

                            HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("📅 ${s.trainingDate} (${s.startTime} - ${s.endTime})", fontSize = 11.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
                                    Text("📍 ${s.location}", fontSize = 11.sp, color = TextMuted, modifier = Modifier.padding(top = 2.dp))
                                }

                                Text(
                                    text = "${s.availableSlots} slots left",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (s.isFull) StatusError else EmeraldPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
