package com.aaagrowers.app.data.api

import com.aaagrowers.app.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // Auth
    @POST("auth/register")
    suspend fun registerCustomer(@Body request: RegisterCustomerRequest): Response<ApiResponse<AuthData>>

    @POST("auth/register")
    suspend fun registerFarmer(@Body request: RegisterFarmerRequest): Response<ApiResponse<AuthData>>

    @POST("auth/register")
    suspend fun registerUser(@Body request: RegisterUserRequest): Response<ApiResponse<AuthData>>

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthData>>

    @GET("auth/me")
    suspend fun getCurrentUser(): Response<ApiResponse<User>>

    @PUT("auth/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<User>>

    // Products & Categories
    @GET("products")
    suspend fun getProducts(
        @Query("category_id") categoryId: Int? = null,
        @Query("search") search: String? = null,
        @Query("is_featured") isFeatured: Boolean? = null,
        @Query("in_stock_only") inStockOnly: Boolean = false,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<ApiResponse<PaginatedData<Product>>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") productId: Int): Response<ApiResponse<Product>>

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    // Orders
    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<Order>>

    @GET("orders")
    suspend fun getOrders(
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<ApiResponse<PaginatedData<Order>>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") orderId: Int): Response<ApiResponse<Order>>

    @PUT("orders/{id}/status")
    suspend fun updateOrderStatus(
        @Path("id") orderId: Int,
        @Body request: UpdateOrderStatusRequest
    ): Response<ApiResponse<Order>>

    @POST("orders/{id}/cancel")
    suspend fun cancelOrder(@Path("id") orderId: Int): Response<ApiResponse<Order>>

    // Payments
    @POST("payments")
    suspend fun processPayment(@Body request: ProcessPaymentRequest): Response<ApiResponse<Payment>>

    // Logistics & Dispatches (Fleet / Couriers)
    @GET("dispatches")
    suspend fun getDispatches(
        @Query("driver_id") driverId: Int? = null,
        @Query("status") status: String? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 30
    ): Response<ApiResponse<PaginatedData<DispatchInfo>>>

    @PUT("dispatches/{id}/status")
    suspend fun updateDispatchStatus(
        @Path("id") dispatchId: Int,
        @Body request: UpdateDispatchStatusRequest
    ): Response<ApiResponse<DispatchInfo>>

    @GET("drivers")
    suspend fun getDrivers(): Response<ApiResponse<List<DriverInfo>>>

    // Warehouse & Inventory Management
    @GET("inventory")
    suspend fun getInventory(
        @Query("low_stock_only") lowStockOnly: Boolean = false,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 50
    ): Response<ApiResponse<PaginatedData<InventoryItem>>>

    @PUT("inventory/{productId}")
    suspend fun adjustStock(
        @Path("productId") productId: Int,
        @Body request: AdjustStockRequest
    ): Response<ApiResponse<InventoryItem>>

    // Suppliers Directory
    @GET("suppliers")
    suspend fun getSuppliers(): Response<ApiResponse<List<SupplierInfo>>>

    // Training (Farmer)
    @GET("trainings")
    suspend fun getTrainings(
        @Query("upcoming_only") upcomingOnly: Boolean = true,
        @Query("category") category: String? = null
    ): Response<ApiResponse<PaginatedData<TrainingSession>>>

    @GET("trainings/{id}")
    suspend fun getTrainingById(@Path("id") sessionId: Int): Response<ApiResponse<TrainingSession>>

    @POST("trainings/{id}/book")
    suspend fun bookTraining(
        @Path("id") sessionId: Int,
        @Body request: BookTrainingRequest
    ): Response<ApiResponse<TrainingBooking>>

    @GET("trainings/my-bookings")
    suspend fun getMyBookings(): Response<ApiResponse<List<TrainingBooking>>>

    @POST("trainings/bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") bookingId: Int): Response<ApiResponse<TrainingBooking>>

    // Certifications
    @GET("certifications")
    suspend fun getMyCertifications(): Response<ApiResponse<PaginatedData<Certification>>>

    @GET("certifications/{id}")
    suspend fun getCertificateById(@Path("id") certId: Int): Response<ApiResponse<Certification>>

    // Feedback & Contact
    @POST("feedback")
    suspend fun submitFeedback(@Body request: SubmitFeedbackRequest): Response<SimpleResponse>

    @POST("contacts")
    suspend fun submitContact(@Body request: SubmitContactRequest): Response<SimpleResponse>
}
