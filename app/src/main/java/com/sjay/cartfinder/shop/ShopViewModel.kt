package com.sjay.cartfinder.shop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjay.cartfinder.data.model.Location
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.data.repository.ShopRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class ShopState {
    object Idle : ShopState()
    object Loading : ShopState()
    data class Success(val stall: Stall?) : ShopState()
    data class StallsList(val stalls: List<Stall>) : ShopState()
    data class Error(val message: String) : ShopState()
}

sealed class ProductsState {
    object Idle : ProductsState()
    object Loading : ProductsState()
    data class Success(val products: List<MenuItem>) : ProductsState()
    data class Error(val message: String) : ProductsState()
}

class ShopViewModel(
    private val repository: ShopRepository = ShopRepository()
) : ViewModel() {

    private val _shopState = MutableStateFlow<ShopState>(ShopState.Idle)
    val shopState: StateFlow<ShopState> = _shopState.asStateFlow()

    private val _allStallsState = MutableStateFlow<ShopState>(ShopState.Idle)
    val allStallsState: StateFlow<ShopState> = _allStallsState.asStateFlow()

    private val _productsState = MutableStateFlow<ProductsState>(ProductsState.Idle)
    val productsState: StateFlow<ProductsState> = _productsState.asStateFlow()

    fun loadVendorShop(ownerId: String) {
        _shopState.value = ShopState.Loading
        viewModelScope.launch {
            val result = repository.getStallByOwner(ownerId)
            if (result.isSuccess) {
                _shopState.value = ShopState.Success(result.getOrNull())
            } else {
                _shopState.value = ShopState.Error(result.exceptionOrNull()?.message ?: "Failed to load shop")
            }
        }
    }

    fun loadAllStalls() {
        _allStallsState.value = ShopState.Loading
        viewModelScope.launch {
            val result = repository.getAllStalls()
            if (result.isSuccess) {
                val activeStalls = result.getOrDefault(emptyList()).filter { it.isOpen }
                _allStallsState.value = ShopState.StallsList(activeStalls)
            } else {
                _allStallsState.value = ShopState.Error(result.exceptionOrNull()?.message ?: "Failed to load stalls")
            }
        }
    }
    
    fun toggleStallStatus(stall: Stall, isOpen: Boolean) {
        viewModelScope.launch {
            val updated = stall.copy(isOpen = isOpen)
            val result = repository.updateStall(updated)
            if (result.isSuccess) {
                _shopState.value = ShopState.Success(updated)
            }
        }
    }

    fun createOrUpdateShop(ownerId: String, name: String, description: String, category: String, phone: String, location: Location, imageUrl: String? = null, openingHours: String = "") {
        _shopState.value = ShopState.Loading
        viewModelScope.launch {
            // First check if shop exists
            val existingResult = repository.getStallByOwner(ownerId)
            if (existingResult.isSuccess) {
                val existing = existingResult.getOrNull()
                if (existing != null) {
                    val updated = existing.copy(
                        name = name,
                        description = description,
                        category = category,
                        phone = phone,
                        location = location,
                        imageUrl = imageUrl ?: existing.imageUrl,
                        openingHours = openingHours.ifEmpty { existing.openingHours }
                    )
                    val result = repository.updateStall(updated)
                    if (result.isSuccess) {
                        _shopState.value = ShopState.Success(updated)
                    } else {
                        _shopState.value = ShopState.Error("Failed to update shop")
                    }
                    return@launch
                }
            }
            
            // Create new
            val newStall = Stall(
                ownerId = ownerId,
                name = name,
                description = description,
                category = category,
                phone = phone,
                location = location,
                imageUrl = imageUrl,
                openingHours = openingHours
            )
            val createResult = repository.createStall(newStall)
            if (createResult.isSuccess) {
                loadVendorShop(ownerId)
            } else {
                _shopState.value = ShopState.Error("Failed to create shop")
            }
        }
    }

    fun loadProducts(stallId: String) {
        _productsState.value = ProductsState.Loading
        viewModelScope.launch {
            val result = repository.getMenuItemsForStall(stallId)
            if (result.isSuccess) {
                _productsState.value = ProductsState.Success(result.getOrDefault(emptyList()))
            } else {
                _productsState.value = ProductsState.Error("Failed to load products")
            }
        }
    }

    fun addProduct(stallId: String, name: String, description: String, price: Double, categoryId: String, stockQuantity: Int) {
        viewModelScope.launch {
            val item = MenuItem(
                stallId = stallId,
                name = name,
                description = description,
                price = price,
                categoryId = categoryId,
                stockQuantity = stockQuantity
            )
            val result = repository.addMenuItem(stallId, item)
            if (result.isSuccess) {
                loadProducts(stallId)
            }
        }
    }

    fun updateProduct(stallId: String, menuItem: MenuItem) {
        viewModelScope.launch {
            val result = repository.updateMenuItem(stallId, menuItem)
            if (result.isSuccess) {
                loadProducts(stallId)
            }
        }
    }

    fun archiveProduct(stallId: String, menuItemId: String) {
        viewModelScope.launch {
            val result = repository.archiveMenuItem(stallId, menuItemId)
            if (result.isSuccess) {
                loadProducts(stallId)
            }
        }
    }
}
