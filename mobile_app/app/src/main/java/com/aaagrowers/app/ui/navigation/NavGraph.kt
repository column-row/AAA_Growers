package com.aaagrowers.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.aaagrowers.app.ui.screens.auth.*
import com.aaagrowers.app.ui.screens.customer.AboutScreen
import com.aaagrowers.app.ui.screens.customer.CartScreen
import com.aaagrowers.app.ui.screens.customer.CheckoutScreen
import com.aaagrowers.app.ui.screens.customer.ContactScreen
import com.aaagrowers.app.ui.screens.customer.CustomerHomeScreen
import com.aaagrowers.app.ui.screens.customer.CustomerProfileScreen
import com.aaagrowers.app.ui.screens.customer.FeedbackScreen
import com.aaagrowers.app.ui.screens.customer.HelpScreen
import com.aaagrowers.app.ui.screens.customer.OrderConfirmationScreen
import com.aaagrowers.app.ui.screens.customer.OrderHistoryScreen
import com.aaagrowers.app.ui.screens.customer.OrderTrackingScreen
import com.aaagrowers.app.ui.screens.customer.ProductDetailScreen
import com.aaagrowers.app.ui.screens.customer.ProductListScreen
import com.aaagrowers.app.ui.screens.customer.SearchScreen
import com.aaagrowers.app.ui.screens.farmer.*
import com.aaagrowers.app.ui.screens.staff.*
import com.aaagrowers.app.ui.theme.EmeraldPrimary
import com.aaagrowers.app.ui.viewmodel.*

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel,
    customerViewModel: CustomerViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel,
    farmerViewModel: FarmerViewModel,
    staffViewModel: StaffViewModel,
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth Routes
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onLoginSuccess = { role ->
                    when (role) {
                        "FARMER" -> {
                            navController.navigate(Screen.FarmerMain.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        "DRIVER", "DISPATCH_MANAGER", "INVENTORY_MANAGER", "FINANCE_MANAGER", "SUPPLIER", "SERVICE_MANAGER", "ADMIN", "TRAINER" -> {
                            navController.navigate(Screen.StaffMain.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                        else -> {
                            navController.navigate(Screen.CustomerMain.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                            }
                        }
                    }
                },
                onNavigateToCustomerRegister = { navController.navigate(Screen.CustomerRegister.route) },
                onNavigateToFarmerRegister = { navController.navigate(Screen.FarmerRegister.route) }
            )
        }

        composable(Screen.CustomerRegister.route) {
            UnifiedRegisterScreen(
                viewModel = authViewModel,
                initialRole = "CUSTOMER",
                onRegisterSuccess = { role ->
                    when (role) {
                        "FARMER" -> navController.navigate(Screen.FarmerMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        "DRIVER", "INVENTORY_MANAGER", "DISPATCH_MANAGER", "SUPPLIER", "ADMIN", "FINANCE_MANAGER", "SERVICE_MANAGER", "TRAINER" -> navController.navigate(Screen.StaffMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        else -> navController.navigate(Screen.CustomerMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.FarmerRegister.route) {
            UnifiedRegisterScreen(
                viewModel = authViewModel,
                initialRole = "FARMER",
                onRegisterSuccess = { role ->
                    when (role) {
                        "FARMER" -> navController.navigate(Screen.FarmerMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        "DRIVER", "INVENTORY_MANAGER", "DISPATCH_MANAGER", "SUPPLIER", "ADMIN", "FINANCE_MANAGER", "SERVICE_MANAGER", "TRAINER" -> navController.navigate(Screen.StaffMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        else -> navController.navigate(Screen.CustomerMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.UnifiedRegister.route,
            arguments = listOf(navArgument("role") { type = NavType.StringType; defaultValue = "CUSTOMER" })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "CUSTOMER"
            UnifiedRegisterScreen(
                viewModel = authViewModel,
                initialRole = role,
                onRegisterSuccess = { userRole ->
                    when (userRole) {
                        "FARMER" -> navController.navigate(Screen.FarmerMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        "DRIVER", "INVENTORY_MANAGER", "DISPATCH_MANAGER", "SUPPLIER", "ADMIN", "FINANCE_MANAGER", "SERVICE_MANAGER", "TRAINER" -> navController.navigate(Screen.StaffMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                        else -> navController.navigate(Screen.CustomerMain.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                    }
                },
                onBackToLogin = { navController.popBackStack() }
            )
        }

        // Customer Main Container
        composable(Screen.CustomerMain.route) {
            CustomerMainContainer(
                rootNavController = navController,
                authViewModel = authViewModel,
                customerViewModel = customerViewModel,
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel
            )
        }

        // Customer Sub-routes
        composable(
            route = Screen.ProductList.route,
            arguments = listOf(navArgument("category_id") { type = NavType.StringType; nullable = true })
        ) { backStackEntry ->
            val catId = backStackEntry.arguments?.getString("category_id")?.toIntOrNull()
            ProductListScreen(
                initialCategoryId = catId,
                customerViewModel = customerViewModel,
                cartViewModel = cartViewModel,
                onNavigateToProduct = { navController.navigate(Screen.ProductDetail.createRoute(it)) },
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.IntType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getInt("productId") ?: 0
            ProductDetailScreen(
                productId = productId,
                customerViewModel = customerViewModel,
                cartViewModel = cartViewModel,
                onNavigateToCart = { navController.navigate(Screen.Cart.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Search.route) {
            SearchScreen(
                customerViewModel = customerViewModel,
                cartViewModel = cartViewModel,
                onNavigateToProduct = { navController.navigate(Screen.ProductDetail.createRoute(it)) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Cart.route) {
            CartScreen(
                cartViewModel = cartViewModel,
                onNavigateToCheckout = { navController.navigate(Screen.Checkout.route) },
                onNavigateToCatalog = { navController.navigate(Screen.ProductList.createRoute(null)) }
            )
        }

        composable(Screen.Checkout.route) {
            CheckoutScreen(
                authViewModel = authViewModel,
                cartViewModel = cartViewModel,
                orderViewModel = orderViewModel,
                onOrderPlaced = { orderId ->
                    navController.navigate(Screen.OrderConfirmation.createRoute(orderId)) {
                        popUpTo(Screen.CustomerMain.route)
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.OrderConfirmation.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderConfirmationScreen(
                orderId = orderId,
                orderViewModel = orderViewModel,
                onTrackOrder = { navController.navigate(Screen.OrderTracking.createRoute(it)) },
                onContinueShopping = {
                    navController.navigate(Screen.CustomerMain.route) {
                        popUpTo(Screen.CustomerMain.route) { inclusive = true }
                    }
                }
            )
        }

        composable(
            route = Screen.OrderTracking.route,
            arguments = listOf(navArgument("orderId") { type = NavType.IntType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getInt("orderId") ?: 0
            OrderTrackingScreen(
                orderId = orderId,
                orderViewModel = orderViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Help.route) { HelpScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.About.route) { AboutScreen(onBack = { navController.popBackStack() }) }
        composable(Screen.Feedback.route) { FeedbackScreen(customerViewModel = customerViewModel, onBack = { navController.popBackStack() }) }
        composable(Screen.Contact.route) { ContactScreen(customerViewModel = customerViewModel, onBack = { navController.popBackStack() }) }

        // Farmer Main Container
        composable(Screen.FarmerMain.route) {
            FarmerMainContainer(
                rootNavController = navController,
                authViewModel = authViewModel,
                farmerViewModel = farmerViewModel
            )
        }

        // Farmer Sub-routes
        composable(
            route = Screen.TrainingDetail.route,
            arguments = listOf(navArgument("sessionId") { type = NavType.IntType })
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getInt("sessionId") ?: 0
            TrainingDetailScreen(
                sessionId = sessionId,
                farmerViewModel = farmerViewModel,
                onNavigateToBookings = { navController.navigate(Screen.FarmerBookings.route) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.CertificateViewer.route,
            arguments = listOf(navArgument("certId") { type = NavType.IntType })
        ) { backStackEntry ->
            val certId = backStackEntry.arguments?.getInt("certId") ?: 0
            CertificateViewerScreen(
                certId = certId,
                farmerViewModel = farmerViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        // Staff & Logistics Operations Container
        composable(Screen.StaffMain.route) {
            StaffMainContainer(
                rootNavController = navController,
                authViewModel = authViewModel,
                staffViewModel = staffViewModel
            )
        }
    }
}

// Customer Bottom Navigation
data class CustomerNavItem(val screen: Screen, val label: String, val icon: ImageVector)

@Composable
fun CustomerMainContainer(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel,
    customerViewModel: CustomerViewModel,
    cartViewModel: CartViewModel,
    orderViewModel: OrderViewModel
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = listOf(
        CustomerNavItem(Screen.CustomerHome, "Home", Icons.Default.Home),
        CustomerNavItem(Screen.ProductList, "Catalog", Icons.Default.ShoppingBag),
        CustomerNavItem(Screen.Cart, "Cart", Icons.Default.ShoppingCart),
        CustomerNavItem(Screen.OrderHistory, "Orders", Icons.Default.ReceiptLong),
        CustomerNavItem(Screen.CustomerProfile, "Profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val selected = currentRoute == item.screen.route
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = EmeraldPrimary, selectedTextColor = EmeraldPrimary),
                        onClick = {
                            bottomNavController.navigate(item.screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.CustomerHome.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.CustomerHome.route) {
                CustomerHomeScreen(
                    customerViewModel = customerViewModel,
                    cartViewModel = cartViewModel,
                    onNavigateToSearch = { rootNavController.navigate(Screen.Search.route) },
                    onNavigateToCategory = { rootNavController.navigate(Screen.ProductList.createRoute(it)) },
                    onNavigateToProduct = { rootNavController.navigate(Screen.ProductDetail.createRoute(it)) },
                    onNavigateToCart = { rootNavController.navigate(Screen.Cart.route) }
                )
            }

            composable(Screen.ProductList.route) {
                ProductListScreen(
                    customerViewModel = customerViewModel,
                    cartViewModel = cartViewModel,
                    onNavigateToProduct = { rootNavController.navigate(Screen.ProductDetail.createRoute(it)) },
                    onNavigateToCart = { rootNavController.navigate(Screen.Cart.route) },
                    onBack = { bottomNavController.navigate(Screen.CustomerHome.route) }
                )
            }

            composable(Screen.Cart.route) {
                CartScreen(
                    cartViewModel = cartViewModel,
                    onNavigateToCheckout = { rootNavController.navigate(Screen.Checkout.route) },
                    onNavigateToCatalog = { bottomNavController.navigate(Screen.ProductList.route) }
                )
            }

            composable(Screen.OrderHistory.route) {
                OrderHistoryScreen(
                    orderViewModel = orderViewModel,
                    onNavigateToOrderTracking = { rootNavController.navigate(Screen.OrderTracking.createRoute(it)) },
                    onNavigateToCatalog = { bottomNavController.navigate(Screen.ProductList.route) }
                )
            }

            composable(Screen.CustomerProfile.route) {
                CustomerProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToOrders = { bottomNavController.navigate(Screen.OrderHistory.route) },
                    onNavigateToFeedback = { rootNavController.navigate(Screen.Feedback.route) },
                    onNavigateToHelp = { rootNavController.navigate(Screen.Help.route) },
                    onNavigateToAbout = { rootNavController.navigate(Screen.About.route) },
                    onNavigateToContact = { rootNavController.navigate(Screen.Contact.route) },
                    onLogout = {
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

// Farmer Bottom Navigation
data class FarmerNavItem(val screen: Screen, val label: String, val icon: ImageVector)

@Composable
fun FarmerMainContainer(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel,
    farmerViewModel: FarmerViewModel
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = listOf(
        FarmerNavItem(Screen.FarmerHome, "Hub", Icons.Default.Agriculture),
        FarmerNavItem(Screen.TrainingList, "Trainings", Icons.Default.School),
        FarmerNavItem(Screen.FarmerBookings, "Bookings", Icons.Default.Event),
        FarmerNavItem(Screen.Certificates, "Certs", Icons.Default.WorkspacePremium),
        FarmerNavItem(Screen.FarmerProfile, "Profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val selected = currentRoute == item.screen.route
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = EmeraldPrimary, selectedTextColor = EmeraldPrimary),
                        onClick = {
                            bottomNavController.navigate(item.screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = bottomNavController,
            startDestination = Screen.FarmerHome.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Screen.FarmerHome.route) {
                FarmerHomeScreen(
                    authViewModel = authViewModel,
                    farmerViewModel = farmerViewModel,
                    onNavigateToTrainings = { bottomNavController.navigate(Screen.TrainingList.route) },
                    onNavigateToTrainingDetail = { rootNavController.navigate(Screen.TrainingDetail.createRoute(it)) },
                    onNavigateToBookings = { bottomNavController.navigate(Screen.FarmerBookings.route) },
                    onNavigateToCertificates = { bottomNavController.navigate(Screen.Certificates.route) },
                    onNavigateToCertificateDetail = { rootNavController.navigate(Screen.CertificateViewer.createRoute(it)) }
                )
            }

            composable(Screen.TrainingList.route) {
                TrainingListScreen(
                    farmerViewModel = farmerViewModel,
                    onNavigateToDetail = { rootNavController.navigate(Screen.TrainingDetail.createRoute(it)) },
                    onBack = { bottomNavController.navigate(Screen.FarmerHome.route) }
                )
            }

            composable(Screen.FarmerBookings.route) {
                FarmerBookingsScreen(
                    farmerViewModel = farmerViewModel,
                    onNavigateToTrainings = { bottomNavController.navigate(Screen.TrainingList.route) }
                )
            }

            composable(Screen.Certificates.route) {
                FarmerBookingsScreen(
                    farmerViewModel = farmerViewModel,
                    onNavigateToTrainings = { bottomNavController.navigate(Screen.TrainingList.route) }
                )
            }

            composable(Screen.FarmerProfile.route) {
                FarmerProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToTrainings = { bottomNavController.navigate(Screen.TrainingList.route) },
                    onNavigateToBookings = { bottomNavController.navigate(Screen.FarmerBookings.route) },
                    onNavigateToCertificates = { bottomNavController.navigate(Screen.Certificates.route) },
                    onNavigateToAbout = { rootNavController.navigate(Screen.About.route) },
                    onNavigateToContact = { rootNavController.navigate(Screen.Contact.route) },
                    onLogout = {
                        rootNavController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}
