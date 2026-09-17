package com.aaagrowers.app.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaagrowers.app.data.model.Certification
import com.aaagrowers.app.data.model.TrainingBooking
import com.aaagrowers.app.data.model.TrainingSession
import com.aaagrowers.app.data.repository.TrainingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FarmerViewModel(
    private val trainingRepo: TrainingRepository
) : ViewModel() {

    private val _sessions = MutableStateFlow<List<TrainingSession>>(emptyList())
    val sessions: StateFlow<List<TrainingSession>> = _sessions.asStateFlow()

    private val _selectedSession = MutableStateFlow<TrainingSession?>(null)
    val selectedSession: StateFlow<TrainingSession?> = _selectedSession.asStateFlow()

    private val _myBookings = MutableStateFlow<List<TrainingBooking>>(emptyList())
    val myBookings: StateFlow<List<TrainingBooking>> = _myBookings.asStateFlow()

    private val _certificates = MutableStateFlow<List<Certification>>(emptyList())
    val certificates: StateFlow<List<Certification>> = _certificates.asStateFlow()

    private val _selectedCertificate = MutableStateFlow<Certification?>(null)
    val selectedCertificate: StateFlow<Certification?> = _selectedCertificate.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    init {
        loadFarmerHub()
    }

    fun loadFarmerHub() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                loadTrainings()
                loadMyBookings()
                loadCertificates()
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadTrainings(category: String? = null) {
        viewModelScope.launch {
            try {
                val resp = trainingRepo.getTrainings(upcomingOnly = true, category = category)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _sessions.value = resp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun loadTrainingDetail(sessionId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = trainingRepo.getTrainingById(sessionId)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _selectedSession.value = resp.body()!!.data
                }
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun bookSession(sessionId: Int, notes: String? = null, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = trainingRepo.bookTraining(sessionId, notes)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    loadMyBookings()
                    loadTrainings()
                    onResult(true, "Session booked successfully!")
                } else {
                    onResult(false, resp.body()?.message ?: "Booking failed")
                }
            } catch (e: Exception) {
                onResult(false, "Connection error: Please try again")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadMyBookings() {
        viewModelScope.launch {
            try {
                val resp = trainingRepo.getMyBookings()
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _myBookings.value = resp.body()!!.data ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun cancelBooking(bookingId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                trainingRepo.cancelBooking(bookingId)
                loadMyBookings()
                loadTrainings()
                onComplete()
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun loadCertificates() {
        viewModelScope.launch {
            try {
                val resp = trainingRepo.getMyCertifications()
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _certificates.value = resp.body()!!.data?.items ?: emptyList()
                }
            } catch (e: Exception) {
                // handle
            }
        }
    }

    fun loadCertificateDetail(certId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val resp = trainingRepo.getCertificateById(certId)
                if (resp.isSuccessful && resp.body()?.success == true) {
                    _selectedCertificate.value = resp.body()!!.data
                }
            } catch (e: Exception) {
                // handle
            } finally {
                _isLoading.value = false
            }
        }
    }
}
