package com.aaagrowers.app.data.model

import com.google.gson.annotations.SerializedName

// Generic API Envelope
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val errors: List<String>? = null
)

data class SimpleResponse(
    val success: Boolean,
    val message: String
)

data class PaginatedData<T>(
    val items: List<T>,
    val pagination: PaginationMeta
)

data class PaginationMeta(
    val total: Int,
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_pages") val totalPages: Int,
    @SerializedName("has_next") val hasNext: Boolean,
    @SerializedName("has_prev") val hasPrev: Boolean
)

// Auth Models
data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterCustomerRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val phone: String?,
    val password: String,
    val role: String = "CUSTOMER",
    val city: String? = null,
    val address: String? = null
)

data class RegisterFarmerRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val phone: String?,
    val password: String,
    val role: String = "FARMER",
    @SerializedName("farm_name") val farmName: String,
    @SerializedName("farm_location") val farmLocation: String,
    @SerializedName("farm_size_acres") val farmSizeAcres: Double,
    @SerializedName("crops_grown") val cropsGrown: String?,
    @SerializedName("farming_experience_years") val farmingExperienceYears: Int,
    @SerializedName("national_id") val nationalId: String?
)

data class RegisterUserRequest(
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    val email: String,
    val phone: String?,
    val password: String,
    val role: String,
    val city: String? = null,
    val address: String? = null,
    @SerializedName("farm_name") val farmName: String? = null,
    @SerializedName("farm_location") val farmLocation: String? = null,
    @SerializedName("farm_size_acres") val farmSizeAcres: Double? = null,
    @SerializedName("crops_grown") val cropsGrown: String? = null,
    @SerializedName("farming_experience_years") val farmingExperienceYears: Int? = null,
    @SerializedName("national_id") val nationalId: String? = null,
    @SerializedName("license_number") val licenseNumber: String? = null,
    @SerializedName("vehicle_registration") val vehicleRegistration: String? = null,
    @SerializedName("vehicle_type") val vehicleType: String? = null,
    @SerializedName("company_name") val companyName: String? = null,
    @SerializedName("supply_category") val supplyCategory: String? = null
)

data class UpdateProfileRequest(
    @SerializedName("first_name") val firstName: String? = null,
    @SerializedName("last_name") val lastName: String? = null,
    val phone: String? = null,
    val city: String? = null,
    val address: String? = null
)

data class AuthData(
    val token: String,
    val user: User
)

data class User(
    val id: Int,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String,
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val phone: String?,
    val role: String,
    val status: String,
    val city: String?,
    val address: String?,
    @SerializedName("avatar_url") val avatarUrl: String?,
    @SerializedName("farmer_profile") val farmerProfile: FarmerProfile?
)

data class FarmerProfile(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("farm_name") val farmName: String,
    @SerializedName("farm_location") val farmLocation: String,
    @SerializedName("farm_size_acres") val farmSizeAcres: Double,
    @SerializedName("crops_grown") val cropsGrown: String?,
    @SerializedName("farming_experience_years") val farmingExperienceYears: Int,
    @SerializedName("national_id") val nationalId: String?,
    @SerializedName("total_trainings_attended") val totalTrainingsAttended: Int = 0,
    @SerializedName("certifications_count") val certificationsCount: Int = 0
)

// Product & Category
data class Category(
    val id: Int,
    val name: String,
    val slug: String,
    val description: String?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("product_count") val productCount: Int
)

data class Product(
    val id: Int,
    @SerializedName("category_id") val categoryId: Int?,
    @SerializedName("category_name") val categoryName: String?,
    val name: String,
    val sku: String,
    val description: String?,
    val unit: String,
    val price: Double,
    @SerializedName("cost_price") val costPrice: Double?,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_active") val isActive: Boolean,
    @SerializedName("is_featured") val isFeatured: Boolean,
    @SerializedName("current_stock") val currentStock: Int,
    @SerializedName("is_in_stock") val isInStock: Boolean,
    @SerializedName("is_low_stock") val isLowStock: Boolean
)

// Cart & Order
data class CartItem(
    val product: Product,
    var quantity: Int
) {
    val subtotal: Double get() = product.price * quantity
}

data class CreateOrderItemRequest(
    @SerializedName("product_id") val productId: Int,
    val quantity: Int
)

data class CreateOrderRequest(
    val items: List<CreateOrderItemRequest>,
    @SerializedName("delivery_address") val deliveryAddress: String,
    @SerializedName("delivery_city") val deliveryCity: String,
    @SerializedName("delivery_phone") val deliveryPhone: String,
    val notes: String? = null
)

data class UpdateOrderStatusRequest(
    val status: String,
    val notes: String? = null
)

data class Order(
    val id: Int = 0,
    @SerializedName("order_number") val orderNumber: String = "",
    @SerializedName("customer_id") val customerId: Int = 0,
    @SerializedName("customer_name") val customerName: String? = "",
    @SerializedName("total_amount") val totalAmount: Double = 0.0,
    @SerializedName("discount_amount") val discountAmount: Double = 0.0,
    @SerializedName("shipping_fee") val shippingFee: Double = 0.0,
    @SerializedName("net_amount") val netAmount: Double = 0.0,
    val status: String = "PENDING",
    @SerializedName("payment_status") val paymentStatus: String = "PENDING",
    @SerializedName("delivery_address") val deliveryAddress: String? = "",
    @SerializedName("delivery_city") val deliveryCity: String? = "",
    @SerializedName("delivery_phone") val deliveryPhone: String? = "",
    val notes: String? = null,
    val items: List<OrderItem>? = null,
    val dispatch: DispatchInfo? = null,
    @SerializedName("placed_at") val placedAt: String? = null
)

