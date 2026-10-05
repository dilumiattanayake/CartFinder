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
    data class Error(val message: String) : PhiState()
    object Success : PhiState()
}

class PhiViewModel : ViewModel() {
    private val repository = PhiRepository()

    private val _phiState = MutableStateFlow<PhiState>(PhiState.Idle)
    val phiState: StateFlow<PhiState> = _phiState.asStateFlow()

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

    fun issueCertificate(stallId: String, grade: String) {
        viewModelScope.launch {
            _phiState.value = PhiState.Loading
            val cert = Certificate(
                id = stallId,
                stallId = stallId,
                grade = grade,
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
                _phiState.value = PhiState.Success
                loadInspections(stallId)
            } else {
                _phiState.value = PhiState.Error(result.exceptionOrNull()?.message ?: "Unknown Error")
            }
        }
    }
}
