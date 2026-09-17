package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aaagrowers.app.ui.components.AAATextField
import com.aaagrowers.app.ui.components.ProductCard
import com.aaagrowers.app.ui.theme.EmeraldPrimary
import com.aaagrowers.app.ui.theme.SurfaceBg
import com.aaagrowers.app.ui.theme.TextSecondary
import com.aaagrowers.app.ui.viewmodel.CartViewModel
import com.aaagrowers.app.ui.viewmodel.CustomerViewModel

@Composable
fun SearchScreen(
    customerViewModel: CustomerViewModel,
    cartViewModel: CartViewModel,
    onNavigateToProduct: (Int) -> Unit,
    onBack: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    val products by customerViewModel.products.collectAsState()
    val isLoading by customerViewModel.isLoading.collectAsState()

    LaunchedEffect(query) {
        customerViewModel.filterProducts(search = if (query.isNotBlank()) query else null)
    }

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

            AAATextField(
                value = query,
                onValueChange = { query = it },
                label = "Search Catalog",
                placeholder = "Type crop, vegetable, flowers...",
                leadingIcon = Icons.Default.Search,
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EmeraldPrimary)
            }
        } else if (products.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = if (query.isEmpty()) "Start typing to search products" else "No products found for '$query'",
                    color = TextSecondary,
                    fontSize = 14.sp
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 8.dp)
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
