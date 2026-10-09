package com.sjay.cartfinder.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjay.cartfinder.data.model.Notification
import com.sjay.cartfinder.data.repository.NotificationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class NotificationState {
    object Idle : NotificationState()
    object Loading : NotificationState()
    data class Success(val notifications: List<Notification>) : NotificationState()
    data class Error(val message: String) : NotificationState()
}

class NotificationViewModel : ViewModel() {
    private val repository = NotificationRepository()

    private val _notificationsState = MutableStateFlow<NotificationState>(NotificationState.Idle)
    val notificationsState: StateFlow<NotificationState> = _notificationsState.asStateFlow()

    fun loadNotifications(userId: String) {
        viewModelScope.launch {
            _notificationsState.value = NotificationState.Loading
            val result = repository.getNotificationsForUser(userId)
            if (result.isSuccess) {
                _notificationsState.value = NotificationState.Success(result.getOrDefault(emptyList()))
            } else {
                _notificationsState.value = NotificationState.Error(result.exceptionOrNull()?.message ?: "Failed to load notifications")
            }
        }
    }

    fun markAsRead(notificationId: String, userId: String) {
        viewModelScope.launch {
            repository.markAsRead(notificationId)
            loadNotifications(userId) // Reload to reflect changes
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            repository.markAllAsRead(userId)
            loadNotifications(userId)
        }
    }
}
