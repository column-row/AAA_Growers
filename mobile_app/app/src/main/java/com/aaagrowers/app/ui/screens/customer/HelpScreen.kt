package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.theme.*

@Composable
fun HelpScreen(onBack: () -> Unit) {
    val faqs = listOf(
        Pair("How is fresh produce delivered?", "All AAA Growers produce is transported in temperature-controlled refrigerated cold-chain vehicles directly from our farms and packing stations to preserve maximum freshness and shelf-life."),
        Pair("What payment methods are supported?", "We accept M-Pesa Express, Visa/Mastercard credit & debit cards, direct electronic bank transfer, and cash on delivery."),
        Pair("What are the delivery areas and timelines?", "We deliver across Nairobi Metropolitan (Same-day dispatch for orders before 11:00 AM) and offer next-day refrigerated delivery across major Kenyan urban centers."),
        Pair("How do outgrower farmers join the training academy?", "Farmers can enroll through the 'Register as Farmer' portal. Once registered, you can view the upcoming masterclasses in our Training section, book sessions, and earn GlobalG.A.P. competence certificates."),
        Pair("What if an item arrives damaged or missing?", "Our customer satisfaction policy includes immediate replacement or refund. Please contact customer care or submit a request within 24 hours of delivery.")
    )

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
            Text("Help & Frequently Asked Questions", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            faqs.forEach { (question, answer) ->
                var isExpanded by remember { mutableStateOf(false) }

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = question,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary,
                                modifier = Modifier.weight(1f)
                            )
                            Icon(
                                if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = EmeraldPrimary
                            )
                        }

                        if (isExpanded) {
                            Text(
                                text = answer,
                                fontSize = 13.sp,
                                color = TextSecondary,
                                lineHeight = 18.sp,
                                modifier = Modifier.padding(top = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
