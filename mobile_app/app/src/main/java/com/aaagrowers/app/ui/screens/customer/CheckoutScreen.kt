package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.AuthViewModel
import com.aaagrowers.app.ui.viewmodel.CartViewModel
import com.aaagrowers.app.ui.viewmodel.OrderViewModel

@Composable
fun CheckoutScreen(
    authViewModel: AuthViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    onOrderPlaced: (Int) -> Unit,
    onBack: () -> Unit
) {
    val user by authViewModel.user.collectAsState()
    var address by remember { mutableStateOf(user?.address ?: "Lavington Green Estate, House 14B") }
    var city by remember { mutableStateOf(user?.city ?: "Nairobi") }
    var phone by remember { mutableStateOf(user?.phone ?: "+254700000001") }
    var notes by remember { mutableStateOf("") }
    var selectedPaymentMethod by remember { mutableStateOf("MPESA") }

    val isLoading by orderViewModel.isLoading.collectAsState()
    val errorMessage by orderViewModel.errorMessage.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Checkout & Dispatch",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextPrimary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp)
        ) {
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

            // Delivery Details Section
            Text(
                text = "Delivery Destination",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    AAATextField(
                        value = address,
                        onValueChange = { address = it },
                        label = "Delivery Street / Estate Address",
                        leadingIcon = Icons.Default.LocationOn
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AAATextField(
                            value = city,
                            onValueChange = { city = it },
                            label = "City",
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Default.LocationCity
                        )
                        AAATextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Contact Phone",
                            modifier = Modifier.weight(1f),
                            leadingIcon = Icons.Default.Phone
                        )
                    }

                    AAATextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = "Gate Code / Delivery Remarks (Optional)",
                        leadingIcon = Icons.Default.Notes
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Payment Methods
            Text(
                text = "Payment Method",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = TextPrimary,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    PaymentOption(
                        title = "M-Pesa Express (STK Push)",
                        subtitle = "Instant prompt on your mobile phone",
                        icon = Icons.Default.PhoneAndroid,
                        isSelected = selectedPaymentMethod == "MPESA",
                        onClick = { selectedPaymentMethod = "MPESA" }
                    )

                    PaymentOption(
                        title = "Credit / Debit Card (Visa/Mastercard)",
                        subtitle = "Secure card payment processor",
                        icon = Icons.Default.CreditCard,
                        isSelected = selectedPaymentMethod == "CARD",
                        onClick = { selectedPaymentMethod = "CARD" }
                    )

                    PaymentOption(
                        title = "Direct Bank Transfer (EFT/RTGS)",
                        subtitle = "Corporate and bulk order settlements",
                        icon = Icons.Default.AccountBalance,
                        isSelected = selectedPaymentMethod == "BANK_TRANSFER",
                        onClick = { selectedPaymentMethod = "BANK_TRANSFER" }
                    )

                    PaymentOption(
                        title = "Cash on Delivery",
                        subtitle = "Pay courier upon fresh produce arrival",
                        icon = Icons.Default.Payments,
                        isSelected = selectedPaymentMethod == "CASH_ON_DELIVERY",
                        onClick = { selectedPaymentMethod = "CASH_ON_DELIVERY" }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Order Summary
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = EmeraldContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Payment Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EmeraldDark
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Subtotal", fontSize = 13.sp, color = TextSecondary)
                        Text("KES ${String.format("%.2f", cartViewModel.getSubtotal())}", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Cold Chain Dispatch Fee", fontSize = 13.sp, color = TextSecondary)
                        Text(
                            text = if (cartViewModel.getShippingFee() == 0.0) "FREE" else "KES ${String.format("%.2f", cartViewModel.getShippingFee())}",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }

                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Net Payable Amount", fontWeight = FontWeight.Black, fontSize = 15.sp, color = EmeraldDark)
                        Text(
                            "KES ${String.format("%.2f", cartViewModel.getNetTotal())}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = EmeraldDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            AAAButton(
                text = "Confirm & Authorize Payment (KES ${String.format("%.2f", cartViewModel.getNetTotal())})",
                isLoading = isLoading,
                onClick = {
                    orderViewModel.placeOrder(
                        deliveryAddress = address,
                        deliveryCity = city,
                        deliveryPhone = phone,
                        paymentMethod = selectedPaymentMethod,
                        notes = if (notes.isNotBlank()) notes else null,
                        onSuccess = { orderId ->
                            onOrderPlaced(orderId)
                        }
                    )
                }
            )
        }
    }
}

@Composable
private fun PaymentOption(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (isSelected) EmeraldContainer else SurfaceBg)
            .border(1.dp, if (isSelected) EmeraldPrimary else BorderLight, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = EmeraldPrimary)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextPrimary)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
        Icon(icon, contentDescription = null, tint = if (isSelected) EmeraldPrimary else TextMuted, modifier = Modifier.size(22.dp))
    }
}
