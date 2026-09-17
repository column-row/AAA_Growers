package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAAAsyncImage
import com.aaagrowers.app.ui.components.AAAButton
import com.aaagrowers.app.ui.components.QuantityPicker
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.CartViewModel
import com.aaagrowers.app.ui.viewmodel.CustomerViewModel

@Composable
fun ProductDetailScreen(
    productId: Int,
    customerViewModel: CustomerViewModel,
    cartViewModel: CartViewModel,
    onNavigateToCart: () -> Unit,
    onBack: () -> Unit
) {
    var quantity by remember { mutableStateOf(1) }
    var addedToCartSnackbar by remember { mutableStateOf(false) }

    val product by customerViewModel.selectedProduct.collectAsState()
    val isLoading by customerViewModel.isLoading.collectAsState()

    LaunchedEffect(productId) {
        customerViewModel.loadProductDetail(productId)
    }

    if (isLoading || product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = EmeraldPrimary)
        }
        return
    }

    val prod = product!!

    Scaffold(
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = CardBg,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    QuantityPicker(
                        quantity = quantity,
                        onQuantityChange = { quantity = it },
                        maxQuantity = prod.currentStock
                    )

                    AAAButton(
                        text = "Add to Cart (KES ${String.format("%.2f", prod.price * quantity)})",
                        onClick = {
                            cartViewModel.addToCart(prod, quantity)
                            addedToCartSnackbar = true
                        },
                        enabled = prod.isInStock,
                        icon = Icons.Default.AddShoppingCart,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        snackbarHost = {
            if (addedToCartSnackbar) {
                Snackbar(
                    action = {
                        TextButton(onClick = onNavigateToCart) {
                            Text("VIEW CART", color = EmeraldLight, fontWeight = FontWeight.Bold)
                        }
                    },
                    modifier = Modifier.padding(16.dp),
                    containerColor = EmeraldDark
                ) {
                    Text("Added ${quantity}x ${prod.name} to cart!", color = Color.White)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceBg)
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // Image Header with overlay back & cart
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(BorderLight)
            ) {
                AAAAsyncImage(
                    imageUrl = prod.imageUrl,
                    contentDescription = prod.name,
                    fallbackTitle = prod.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    IconButton(
                        onClick = onNavigateToCart,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.4f))
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.White)
                    }
                }
            }

            // Info Section
            Card(
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
                colors = CardDefaults.cardColors(containerColor = CardBg),
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = EmeraldContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = prod.categoryName ?: "Export Produce",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldDark,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Text(
                            text = if (prod.isInStock) "${prod.currentStock} in stock" else "Out of Stock",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (prod.isInStock) EmeraldPrimary else StatusError
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = prod.name,
                        fontWeight = FontWeight.Black,
                        fontSize = 22.sp,
                        color = TextPrimary
                    )

                    Text(
                        text = "SKU: ${prod.sku}",
                        fontSize = 12.sp,
                        color = TextMuted,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(top = 2.dp)
                    )

                    Row(
                        modifier = Modifier.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = "KES ${String.format("%.2f", prod.price)}",
                            fontWeight = FontWeight.Black,
                            fontSize = 24.sp,
                            color = EmeraldPrimary
                        )
                        Text(
                            text = " / ${prod.unit}",
                            fontSize = 13.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(start = 4.dp, bottom = 3.dp)
                        )
                    }

                    HorizontalDivider(color = BorderLight, modifier = Modifier.padding(vertical = 8.dp))

                    Text(
                        text = "About this Produce",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Text(
                        text = prod.description ?: "Grown under strict agronomic standards in Kenya's fertile Rift Valley and Mount Kenya regions. Harvested fresh for optimal flavour, nutrition and shelf life.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Quality badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        QualityBadge(icon = Icons.Default.Verified, title = "Export Grade")
                        QualityBadge(icon = Icons.Default.Eco, title = "Pesticide Checked")
                        QualityBadge(icon = Icons.Default.LocalShipping, title = "Cold Chain")
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityBadge(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(EmeraldContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = EmeraldPrimary, modifier = Modifier.size(22.dp))
        }
        Text(text = title, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary, modifier = Modifier.padding(top = 4.dp))
    }
}
