package com.aaagrowers.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaagrowers.app.data.local.CartManager
import com.aaagrowers.app.data.model.*
import com.aaagrowers.app.data.repository.OrderRepository
import com.aaagrowers.app.data.repository.PaymentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(
    private val orderRepo: OrderRepository,
    private val paymentRepo: PaymentRepository
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _currentOrder = MutableStateFlow<Order?>(null)
    val currentOrder: StateFlow<Order?> = _currentOrder.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun placeOrder(
        deliveryAddress: String,
        deliveryCity: String,
        deliveryPhone: String,
        paymentMethod: String,
        notes: String? = null,
        onSuccess: (Int) -> Unit
    ) {
        val cartItems = CartManager.cartItems.value
        if (cartItems.isEmpty()) {
            _errorMessage.value = "Your cart is empty"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val itemsReq = cartItems.map { CreateOrderItemRequest(it.product.id, it.quantity) }
                val orderReq = CreateOrderRequest(
                    items = itemsReq,
                    deliveryAddress = deliveryAddress,
                    deliveryCity = deliveryCity,
                    deliveryPhone = deliveryPhone,
                    notes = notes
                )

                val resp = orderRepo.createOrder(orderReq)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    val createdOrder = resp.body()!!.data!!
                    _currentOrder.value = createdOrder

                    // Process Mock Payment
                    val payResp = paymentRepo.processPayment(
                        ProcessPaymentRequest(
                            orderId = createdOrder.id,
                            amount = createdOrder.netAmount,
                            paymentMethod = paymentMethod,
                            simulateStatus = "SUCCESS"
                        )
                    )

                    // Clear Cart upon successful order placement
                    CartManager.clearCart()
                    onSuccess(createdOrder.id)
                } else {
                    _errorMessage.value = resp.body()?.message ?: "Order placement failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Network error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadOrderHistory() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = orderRepo.getOrders()
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _orders.value = resp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadOrderDetail(orderId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = orderRepo.getOrderById(orderId)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _currentOrder.value = resp.body()!!.data
                }
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun cancelOrder(orderId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                orderRepo.cancelOrder(orderId)
                loadOrderDetail(orderId)
                loadOrderHistory()
                onComplete()
            } catch (e: Exception) {
                // handle
            }
        }
    }
}
