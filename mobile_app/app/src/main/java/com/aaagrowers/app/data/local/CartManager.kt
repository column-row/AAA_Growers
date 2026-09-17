package com.aaagrowers.app.data.local

import com.aaagrowers.app.data.model.CartItem
import com.aaagrowers.app.data.model.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object CartManager {
    private val _cartItems = MutableStateFlow<List<CartItem>>(emptyList())
    val cartItems: StateFlow<List<CartItem>> = _cartItems.asStateFlow()

    fun addToCart(product: Product, quantity: Int = 1) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == product.id }
        if (index != -1) {
            val existing = current[index]
            val newQty = existing.quantity + quantity
            // Cap at available stock
            val cappedQty = newQty.coerceAtMost(product.currentStock)
            current[index] = existing.copy(quantity = cappedQty)
        } else {
            val initialQty = quantity.coerceAtMost(product.currentStock).coerceAtLeast(1)
            current.add(CartItem(product, initialQty))
        }
        _cartItems.value = current
    }

    fun updateQuantity(productId: Int, newQuantity: Int) {
        val current = _cartItems.value.toMutableList()
        val index = current.indexOfFirst { it.product.id == productId }
        if (index != -1) {
            if (newQuantity <= 0) {
                current.removeAt(index)
            } else {
                val stock = current[index].product.currentStock
                current[index] = current[index].copy(quantity = newQuantity.coerceAtMost(stock))
            }
            _cartItems.value = current
        }
    }

    fun removeFromCart(productId: Int) {
        val current = _cartItems.value.toMutableList()
        current.removeAll { it.product.id == productId }
        _cartItems.value = current
    }

    fun clearCart() {
        _cartItems.value = emptyList()
    }

    fun getSubtotal(): Double {
        return _cartItems.value.sumOf { it.subtotal }
    }

    fun getShippingFee(): Double {
        val subtotal = getSubtotal()
        return if (subtotal > 0 && subtotal < 3000) 300.0 else 0.0
    }

    fun getNetTotal(): Double {
        return getSubtotal() + getShippingFee()
    }

    fun getItemCount(): Int {
        return _cartItems.value.sumOf { it.quantity }
    }
}
