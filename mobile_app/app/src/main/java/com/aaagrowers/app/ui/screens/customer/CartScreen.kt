package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.RemoveShoppingCart
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAAAsyncImage
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.QuantityPicker
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.CartViewModel

@Composable
fun CartScreen(
    cartViewModel: CartViewModel,
    onNavigateToCheckout: () -> Unit,
    onNavigateToCatalog: () -> Unit
) {
    val cartItems by cartViewModel.cartItems.collectAsState()

    if (cartItems.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(EmeraldContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.RemoveShoppingCart,
                    contentDescription = null,
                    tint = EmeraldPrimary,
                    modifier = Modifier.size(40.dp)
                )
            }

            Text(
                text = "Your Cart is Empty",
                fontWeight = FontWeight.Black,
                fontSize = 20.sp,
                color = TextPrimary,
                modifier = Modifier.padding(top = 16.dp)
            )

            Text(
                text = "Explore our selection of export-grade vegetables, herbs and flowers to get started.",
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AAAButton(
                text = "Browse Catalog",
                onClick = onNavigateToCatalog,
                icon = Icons.Default.ShoppingBag,
                modifier = Modifier.width(200.dp)
            )
        }
        return
    }

    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = CardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Subtotal", fontSize = 13.sp, color = TextSecondary)
                        Text(
                            text = "KES ${String.format("%.2f", cartViewModel.getSubtotal())}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Delivery Fee", fontSize = 13.sp, color = TextSecondary)
                        Text(
                            text = if (cartViewModel.getShippingFee() == 0.0) "FREE" else "KES ${String.format("%.2f", cartViewModel.getShippingFee())}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (cartViewModel.getShippingFee() == 0.0) EmeraldPrimary else TextPrimary
                        )
                    }

                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Estimated Total", fontWeight = FontWeight.Black, fontSize = 16.sp, color = TextPrimary)
                        Text(
                            text = "KES ${String.format("%.2f", cartViewModel.getNetTotal())}",
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp,
                            color = EmeraldPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    AAAButton(
                        text = "Proceed to Checkout",
                        onClick = onNavigateToCheckout
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Shopping Cart (${cartViewModel.getItemCount()} items)",
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp,
                    color = TextPrimary
                )

                TextButton(onClick = { cartViewModel.clearCart() }) {
                    Text("Clear", color = StatusError, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBg),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AAAAsyncImage(
                                imageUrl = item.product.imageUrl,
                                contentDescription = item.product.name,
                                fallbackTitle = item.product.name,
                                modifier = Modifier
                                    .size(70.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                Text(
                                    text = "KES ${String.format("%.2f", item.product.price)} / ${item.product.unit}",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                                Text(
                                    text = "Total: KES ${String.format("%.2f", item.subtotal)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EmeraldPrimary,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                IconButton(
                                    onClick = { cartViewModel.removeFromCart(item.product.id) },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Remove", tint = TextMuted, modifier = Modifier.size(18.dp))
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                QuantityPicker(
                                    quantity = item.quantity,
                                    onQuantityChange = { cartViewModel.updateQuantity(item.product.id, it) },
                                    maxQuantity = item.product.currentStock
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
