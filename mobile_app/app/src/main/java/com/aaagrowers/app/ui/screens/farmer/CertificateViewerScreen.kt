package com.aaagrowers.app.ui.screens.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.FarmerViewModel

@Composable
fun CertificateViewerScreen(
    certId: Int,
    farmerViewModel: FarmerViewModel,
    onBack: () -> Unit
) {
    val certificate by farmerViewModel.selectedCertificate.collectAsState()
    val isLoading by farmerViewModel.isLoading.collectAsState()

    LaunchedEffect(certId) {
        farmerViewModel.loadCertificateDetail(certId)
    }

    if (isLoading || certificate == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = EmeraldPrimary)
        }
        return
    }

    val cert = certificate!!

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
            Text("Verified Digital Certificate", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // High-Prestige Certificate Card
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(3.dp, EarthGold.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                    .padding(2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = EarthGold, modifier = Modifier.size(36.dp))
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "AAA GROWERS ACADEMY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EmeraldDark,
                        letterSpacing = 1.5.sp
                    )

                    Text(
                        text = "CERTIFICATE OF COMPETENCE",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    HorizontalDivider(
                        color = EarthGold.copy(alpha = 0.6f),
                        modifier = Modifier
                            .width(80.dp)
                            .padding(vertical = 12.dp),
                        thickness = 2.dp
                    )

                    Text(
                        text = "This officially certifies that",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    Text(
                        text = cert.farmerName,
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        color = EmeraldDark,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    Text(
                        text = "Farm: ${cert.farmName ?: "Outgrower Cluster"}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "has successfully completed evaluation and field practicals in",
                        fontSize = 12.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )

                    Surface(
                        color = EmeraldContainer,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        Text(
                            text = cert.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = EmeraldDark,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Lead Agronomist", fontSize = 10.sp, color = TextMuted)
                            Text(cert.trainerName ?: "Dr. Samuel Kipchoge", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            Text("Issued: ${cert.issueDate}", fontSize = 10.sp, color = TextMuted)
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text("Certificate ID", fontSize = 10.sp, color = TextMuted)
                            Text(cert.certificateNumber, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextPrimary)
                            Text("Status: VERIFIED", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = EmeraldPrimary)
                        }
                    }

                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 12.dp))

                    // Verification Hash
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SHA-256: ${cert.verificationHash.take(20)}...",
                            fontSize = 10.sp,
                            color = TextMuted,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}
