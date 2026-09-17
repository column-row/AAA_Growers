package com.aaagrowers.app.data.repository

import com.aaagrowers.app.data.api.ApiService
import com.aaagrowers.app.data.model.*

class AuthRepository(private val api: ApiService) {
    suspend fun registerCustomer(request: RegisterCustomerRequest) = api.registerCustomer(request)
    suspend fun registerFarmer(request: RegisterFarmerRequest) = api.registerFarmer(request)
    suspend fun registerUser(request: RegisterUserRequest) = api.registerUser(request)
    suspend fun login(request: LoginRequest) = api.login(request)
    suspend fun getCurrentUser() = api.getCurrentUser()
    suspend fun updateProfile(request: UpdateProfileRequest) = api.updateProfile(request)
}

class ProductRepository(private val api: ApiService) {
    suspend fun getProducts(categoryId: Int? = null, search: String? = null, isFeatured: Boolean? = null, inStockOnly: Boolean = false) =
        api.getProducts(categoryId, search, isFeatured, inStockOnly)

    suspend fun getProductById(productId: Int) = api.getProductById(productId)
    suspend fun getCategories() = api.getCategories()
}

class OrderRepository(private val api: ApiService) {
    suspend fun createOrder(request: CreateOrderRequest) = api.createOrder(request)
    suspend fun getOrders(status: String? = null) = api.getOrders(status)
    suspend fun getOrderById(orderId: Int) = api.getOrderById(orderId)
    suspend fun updateOrderStatus(orderId: Int, status: String, notes: String? = null) =
        api.updateOrderStatus(orderId, UpdateOrderStatusRequest(status, notes))
    suspend fun cancelOrder(orderId: Int) = api.cancelOrder(orderId)
}

class PaymentRepository(private val api: ApiService) {
    suspend fun processPayment(request: ProcessPaymentRequest) = api.processPayment(request)
}

class TrainingRepository(private val api: ApiService) {
    suspend fun getTrainings(upcomingOnly: Boolean = true, category: String? = null) =
        api.getTrainings(upcomingOnly, category)

    suspend fun getTrainingById(sessionId: Int) = api.getTrainingById(sessionId)
    suspend fun bookTraining(sessionId: Int, notes: String? = null) =
        api.bookTraining(sessionId, BookTrainingRequest(notes))

    suspend fun getMyBookings() = api.getMyBookings()
    suspend fun cancelBooking(bookingId: Int) = api.cancelBooking(bookingId)
    suspend fun getMyCertifications() = api.getMyCertifications()
    suspend fun getCertificateById(certId: Int) = api.getCertificateById(certId)
}

class StaffRepository(private val api: ApiService) {
    suspend fun getDispatches(driverId: Int? = null, status: String? = null) =
        api.getDispatches(driverId, status)

    suspend fun updateDispatchStatus(dispatchId: Int, status: String, notes: String? = null) =
        api.updateDispatchStatus(dispatchId, UpdateDispatchStatusRequest(status, notes))

    suspend fun getDrivers() = api.getDrivers()

    suspend fun getInventory(lowStockOnly: Boolean = false) =
        api.getInventory(lowStockOnly)

    suspend fun adjustStock(productId: Int, changeQuantity: Int, movementType: String, notes: String? = null, referenceId: String? = null) =
        api.adjustStock(productId, AdjustStockRequest(changeQuantity, movementType, notes, referenceId))

    suspend fun getSuppliers() = api.getSuppliers()
}

class CommonRepository(private val api: ApiService) {
    suspend fun submitFeedback(request: SubmitFeedbackRequest) = api.submitFeedback(request)
    suspend fun submitContact(request: SubmitContactRequest) = api.submitContact(request)
}
