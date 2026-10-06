package com.sjay.cartfinder.phi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjay.cartfinder.data.model.Certificate
import com.sjay.cartfinder.data.model.Inspection
import com.sjay.cartfinder.data.model.PhiAlert
import com.sjay.cartfinder.data.repository.PhiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PhiState {
    object Idle : PhiState()
    object Loading : PhiState()
    data class CertificateData(val certificate: Certificate?) : PhiState()
    data class InspectionData(val inspections: List<Inspection>) : PhiState()
    data class AlertData(val alerts: List<PhiAlert>) : PhiState()
    data class PendingRequestsData(val requests: List<Certificate>) : PhiState()
    data class Error(val message: String) : PhiState()
    object Success : PhiState()
}

class PhiViewModel : ViewModel() {
    private val repository = PhiRepository()
    private val shopRepository = com.sjay.cartfinder.data.repository.ShopRepository()

    private val _phiState = MutableStateFlow<PhiState>(PhiState.Idle)
    val phiState: StateFlow<PhiState> = _phiState.asStateFlow()

    private val _pendingRequests = MutableStateFlow<List<Certificate>>(emptyList())
    val pendingRequests: StateFlow<List<Certificate>> = _pendingRequests.asStateFlow()

    private val _allAlerts = MutableStateFlow<List<PhiAlert>>(emptyList())
    val allAlerts: StateFlow<List<PhiAlert>> = _allAlerts.asStateFlow()

    private val _stallState = MutableStateFlow<com.sjay.cartfinder.data.model.Stall?>(null)
    val stallState: StateFlow<com.sjay.cartfinder.data.model.Stall?> = _stallState.asStateFlow()

    fun loadStall(stallId: String) {
        viewModelScope.launch {
            val result = shopRepository.getStallById(stallId)
            if (result.isSuccess) {
                _stallState.value = result.getOrNull()
            }
        }
    }

    fun loadCertificate(stallId: String) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val result = repository.getCertificate(stallId)
            if (result.isSuccess) {
                _phiState.value = PhiState.CertificateData(result.getOrNull())
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun requestCertificate(stallId: String) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val result = repository.requestCertificate(stallId)
            if (result.isSuccess) {
                _phiState.value = PhiState.Success
                loadCertificate(stallId)
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun issueCertificate(stallId: String, grade: String, score: Int = 100) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val cert = Certificate(
                id = stallId,
                stallId = stallId,
                grade = grade,
                score = score,
                status = "ACTIVE"
            )
            val result = repository.issueCertificate(cert)
            if (result.isSuccess) {
                _phiState.value = PhiState.Success
                loadCertificate(stallId)
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun loadInspections(stallId: String) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val result = repository.getInspections(stallId)
            if (result.isSuccess) {
                _phiState.value = PhiState.InspectionData(result.getOrDefault(emptyList()))
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    private suspend fun updateCertificateAverageScore(stallId: String) {
        val inspectionsResult = repository.getInspections(stallId)
        val certResult = repository.getCertificate(stallId)
        
        if (inspectionsResult.isSuccess && certResult.isSuccess) {
            val inspections = inspectionsResult.getOrDefault(emptyList())
            val cert = certResult.getOrNull()
            if (cert != null) {
                val avgScore = if (inspections.isNotEmpty()) {
                    inspections.sumOf { it.score } / inspections.size
                } else {
                    100
                }
                val newGrade = if (avgScore >= 90) "A" else if (avgScore >= 75) "B" else if (avgScore >= 50) "C" else "Rejected"
                
                val updatedCert = cert.copy(score = avgScore, grade = newGrade)
                repository.issueCertificate(updatedCert)
            }
        }
    }

    fun addInspection(stallId: String, inspectorId: String, score: Int, resultText: String, notes: String) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val insp = Inspection(
                stallId = stallId,
                inspectorId = inspectorId,
                score = score,
                result = resultText,
                notes = notes
            )
            val result = repository.addInspection(insp)
            if (result.isSuccess) {
                updateCertificateAverageScore(stallId)
                _phiState.value = PhiState.Success
                loadInspections(stallId)
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun deleteCertificate(stallId: String) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val result = repository.deleteCertificate(stallId)
            if (result.isSuccess) {
                _phiState.value = PhiState.Success
                loadCertificate(stallId)
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun deleteInspection(stallId: String, inspectionId: String) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val result = repository.deleteInspection(inspectionId)
            if (result.isSuccess) {
                updateCertificateAverageScore(stallId)
                _phiState.value = PhiState.Success
                loadInspections(stallId)
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun updateInspection(stallId: String, inspection: Inspection) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val result = repository.updateInspection(inspection)
            if (result.isSuccess) {
                updateCertificateAverageScore(stallId)
                _phiState.value = PhiState.Success
                loadInspections(stallId)
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun loadAllAlerts() {
        viewModelScope.launch {
            val result = repository.getAllActiveAlerts()
            if (result.isSuccess) {
                _allAlerts.value = result.getOrDefault(emptyList())
                _phiState.value = PhiState.AlertData(result.getOrDefault(emptyList()))
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }

    fun loadPendingRequests() {
        viewModelScope.launch {
            val result = repository.getPendingCertificateRequests()
            if (result.isSuccess) {
                _pendingRequests.value = result.getOrDefault(emptyList())
            }
        }
    }
}
