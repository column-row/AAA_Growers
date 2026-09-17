package com.aaagrowers.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.aaagrowers.app.data.local.CartManager
import com.aaagrowers.app.data.model.CartItem
import com.aaagrowers.app.data.model.Product
import kotlinx.coroutines.flow.StateFlow

class CartViewModel : ViewModel() {
    val cartItems: StateFlow<List<CartItem>> = CartManager.cartItems

    fun addToCart(product: Product, quantity: Int = 1) {
        CartManager.addToCart(product, quantity)
    }

    fun updateQuantity(productId: Int, quantity: Int) {
        CartManager.updateQuantity(productId, quantity)
    }

    fun removeFromCart(productId: Int) {
        CartManager.removeFromCart(productId)
    }

    fun clearCart() {
        CartManager.clearCart()
    }

    fun getSubtotal(): Double = CartManager.getSubtotal()
    fun getShippingFee(): Double = CartManager.getShippingFee()
    fun getNetTotal(): Double = CartManager.getNetTotal()
    fun getItemCount(): Int = CartManager.getItemCount()
}
