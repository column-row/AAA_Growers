package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.ProductCard
import com.aaagrowers.app.ui.theme.EmeraldPrimary
import com.aaagrowers.app.ui.theme.SurfaceBg
import com.aaagrowers.app.ui.theme.TextPrimary
import com.aaagrowers.app.ui.theme.TextSecondary
import com.aaagrowers.app.ui.viewmodel.CartViewModel
import com.aaagrowers.app.ui.viewmodel.CustomerViewModel

@Composable
fun ProductListScreen(
    initialCategoryId: Int? = null,
    customerViewModel: CustomerViewModel,
    cartViewModel: CartViewModel,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToCart: () -> Unit,
    onBack: () -> Unit
) {
    var selectedCategoryId by remember { mutableStateOf(initialCategoryId) }
    val products by customerViewModel.products.collectAsState()
    val categories by customerViewModel.categories.collectAsState()
    val isLoading by customerViewModel.isLoading.collectAsState()

    LaunchedEffect(selectedCategoryId) {
        customerViewModel.filterProducts(categoryId = selectedCategoryId)
    }

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
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Text(
                    text = "Fresh Produce Catalog",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextPrimary
                )
            }

            IconButton(onClick = onNavigateToCart) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = EmeraldPrimary)
            }
        }

        // Category Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedCategoryId == null,
                    onClick = { selectedCategoryId = null },
                    label = { Text("All Produce", fontSize = 12.sp) }
                )
            }
            items(categories) { cat ->
                FilterChip(
                    selected = selectedCategoryId == cat.id,
                    onClick = { selectedCategoryId = cat.id },
                    label = { Text(cat.name, fontSize = 12.sp) }
                )
            }
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "No produce found in this category.", color = TextSecondary, fontSize = 14.sp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .padding(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                val chunked = products.chunked(2)
                chunked.forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        rowItems.forEach { item ->
                            ProductCard(
                                product = item,
                                onClick = { onNavigateToProduct(item.id) },
                                onAddToCart = { cartViewModel.addToCart(item, 1) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
