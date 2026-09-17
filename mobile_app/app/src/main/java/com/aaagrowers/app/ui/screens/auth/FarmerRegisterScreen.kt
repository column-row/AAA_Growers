package com.aaagrowers.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.AuthViewModel

@Composable
fun FarmerRegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var farmName by remember { mutableStateOf("") }
    var farmLocation by remember { mutableStateOf("Naivasha") }
    var farmSizeAcres by remember { mutableStateOf("5.0") }
    var cropsGrown by remember { mutableStateOf("French Beans, Snow Peas") }
    var experienceYears by remember { mutableStateOf("3") }
    var nationalId by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onBackToLogin, modifier = Modifier.padding(bottom = 8.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }

        Text(
            text = "Farmer & Outgrower Enrollment",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = TextPrimary
        )
        Text(
            text = "Partner with AAA Growers for export supply chains, agronomic masterclasses, and GlobalG.A.P. certifications.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        if (errorMessage != null) {
            Surface(
                color = Color(0xFFFFE4E6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = StatusError,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Text(
            text = "Personal Details",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = EmeraldPrimary,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AAATextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = "First Name",
                modifier = Modifier.weight(1f)
            )
            AAATextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = "Last Name",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AAATextField(
            value = email,
            onValueChange = { email = it },
            label = "Email Address",
            leadingIcon = Icons.Default.Email
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AAATextField(
                value = phone,
                onValueChange = { phone = it },
                label = "Phone",
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.Phone
            )
            AAATextField(
                value = nationalId,
                onValueChange = { nationalId = it },
                label = "National ID",
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.Badge
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Farm Profile & Acreage",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = EarthGold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        AAATextField(
            value = farmName,
            onValueChange = { farmName = it },
            label = "Farm Name",
            placeholder = "e.g. Rift Valley Organic Farm",
            leadingIcon = Icons.Default.Agriculture
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AAATextField(
                value = farmLocation,
                onValueChange = { farmLocation = it },
                label = "Location",
                modifier = Modifier.weight(1f),
                leadingIcon = Icons.Default.Place
            )
            AAATextField(
                value = farmSizeAcres,
                onValueChange = { farmSizeAcres = it },
                label = "Size (Acres)",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        AAATextField(
            value = cropsGrown,
            onValueChange = { cropsGrown = it },
            label = "Crops Grown",
            placeholder = "e.g. Fine French Beans, Sugar Snaps, Herbs"
        )

        Spacer(modifier = Modifier.height(12.dp))

        AAATextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            leadingIcon = Icons.Default.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        AAAButton(
            text = "Enroll as Outgrower Farmer",
            isLoading = isLoading,
            onClick = {
                val req = com.aaagrowers.app.data.model.RegisterFarmerRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    phone = phone.trim().ifBlank { null },
                    password = password,
                    farmName = farmName.trim(),
                    farmLocation = farmLocation.trim(),
                    farmSizeAcres = farmSizeAcres.toDoubleOrNull() ?: 1.0,
                    cropsGrown = cropsGrown.trim().ifBlank { null },
                    farmingExperienceYears = experienceYears.toIntOrNull() ?: 1,
                    nationalId = nationalId.trim().ifBlank { null }
                )
                viewModel.registerFarmer(req, onRegisterSuccess)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already registered? ", fontSize = 13.sp, color = TextSecondary)
            Text(
                text = "Sign In",
                color = EmeraldPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { onBackToLogin() }
            )
        }
    }
}
