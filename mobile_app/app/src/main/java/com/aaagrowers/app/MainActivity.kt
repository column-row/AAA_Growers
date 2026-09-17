package com.aaagrowers.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.aaagrowers.app.data.api.ApiClient
import com.aaagrowers.app.data.local.SessionManager
import com.aaagrowers.app.data.repository.*
import com.aaagrowers.app.ui.navigation.AppNavGraph
import com.aaagrowers.app.ui.navigation.Screen
import com.aaagrowers.app.ui.theme.AAAGrowersTheme
import com.aaagrowers.app.ui.viewmodel.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val apiService = ApiClient.getService(applicationContext)
        val sessionManager = SessionManager(applicationContext)

        // Repositories
        val authRepo = AuthRepository(apiService)
        val productRepo = ProductRepository(apiService)
        val orderRepo = OrderRepository(apiService)
        val paymentRepo = PaymentRepository(apiService)
        val trainingRepo = TrainingRepository(apiService)
        val staffRepo = StaffRepository(apiService)
        val commonRepo = CommonRepository(apiService)

        // ViewModels
        val authViewModel = AuthViewModel(authRepo, sessionManager)
        val customerViewModel = CustomerViewModel(productRepo, commonRepo)
        val cartViewModel = CartViewModel()
        val orderViewModel = OrderViewModel(orderRepo, paymentRepo)
        val farmerViewModel = FarmerViewModel(trainingRepo)
        val staffViewModel = StaffViewModel(staffRepo, orderRepo)

        // Determine Start Destination based on active role
        val startDestination = if (sessionManager.isLoggedIn()) {
            when (sessionManager.getUserRole()) {
                "FARMER" -> Screen.FarmerMain.route
                "DRIVER", "DISPATCH_MANAGER", "INVENTORY_MANAGER", "FINANCE_MANAGER", "SUPPLIER", "SERVICE_MANAGER", "ADMIN", "TRAINER" -> Screen.StaffMain.route
                else -> Screen.CustomerMain.route
            }
        } else {
            Screen.Login.route
        }

        setContent {
            AAAGrowersTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    authViewModel = authViewModel,
                    customerViewModel = customerViewModel,
                    cartViewModel = cartViewModel,
                    orderViewModel = orderViewModel,
                    farmerViewModel = farmerViewModel,
                    staffViewModel = staffViewModel,
                    startDestination = startDestination
                )
            }
        }
    }
}
