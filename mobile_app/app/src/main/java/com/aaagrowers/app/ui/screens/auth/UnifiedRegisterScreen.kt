package com.aaagrowers.app.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.aaagrowers.app.data.model.RegisterUserRequest
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.AuthViewModel

data class RoleOption(val id: String, val title: String, val subtitle: String, val icon: String)

@Composable
fun UnifiedRegisterScreen(
    viewModel: AuthViewModel,
    initialRole: String = "CUSTOMER",
    onRegisterSuccess: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(initialRole) }

    // Common Credentials
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("Nairobi") }
    var address by remember { mutableStateOf("") }

    // Farmer specifics
    var farmName by remember { mutableStateOf("") }
    var farmLocation by remember { mutableStateOf("Naivasha") }
    var farmSizeAcres by remember { mutableStateOf("5.0") }
    var cropsGrown by remember { mutableStateOf("French Beans, Snow Peas") }
    var farmingYears by remember { mutableStateOf("3") }
    var nationalId by remember { mutableStateOf("") }

    // Driver specifics
    var licenseNumber by remember { mutableStateOf("") }
    var vehicleReg by remember { mutableStateOf("") }
    var vehicleType by remember { mutableStateOf("Refrigerated Van (3-Ton)") }

    // Supplier specifics
    var companyName by remember { mutableStateOf("") }
    var supplyCategory by remember { mutableStateOf("Seeds & Fertilizers") }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val rolesList = listOf(
        RoleOption("CUSTOMER", "Customer", "Produce Buyer", "🛒"),
        RoleOption("FARMER", "Farmer", "Outgrower Academy", "🌱"),
        RoleOption("DRIVER", "Driver", "Fleet Logistics", "🚚"),
        RoleOption("INVENTORY_MANAGER", "Inventory", "Warehouse Stock", "📦"),
        RoleOption("DISPATCH_MANAGER", "Dispatch", "Logistics Routing", "📋"),
        RoleOption("SUPPLIER", "Supplier", "Farm Inputs Partner", "🤝")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(horizontal = 20.dp, vertical = 16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        IconButton(onClick = onBackToLogin, modifier = Modifier.padding(bottom = 4.dp)) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }

        Text(
            text = "Create AAA Growers Account",
            fontWeight = FontWeight.Black,
            fontSize = 24.sp,
            color = TextPrimary
        )
        Text(
            text = "Select your platform role to configure your dedicated mobile workspace.",
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 14.dp)
        )

        // Role Selector Chips
        Text("SELECT ACCOUNT ROLE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = EmeraldDark, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rolesList.forEach { role ->
                val isSelected = selectedRole == role.id
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) EmeraldPrimary else CardBg,
                    border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, BorderLight),
                    modifier = Modifier
                        .clickable { selectedRole = role.id }
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("${role.icon} ${role.title}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isSelected) Color.White else TextPrimary)
                        Text(role.subtitle, fontSize = 10.sp, color = if (isSelected) Color.White.copy(alpha = 0.8f) else TextMuted)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (errorMessage != null) {
            Surface(
                color = Color(0xFFFFE4E6),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            ) {
                Text(
                    text = errorMessage!!,
                    color = StatusError,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        // Account Credentials Section
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Personal & Account Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
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

                AAATextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "you@aaagrowers.co.ke",
                    leadingIcon = Icons.Default.Email
                )

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    AAATextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        placeholder = "+254700000000",
                        modifier = Modifier.weight(1f),
                        leadingIcon = Icons.Default.Phone
                    )
                    AAATextField(
                        value = city,
                        onValueChange = { city = it },
                        label = "City / County",
                        modifier = Modifier.weight(1f),
                        leadingIcon = Icons.Default.LocationCity
                    )
                }

                AAATextField(
                    value = address,
                    onValueChange = { address = it },
                    label = if (selectedRole == "CUSTOMER") "Delivery Address" else "Depot / Station / Office",
                    placeholder = "e.g. Westlands / Naivasha Farm Road"
                )

                AAATextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Role-Specific Profile Section
        if (selectedRole == "FARMER") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldContainer.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🌱 Outgrower Farm Profile", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = EmeraldDark)

                    AAATextField(
                        value = farmName,
                        onValueChange = { farmName = it },
                        label = "Farm Name",
                        placeholder = "e.g. Mount Longonot Fresh Farms"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AAATextField(
                            value = farmLocation,
                            onValueChange = { farmLocation = it },
                            label = "Farm Location",
                            modifier = Modifier.weight(1f)
                        )
                        AAATextField(
                            value = farmSizeAcres,
                            onValueChange = { farmSizeAcres = it },
                            label = "Acreage",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AAATextField(
                            value = cropsGrown,
                            onValueChange = { cropsGrown = it },
                            label = "Primary Crops",
                            modifier = Modifier.weight(1f)
                        )
                        AAATextField(
                            value = nationalId,
                            onValueChange = { nationalId = it },
                            label = "National ID",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (selectedRole == "DRIVER") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🚚 Logistics Driver Fleet Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)

                    AAATextField(
                        value = licenseNumber,
                        onValueChange = { licenseNumber = it },
                        label = "Driving License Number",
                        placeholder = "DL-12345"
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AAATextField(
                            value = vehicleReg,
                            onValueChange = { vehicleReg = it },
                            label = "Vehicle Registration",
                            placeholder = "KDC 567B",
                            modifier = Modifier.weight(1f)
                        )
                        AAATextField(
                            value = vehicleType,
                            onValueChange = { vehicleType = it },
                            label = "Vehicle Type",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        if (selectedRole == "SUPPLIER") {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("🤝 Agricultural Inputs Business Details", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextPrimary)

                    AAATextField(
                        value = companyName,
                        onValueChange = { companyName = it },
                        label = "Company / Trading Name",
                        placeholder = "e.g. Kenya Seed & Irrigation Co."
                    )

                    AAATextField(
                        value = supplyCategory,
                        onValueChange = { supplyCategory = it },
                        label = "Supply Category",
                        placeholder = "e.g. Certified Seeds, Fertilizers, Greenhouse Nets"
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        AAAButton(
            text = "Create ${rolesList.find { it.id == selectedRole }?.title ?: "Account"}",
            isLoading = isLoading,
            onClick = {
                val req = RegisterUserRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    phone = phone.trim().ifBlank { null },
                    password = password,
                    role = selectedRole,
                    city = city.trim().ifBlank { null },
                    address = address.trim().ifBlank { null },
                    farmName = if (selectedRole == "FARMER") farmName.trim().ifBlank { null } else null,
                    farmLocation = if (selectedRole == "FARMER") farmLocation.trim().ifBlank { null } else null,
                    farmSizeAcres = if (selectedRole == "FARMER") farmSizeAcres.toDoubleOrNull() ?: 1.0 else null,
                    cropsGrown = if (selectedRole == "FARMER") cropsGrown.trim().ifBlank { null } else null,
                    farmingExperienceYears = if (selectedRole == "FARMER") farmingYears.toIntOrNull() ?: 1 else null,
                    nationalId = if (selectedRole == "FARMER") nationalId.trim().ifBlank { null } else null,
                    licenseNumber = if (selectedRole == "DRIVER") licenseNumber.trim().ifBlank { null } else null,
                    vehicleRegistration = if (selectedRole == "DRIVER") vehicleReg.trim().ifBlank { null } else null,
                    vehicleType = if (selectedRole == "DRIVER") vehicleType.trim().ifBlank { null } else null,
                    companyName = if (selectedRole == "SUPPLIER") companyName.trim().ifBlank { null } else null,
                    supplyCategory = if (selectedRole == "SUPPLIER") supplyCategory.trim().ifBlank { null } else null
                )
                viewModel.registerUser(req) { role ->
                    onRegisterSuccess(role)
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(text = "Already have an account? ", fontSize = 13.sp, color = TextSecondary)
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