data class OrderItem(
    val id: Int = 0,
    @SerializedName("product_id") val productId: Int = 0,
    @SerializedName("product_name") val productName: String = "",
    @SerializedName("product_image") val productImage: String? = null,
    @SerializedName("product_sku") val productSku: String? = null,
    @SerializedName("product_unit") val productUnit: String? = null,
    @SerializedName("unit_price") val unitPrice: Double = 0.0,
    val quantity: Int = 1,
    val subtotal: Double = 0.0
)

// Dispatch & Fleet
data class DispatchInfo(
    val id: Int = 0,
    @SerializedName("dispatch_number") val dispatchNumber: String = "",
    @SerializedName("order_id") val orderId: Int = 0,
    @SerializedName("order_number") val orderNumber: String? = null,
    @SerializedName("driver_id") val driverId: Int? = null,
    @SerializedName("driver_name") val driverName: String? = null,
    @SerializedName("vehicle_reg") val vehicleReg: String? = null,
    @SerializedName("delivery_address") val deliveryAddress: String? = null,
    val status: String = "PENDING",
    @SerializedName("tracking_notes") val trackingNotes: String? = null
)

data class UpdateDispatchStatusRequest(
    val status: String,
    val notes: String? = null
)

data class DriverInfo(
    val id: Int,
    val name: String,
    val phone: String?,
    @SerializedName("license_number") val licenseNumber: String,
    @SerializedName("vehicle_registration") val vehicleRegistration: String,
    @SerializedName("vehicle_type") val vehicleType: String,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("current_location") val currentLocation: String?,
    @SerializedName("total_deliveries") val totalDeliveries: Int = 0
)

// Inventory & Warehouse
data class InventoryItem(
    val id: Int,
    @SerializedName("product_id") val productId: Int,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_sku") val productSku: String?,
    @SerializedName("current_stock") val currentStock: Int,
    @SerializedName("low_stock_threshold") val lowStockThreshold: Int,
    @SerializedName("is_low_stock") val isLowStock: Boolean,
    @SerializedName("is_out_of_stock") val isOutOfStock: Boolean
)

data class AdjustStockRequest(
    @SerializedName("change_quantity") val changeQuantity: Int,
    @SerializedName("movement_type") val movementType: String,
    val notes: String? = null,
    @SerializedName("reference_id") val referenceId: String? = null
)

// Supplier
data class SupplierInfo(
    val id: Int,
    val name: String,
    @SerializedName("contact_person") val contactPerson: String,
    val email: String,
    val phone: String,
    val address: String,
    @SerializedName("supply_category") val supplyCategory: String,
    val rating: Double
)

// Payment
data class ProcessPaymentRequest(
    @SerializedName("order_id") val orderId: Int,
    val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("simulate_status") val simulateStatus: String = "SUCCESS"
)

data class Payment(
    val id: Int,
    @SerializedName("order_id") val orderId: Int,
    @SerializedName("order_number") val orderNumber: String?,
    val amount: Double,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("transaction_reference") val transactionReference: String,
    val status: String,
    @SerializedName("paid_at") val paidAt: String?
)

// Training & Certifications
data class BookTrainingRequest(
    val notes: String? = null
)

data class TrainingSession(
    val id: Int,
    @SerializedName("trainer_name") val trainerName: String,
    val title: String,
    val description: String,
    val category: String,
    @SerializedName("training_date") val trainingDate: String,
    @SerializedName("start_time") val startTime: String,
    @SerializedName("end_time") val endTime: String,
    val location: String,
    val capacity: Int,
    @SerializedName("booked_count") val bookedCount: Int,
    @SerializedName("available_slots") val availableSlots: Int,
    @SerializedName("is_full") val isFull: Boolean,
    val status: String
)

data class TrainingBooking(
    val id: Int,
    @SerializedName("training_id") val trainingId: Int,
    @SerializedName("training_title") val trainingTitle: String?,
    @SerializedName("training_date") val trainingDate: String?,
    @SerializedName("training_location") val trainingLocation: String?,
    @SerializedName("farmer_id") val farmerId: Int,
    @SerializedName("farmer_name") val farmerName: String?,
    val status: String,
    @SerializedName("booking_date") val bookingDate: String?
)

data class Certification(
    val id: Int,
    @SerializedName("certificate_number") val certificateNumber: String,
    @SerializedName("farmer_id") val farmerId: Int,
    @SerializedName("farmer_name") val farmerName: String,
    @SerializedName("farm_name") val farmName: String?,
    @SerializedName("training_id") val trainingId: Int,
    @SerializedName("training_title") val trainingTitle: String?,
    @SerializedName("trainer_name") val trainerName: String?,
    @SerializedName("issue_date") val issueDate: String,
    @SerializedName("expiry_date") val expiryDate: String?,
    val title: String,
    @SerializedName("verification_hash") val verificationHash: String,
    val status: String
)

// Feedback & Contact
data class SubmitFeedbackRequest(
    val rating: Int,
    val category: String = "General",
    val comment: String
)

data class SubmitContactRequest(
    @SerializedName("full_name") val fullName: String,
    val email: String,
    val phone: String?,
    val subject: String,
    val message: String
)
