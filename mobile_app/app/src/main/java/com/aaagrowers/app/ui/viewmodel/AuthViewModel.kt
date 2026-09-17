package com.aaagrowers.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaagrowers.app.data.api.ApiClient
import com.aaagrowers.app.data.local.SessionManager
import com.aaagrowers.app.data.model.*
import com.aaagrowers.app.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private var authRepo: AuthRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(sessionManager.getUser())
    val user: StateFlow<User?> = _user.asStateFlow()

    private val _serverUrl = MutableStateFlow(sessionManager.getServerUrl())
    val serverUrl: StateFlow<String> = _serverUrl.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateServerUrl(newUrl: String, context: android.content.Context) {
        sessionManager.setServerUrl(newUrl)
        _serverUrl.value = sessionManager.getServerUrl()
        ApiClient.resetService()
        val newApi = ApiClient.getService(context)
        authRepo = AuthRepository(newApi)
    }

    fun login(email: String, password: String, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = authRepo.login(LoginRequest(email.trim(), password))
                if (resp.isSuccessful && resp.body()?.success == true) {
                    val authData = resp.body()!!.data!!
                    sessionManager.saveAuth(authData.token, authData.user)
                    _user.value = authData.user
                    onSuccess(authData.user.role)
                } else {
                    _errorMessage.value = resp.body()?.message ?: "Invalid email or password"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error (${sessionManager.getServerUrl()}): ${e.localizedMessage ?: "Please verify server is running"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerCustomer(request: RegisterCustomerRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = authRepo.registerCustomer(request)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    val authData = resp.body()!!.data!!
                    sessionManager.saveAuth(authData.token, authData.user)
                    _user.value = authData.user
                    onSuccess()
                } else {
                    _errorMessage.value = resp.body()?.message ?: "Customer registration failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error: ${e.localizedMessage ?: "Please check server status"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerFarmer(request: RegisterFarmerRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = authRepo.registerFarmer(request)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    val authData = resp.body()!!.data!!
                    sessionManager.saveAuth(authData.token, authData.user)
                    _user.value = authData.user
                    onSuccess()
                } else {
                    _errorMessage.value = resp.body()?.message ?: "Farmer registration failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error: ${e.localizedMessage ?: "Please check server status"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun registerUser(request: RegisterUserRequest, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val resp = authRepo.registerUser(request)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    val authData = resp.body()!!.data!!
                    sessionManager.saveAuth(authData.token, authData.user)
                    _user.value = authData.user
                    onSuccess(authData.user.role)
                } else {
                    _errorMessage.value = resp.body()?.message ?: "Registration failed"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Connection error: ${e.localizedMessage ?: "Please check server status"}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun logout(onLogout: () -> Unit) {
        sessionManager.logout()
        _user.value = null
        onLogout()
    }
}
