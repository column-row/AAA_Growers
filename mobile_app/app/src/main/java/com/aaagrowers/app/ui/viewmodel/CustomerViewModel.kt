package com.aaagrowers.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaagrowers.app.data.model.*
import com.aaagrowers.app.data.repository.CommonRepository
import com.aaagrowers.app.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CustomerViewModel(
    private val productRepo: ProductRepository,
    private val commonRepo: CommonRepository
) : ViewModel() {

    private val _featuredProducts = MutableStateFlow<List<Product>>(emptyList())
    val featuredProducts: StateFlow<List<Product>> = _featuredProducts.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadHomeData()
    }

    fun loadHomeData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val catResp = productRepo.getCategories()
                if (catResp.isSuccessful && catResp.body()?.success == true) {
                    _categories.value = catResp.body()!!.data ?: emptyList()
                }

                val featResp = productRepo.getProducts(isFeatured = true)
                if (featResp.isSuccessful && featResp.body()?.success == true) {
                    _featuredProducts.value = featResp.body()!!.data?.items ?: emptyList()
                }

                val allResp = productRepo.getProducts()
                if (allResp.isSuccessful && allResp.body()?.success == true) {
                    _products.value = allResp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun filterProducts(categoryId: Int? = null, search: String? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = productRepo.getProducts(categoryId = categoryId, search = search)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _products.value = resp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadProductDetail(productId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = productRepo.getProductById(productId)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _selectedProduct.value = resp.body()!!.data
                }
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun submitFeedback(rating: Int, category: String, comment: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = commonRepo.submitFeedback(SubmitFeedbackRequest(rating, category, comment))
                if (resp.isSuccessful && resp.body()?.success == true) {
                    onComplete(true, "Feedback submitted successfully!")
                } else {
                    onComplete(false, resp.body()?.message ?: "Failed to submit feedback")
                }
            } catch (e: Exception) {
                onComplete(false, "Connection error")
            }
        }
    }

    fun submitContact(fullName: String, email: String, phone: String?, subject: String, message: String, onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = commonRepo.submitContact(SubmitContactRequest(fullName, email, phone, subject, message))
                if (resp.isSuccessful && resp.body()?.success == true) {
                    onComplete(true, "Inquiry sent to support team!")
                } else {
                    onComplete(false, resp.body()?.message ?: "Failed to send message")
                }
            } catch (e: Exception) {
                onComplete(false, "Connection error")
            }
        }
    }
}
