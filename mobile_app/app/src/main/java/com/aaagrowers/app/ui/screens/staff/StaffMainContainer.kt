package com.aaagrowers.app.ui.screens.staff

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
import androidx.navigation.compose.*
import com.aaagrowers.app.ui.navigation.Screen
import com.aaagrowers.app.ui.screens.customer.CustomerProfileScreen
import com.aaagrowers.app.ui.theme.EmeraldPrimary
import com.aaagrowers.app.ui.viewmodel.AuthViewModel
import com.aaagrowers.app.ui.viewmodel.StaffViewModel

data class StaffNavItem(val route: String, val label: String, val icon: ImageVector)

@Composable
fun StaffMainContainer(
    rootNavController: NavHostController,
    authViewModel: AuthViewModel,
    staffViewModel: StaffViewModel
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val navItems = listOf(
        StaffNavItem("staff_logistics", "Logistics", Icons.Default.LocalShipping),
        StaffNavItem("staff_inventory", "Inventory", Icons.Default.Inventory2),
        StaffNavItem("staff_orders", "Orders", Icons.Default.ReceiptLong),
        StaffNavItem("staff_suppliers", "Suppliers", Icons.Default.Business),
        StaffNavItem("staff_profile", "Profile", Icons.Default.Person)
    )

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                navItems.forEach { item ->
                    val selected = currentRoute == item.route
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label, fontSize = 11.sp) },
                        selected = selected,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EmeraldPrimary,
                            selectedTextColor = EmeraldPrimary
                        ),
                        onClick = {
                            bottomNavController.navigate(item.route) {
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
            startDestination = "staff_logistics",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("staff_logistics") {
                StaffLogisticsScreen(staffViewModel = staffViewModel)
            }

            composable("staff_inventory") {
                StaffInventoryScreen(staffViewModel = staffViewModel)
            }

            composable("staff_orders") {
                StaffOrdersScreen(staffViewModel = staffViewModel)
            }

            composable("staff_suppliers") {
                StaffSuppliersScreen(staffViewModel = staffViewModel)
            }

            composable("staff_profile") {
                CustomerProfileScreen(
                    authViewModel = authViewModel,
                    onNavigateToOrders = { bottomNavController.navigate("staff_orders") },
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
