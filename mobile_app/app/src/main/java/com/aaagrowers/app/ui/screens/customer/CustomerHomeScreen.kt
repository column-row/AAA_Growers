package com.aaagrowers.app.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aaagrowers.app.ui.components.ProductCard
import com.aaagrowers.app.ui.theme.*
import com.aaagrowers.app.ui.viewmodel.CartViewModel
import com.aaagrowers.app.ui.viewmodel.CustomerViewModel

@Composable
fun CustomerHomeScreen(
    customerViewModel: CustomerViewModel,
    cartViewModel: CartViewModel,
    onNavigateToSearch: () -> Unit,
    onNavigateToCategory: (Int) -> Unit,
    onNavigateToProduct: (Int) -> Unit,
    onNavigateToCart: () -> Unit
) {
    val featuredProducts by customerViewModel.featuredProducts.collectAsState()
    val allProducts by customerViewModel.products.collectAsState()
    val categories by customerViewModel.categories.collectAsState()
    val isLoading by customerViewModel.isLoading.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceBg)
            .verticalScroll(rememberScrollState())
            .padding(bottom = 80.dp)
    ) {
        // Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Welcome to",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "AAA GROWERS",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldDark
                )
            }

            IconButton(
                onClick = onNavigateToCart,
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(EmeraldLight)
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = EmeraldDark)
            }
        }

        // Search Bar Clickable Banner
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .clickable { onNavigateToSearch() },
            shape = RoundedCornerShape(16.dp),
            color = CardBg,
            shadowElevation = 1.dp
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted)
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Search fresh herbs, French beans, roses...",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }
        }

        // Hero Promo Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = EmeraldDark)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Surface(
                        color = Color(0xFF22C55E).copy(alpha = 0.25f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "FARM TO FORK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldLight,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Text(
                        text = "GlobalG.A.P. Certified Produce",
                        fontWeight = FontWeight.Black,
                        fontSize = 18.sp,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Same-day cold chain delivery across Kenya",
                        fontSize = 12.sp,
                        color = EmeraldLight
                    )
                }
                Icon(
                    Icons.Default.Spa,
                    contentDescription = null,
                    tint = EmeraldLight,
                    modifier = Modifier.size(56.dp)
                )
            }
        }

        // Categories Horizontal Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Categories",
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = TextPrimary
            )
        }

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(categories) { cat ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .width(72.dp)
                        .clickable { onNavigateToCategory(cat.id) }
                ) {
                    Box(
                        modifier = Modifier
                            .size(62.dp)
                            .clip(CircleShape)
                            .background(EmeraldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = cat.imageUrl ?: "https://images.unsplash.com/photo-1540420773420-3366772f4999?w=200",
                            contentDescription = cat.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = cat.name,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Featured Products
        Text(
            text = "Featured Harvest",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(if (featuredProducts.isNotEmpty()) featuredProducts else allProducts.take(4)) { prod ->
                ProductCard(
                    product = prod,
                    onClick = { onNavigateToProduct(prod.id) },
                    onAddToCart = { cartViewModel.addToCart(prod, 1) },
                    modifier = Modifier.width(170.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // All Products Grid View
        Text(
            text = "All Fresh Produce",
            fontWeight = FontWeight.Bold,
            fontSize = 17.sp,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
        )

        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            val chunked = allProducts.chunked(2)
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
