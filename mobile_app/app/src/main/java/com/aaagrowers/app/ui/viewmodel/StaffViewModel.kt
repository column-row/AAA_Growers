package com.aaagrowers.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaagrowers.app.data.model.*
import com.aaagrowers.app.data.repository.OrderRepository
import com.aaagrowers.app.data.repository.StaffRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StaffViewModel(
    private val staffRepo: StaffRepository,
    private val orderRepo: OrderRepository
) : ViewModel() {

    private val _dispatches = MutableStateFlow<List<DispatchInfo>>(emptyList())
    val dispatches: StateFlow<List<DispatchInfo>> = _dispatches.asStateFlow()

    private val _drivers = MutableStateFlow<List<DriverInfo>>(emptyList())
    val drivers: StateFlow<List<DriverInfo>> = _drivers.asStateFlow()

    private val _inventory = MutableStateFlow<List<InventoryItem>>(emptyList())
    val inventory: StateFlow<List<InventoryItem>> = _inventory.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _suppliers = MutableStateFlow<List<SupplierInfo>>(emptyList())
    val suppliers: StateFlow<List<SupplierInfo>> = _suppliers.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    init {
        loadLogisticsData()
    }

    fun loadLogisticsData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadDispatches()
                loadInventory()
                loadOrders()
                loadDrivers()
                loadSuppliers()
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadDispatches(status: String? = null) {
        viewModelScope.launch {
            try {
                val resp = staffRepo.getDispatches(status = status)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _dispatches.value = resp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun updateDispatchStatus(dispatchId: Int, status: String, notes: String? = null, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = staffRepo.updateDispatchStatus(dispatchId, status, notes)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    loadDispatches()
                    onResult(true, "Dispatch status updated to $status")
                } else {
                    onResult(false, resp.body()?.message ?: "Failed to update dispatch")
                }
            } catch (e: Exception) {
                onResult(false, "Connection error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadInventory(lowStockOnly: Boolean = false) {
        viewModelScope.launch {
            try {
                val resp = staffRepo.getInventory(lowStockOnly = lowStockOnly)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _inventory.value = resp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun adjustStock(productId: Int, changeQuantity: Int, movementType: String, notes: String? = null, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = staffRepo.adjustStock(productId, changeQuantity, movementType, notes)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    loadInventory()
                    onResult(true, "Stock updated successfully")
                } else {
                    onResult(false, resp.body()?.message ?: "Stock update failed")
                }
            } catch (e: Exception) {
                onResult(false, "Connection error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadOrders(status: String? = null) {
        viewModelScope.launch {
            try {
                val resp = orderRepo.getOrders(status = status)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _orders.value = resp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun updateOrderStatus(orderId: Int, status: String, notes: String? = null, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            try {
                val resp = orderRepo.updateOrderStatus(orderId, status, notes)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    loadOrders()
                    onResult(true, "Order status updated to $status")
                } else {
                    onResult(false, resp.body()?.message ?: "Failed to update order")
                }
            } catch (e: Exception) {
                onResult(false, "Connection error: ${e.localizedMessage}")
            }
        }
    }

    fun loadDrivers() {
        viewModelScope.launch {
            try {
                val resp = staffRepo.getDrivers()
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _drivers.value = resp.body()!!.data ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun loadSuppliers() {
        viewModelScope.launch {
            try {
                val resp = staffRepo.getSuppliers()
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _suppliers.value = resp.body()!!.data ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }
}
