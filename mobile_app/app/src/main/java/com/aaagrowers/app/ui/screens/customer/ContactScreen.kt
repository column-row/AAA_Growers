package com.aaagrowers.app.ui.screens.customer

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
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.CustomerViewModel

@Composable
fun ContactScreen(
    customerViewModel: CustomerViewModel,
    onBack: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var statusMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

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
            Text("Contact Customer Care", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = TextPrimary)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (statusMessage != null) {
                Surface(
                    color = EmeraldContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = statusMessage!!,
                        color = EmeraldDark,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(14.dp)
                    )
                }
            }

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AAATextField(
                        value = fullName,
                        onValueChange = { fullName = it },
                        label = "Your Full Name",
                        leadingIcon = Icons.Default.Person
                    )

                    AAATextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        leadingIcon = Icons.Default.Email
                    )

                    AAATextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number (Optional)",
                        leadingIcon = Icons.Default.Phone
                    )

                    AAATextField(
                        value = subject,
                        onValueChange = { subject = it },
                        label = "Subject / Order Ref",
                        leadingIcon = Icons.Default.Subject
                    )

                    OutlinedTextField(
                        value = message,
                        onValueChange = { message = it },
                        placeholder = { Text("How can our team help you today?", fontSize = 13.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(110.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EmeraldPrimary,
                            unfocusedBorderColor = BorderLight
                        )
                    )
                }
            }

            AAAButton(
                text = "Send Message",
                isLoading = isSubmitting,
                enabled = fullName.isNotBlank() && email.isNotBlank() && message.isNotBlank(),
                onClick = {
                    isSubmitting = true
                    customerViewModel.submitContact(fullName, email, phone.ifBlank { null }, subject, message) { success, msg ->
                        isSubmitting = false
                        statusMessage = msg
                        if (success) {
                            subject = ""
                            message = ""
                        }
                    }
                }
            )
        }
    }
}
