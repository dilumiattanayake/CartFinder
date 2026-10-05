package com.sjay.cartfinder.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class StallListState {
    object Idle : StallListState()
    object Loading : StallListState()
    data class Success(val stalls: List<Stall>) : StallListState()
    data class Error(val message: String) : StallListState()
}

sealed class StallMenuState {
    object Idle : StallMenuState()
    object Loading : StallMenuState()
    data class Success(val items: List<MenuItem>) : StallMenuState()
    data class Error(val message: String) : StallMenuState()
}

class CustomerDashboardViewModel : ViewModel() {
    private val shopRepo = ShopRepository()

    private val _stallsState = MutableStateFlow<StallListState>(StallListState.Idle)
    val stallsState: StateFlow<StallListState> = _stallsState.asStateFlow()

    private val _menuState = MutableStateFlow<StallMenuState>(StallMenuState.Idle)
    val menuState: StateFlow<StallMenuState> = _menuState.asStateFlow()

    fun loadAllStalls() {
        viewModelScope.launch {
            _stallsState.value = StallListState.Loading
            val result = shopRepo.getAllStalls()
            if (result.isSuccess) {
                _stallsState.value = StallListState.Success(result.getOrNull() ?: emptyList())
            } else {
                _stallsState.value = StallListState.Error(result.exceptionOrNull()?.message ?: "Failed to load stalls")
            }
        }
    }

    fun loadStallMenu(stallId: String) {
        viewModelScope.launch {
            _menuState.value = StallMenuState.Loading
            val result = shopRepo.getMenuItemsForStall(stallId)
            if (result.isSuccess) {
                // Only show available items to customer
                val availableItems = (result.getOrNull() ?: emptyList()).filter { it.available }
                _menuState.value = StallMenuState.Success(availableItems)
            } else {
                _menuState.value = StallMenuState.Error(result.exceptionOrNull()?.message ?: "Failed to load menu")
            }
        }
    }
}
