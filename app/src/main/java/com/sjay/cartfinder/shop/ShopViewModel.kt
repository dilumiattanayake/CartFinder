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
import kotlinx.coroutines.tasks.await

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

    fun createOrUpdateShop(context: android.content.Context, ownerId: String, name: String, description: String, category: String, phone: String, location: Location, imageUrl: String? = null, openingHours: String = "") {
        _shopState.value = ShopState.Loading
        viewModelScope.launch {
            try {
                val uploadedUrl = if (imageUrl != null) uploadImage(context, imageUrl) else null
                
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
                            imageUrl = uploadedUrl ?: existing.imageUrl,
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
                    imageUrl = uploadedUrl,
                    openingHours = openingHours
                )
                val createResult = repository.createStall(newStall)
                if (createResult.isSuccess) {
                    loadVendorShop(ownerId)
                } else {
                    _shopState.value = ShopState.Error("Failed to create shop")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _shopState.value = ShopState.Error("Failed to upload image. Please check your Firebase Storage Rules.")
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

    fun addProduct(context: android.content.Context, stallId: String, name: String, description: String, price: Double, categoryId: String, stockQuantity: Int, imageUrl: String? = null, preparationTime: Int = 8) {
        viewModelScope.launch {
            try {
                val uploadedUrl = if (imageUrl != null) uploadImage(context, imageUrl) else null
                val item = MenuItem(
                    stallId = stallId,
                    name = name,
                    description = description,
                    price = price,
                    categoryId = categoryId,
                    stockQuantity = stockQuantity,
                    imageUrl = uploadedUrl,
                    preparationTime = preparationTime
                )
                val result = repository.addMenuItem(stallId, item)
                if (result.isSuccess) {
                    loadProducts(stallId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _productsState.value = ProductsState.Error("Failed to upload image. Please check your Firebase Storage Rules.")
            }
        }
    }

    fun updateProduct(context: android.content.Context, stallId: String, menuItem: MenuItem) {
        viewModelScope.launch {
            try {
                val uploadedUrl = if (menuItem.imageUrl != null) uploadImage(context, menuItem.imageUrl) else null
                val updatedItem = menuItem.copy(imageUrl = uploadedUrl ?: menuItem.imageUrl)
                val result = repository.updateMenuItem(stallId, updatedItem)
                if (result.isSuccess) {
                    loadProducts(stallId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _productsState.value = ProductsState.Error("Failed to upload image. Please check your Firebase Storage Rules.")
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

    fun deleteProduct(stallId: String, productId: String) {
        viewModelScope.launch {
            val result = repository.deleteMenuItem(stallId, productId)
            if (result.isSuccess) {
                loadProducts(stallId)
            }
        }
    }

    suspend fun uploadImage(context: android.content.Context, uriStr: String): String {
        if (uriStr.startsWith("data:image")) return uriStr
        if (!uriStr.startsWith("content://") && !uriStr.startsWith("file://")) {
            return uriStr // Already a web URL or valid path
        }
        
        return kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
            val uri = android.net.Uri.parse(uriStr)
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = android.graphics.BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            if (bitmap == null) throw Exception("Failed to decode image")

            val maxDimension = 500
            val scale = Math.min(maxDimension.toFloat() / bitmap.width, maxDimension.toFloat() / bitmap.height)
            val scaledBitmap = if (scale < 1) {
                val matrix = android.graphics.Matrix()
                matrix.postScale(scale, scale)
                android.graphics.Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
            } else {
                bitmap
            }

            val outputStream = java.io.ByteArrayOutputStream()
            scaledBitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()

            val base64String = android.util.Base64.encodeToString(byteArray, android.util.Base64.NO_WRAP)
            "data:image/jpeg;base64,$base64String"
        }
    }
}
