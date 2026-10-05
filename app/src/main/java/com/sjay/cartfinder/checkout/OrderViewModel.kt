package com.sjay.cartfinder.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjay.cartfinder.data.model.Order
import com.sjay.cartfinder.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class OrderListState {
    object Idle : OrderListState()
    object Loading : OrderListState()
    data class Success(val orders: List<Order>) : OrderListState()
    data class Error(val message: String) : OrderListState()
}

class OrderViewModel : ViewModel() {
    private val orderRepo = OrderRepository()

    private val _orderState = MutableStateFlow<OrderListState>(OrderListState.Idle)
    val orderState: StateFlow<OrderListState> = _orderState.asStateFlow()

    fun loadCustomerOrders(customerId: String) {
        viewModelScope.launch {
            _orderState.value = OrderListState.Loading
            val result = orderRepo.getCustomerOrders(customerId)
            if (result.isSuccess) {
                _orderState.value = OrderListState.Success(result.getOrNull() ?: emptyList())
            } else {
                _orderState.value = OrderListState.Error(result.exceptionOrNull()?.message ?: "Failed to load orders")
            }
        }
    }

    fun loadVendorOrders(stallId: String) {
        viewModelScope.launch {
            _orderState.value = OrderListState.Loading
            val result = orderRepo.getVendorOrders(stallId)
            if (result.isSuccess) {
                _orderState.value = OrderListState.Success(result.getOrNull() ?: emptyList())
            } else {
                _orderState.value = OrderListState.Error(result.exceptionOrNull()?.message ?: "Failed to load orders")
            }
        }
    }

    fun updateOrderStatus(orderId: String, newStatus: String, stallId: String) {
        viewModelScope.launch {
            val result = orderRepo.updateOrderStatus(orderId, newStatus)
            if (result.isSuccess) {
                // Reload vendor orders to reflect changes
                loadVendorOrders(stallId)
            } else {
                // To keep it simple, we don't show an error state if a single update fails,
                // but we could handle it through a separate flow for toast messages.
            }
        }
    }
}
