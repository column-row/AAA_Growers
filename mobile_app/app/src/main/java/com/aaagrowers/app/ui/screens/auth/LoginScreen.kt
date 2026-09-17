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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onLoginSuccess: (String) -> Unit,
    onNavigateToCustomerRegister: () -> Unit,
    onNavigateToFarmerRegister: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("customer@aaagrowers.co.ke") }
    var password by remember { mutableStateOf("Password123!") }
    var showServerSettings by remember { mutableStateOf(false) }

    val currentServerUrl by viewModel.serverUrl.collectAsState()
    var tempServerUrl by remember(currentServerUrl) { mutableStateOf(currentServerUrl) }

    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Server URL indicator bar
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(EmeraldContainer)
                .clickable { showServerSettings = true }
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.Dns, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Server: ${currentServerUrl.replace("/api/", "")}",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EmeraldDark
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(Icons.Default.Settings, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(12.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Logo
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(EmeraldPrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Spa,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "AAA GROWERS",
            fontWeight = FontWeight.Black,
            fontSize = 26.sp,
            color = TextPrimary,
            letterSpacing = 1.sp
        )
        Text(
            text = "Enterprise Agricultural E-Commerce & Logistics",
            fontSize = 13.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = CardBg),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFFE4E6),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = errorMessage!!,
                                color = StatusError,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Tip: Tap the Server indicator above if you are on a physical phone to enter your computer's Wi-Fi IP (e.g. 192.168.x.x).",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }

                AAATextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email Address",
                    placeholder = "name@domain.com",
                    leadingIcon = Icons.Default.Email
                )

                Spacer(modifier = Modifier.height(14.dp))

                AAATextField(
                    value = password,
                    onValueChange = { password = it },
                    label = "Password",
                    placeholder = "••••••••",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(20.dp))

                AAAButton(
                    text = "Sign In",
                    isLoading = isLoading,
                    onClick = {
                        viewModel.login(email, password) { role ->
                            onLoginSuccess(role)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Demo Presets for all roles
        Text("Quick Role Switcher (1-Tap Demo)", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AssistChip(
                onClick = { email = "customer@aaagrowers.co.ke"; password = "Password123!" },
                label = { Text("🛒 Customer", fontSize = 10.sp) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            AssistChip(
                onClick = { email = "farmer@aaagrowers.co.ke"; password = "Password123!" },
                label = { Text("🌱 Farmer", fontSize = 10.sp) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            AssistChip(
                onClick = { email = "driver@aaagrowers.co.ke"; password = "Password123!" },
                label = { Text("🚚 Driver", fontSize = 10.sp) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            AssistChip(
                onClick = { email = "inventory@aaagrowers.co.ke"; password = "Password123!" },
                label = { Text("📦 Warehouse", fontSize = 10.sp) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            AssistChip(
                onClick = { email = "dispatch@aaagrowers.co.ke"; password = "Password123!" },
                label = { Text("📋 Logistics Mgr", fontSize = 10.sp) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
            AssistChip(
                onClick = { email = "admin@aaagrowers.co.ke"; password = "Password123!" },
                label = { Text("👑 Admin", fontSize = 10.sp) },
                modifier = Modifier.padding(horizontal = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "New to AAA Growers?",
            fontSize = 13.sp,
            color = TextSecondary,
            fontWeight = FontWeight.Medium
        )

        Button(
            onClick = { onNavigateToCustomerRegister() },
            colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Icon(Icons.Default.PersonAdd, contentDescription = null, tint = EmeraldDark, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Create Account (Choose from 6 Roles)", color = EmeraldDark, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }

    // Server Configuration Dialog
    if (showServerSettings) {
        AlertDialog(
            onDismissRequest = { showServerSettings = false },
            title = { Text("Backend Server Configuration", fontWeight = FontWeight.Bold, fontSize = 16.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = "Set the Flask REST API URL. If testing on a physical phone, enter your computer's local Wi-Fi IP.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )

                    OutlinedTextField(
                        value = tempServerUrl,
                        onValueChange = { tempServerUrl = it },
                        label = { Text("Server API Base URL") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )

                    Text("Quick Presets:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { tempServerUrl = "http://172.17.21.103:5000/api/" },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("📱 Wi-Fi (172.17.21.103)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = { tempServerUrl = "http://10.0.2.2:5000/api/" },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Emulator (10.0.2.2)", color = EmeraldDark, fontSize = 10.sp)
                        }
                        Button(
                            onClick = { tempServerUrl = "http://localhost:5000/api/" },
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldContainer),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text("Localhost", color = EmeraldDark, fontSize = 10.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateServerUrl(tempServerUrl, context)
                        showServerSettings = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EmeraldPrimary)
                ) {
                    Text("Save & Connect", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showServerSettings = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
