package com.aaagrowers.app.ui.navigation

sealed class Screen(val route: String) {
    // Auth
    object Login : Screen("login")
    object CustomerRegister : Screen("customer_register")
    object FarmerRegister : Screen("farmer_register")
    object UnifiedRegister : Screen("register?role={role}") {
        fun createRoute(role: String = "CUSTOMER") = "register?role=$role"
    }

    // Customer Hub
    object CustomerMain : Screen("customer_main")
    object CustomerHome : Screen("customer_home")
    object ProductList : Screen("product_list?category_id={category_id}") {
        fun createRoute(categoryId: Int? = null) = if (categoryId != null) "product_list?category_id=$categoryId" else "product_list"
    }
    object ProductDetail : Screen("product_detail/{productId}") {
        fun createRoute(productId: Int) = "product_detail/$productId"
    }
    object Search : Screen("search")
    object Cart : Screen("cart")
    object Checkout : Screen("checkout")
    object OrderConfirmation : Screen("order_confirmation/{orderId}") {
        fun createRoute(orderId: Int) = "order_confirmation/$orderId"
    }
    object OrderHistory : Screen("order_history")
    object OrderTracking : Screen("order_tracking/{orderId}") {
        fun createRoute(orderId: Int) = "order_tracking/$orderId"
    }
    object CustomerProfile : Screen("customer_profile")
    object Help : Screen("help")
    object About : Screen("about")
    object Feedback : Screen("feedback")
    object Contact : Screen("contact")

    // Farmer Hub
    object FarmerMain : Screen("farmer_main")
    object FarmerHome : Screen("farmer_home")
    object TrainingList : Screen("training_list")
    object TrainingDetail : Screen("training_detail/{sessionId}") {
        fun createRoute(sessionId: Int) = "training_detail/$sessionId"
    }
    object FarmerBookings : Screen("farmer_bookings")
    object Certificates : Screen("certificates")
    object CertificateViewer : Screen("certificate_viewer/{certId}") {
        fun createRoute(certId: Int) = "certificate_viewer/$certId"
    }
    object FarmerProfile : Screen("farmer_profile")

    // Staff & Operations Hub (Logistics, Warehouse, Inventory, Fulfillment, Suppliers)
    object StaffMain : Screen("staff_main")
}
