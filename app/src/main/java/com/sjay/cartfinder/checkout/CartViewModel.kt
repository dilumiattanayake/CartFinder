package com.sjay.cartfinder.checkout

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sjay.cartfinder.data.model.Cart
import com.sjay.cartfinder.data.model.CartItem
import com.sjay.cartfinder.data.model.Order
import com.sjay.cartfinder.data.repository.CartRepository
import com.sjay.cartfinder.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

sealed class CartState {
    object Idle : CartState()
    object Loading : CartState()
    data class Success(val cart: Cart) : CartState()
    data class Error(val message: String) : CartState()
    data class CheckoutSuccess(val orderId: String, val totalAmount: Double) : CartState()
}

class CartViewModel : ViewModel() {
    private val cartRepo = CartRepository()
    private val orderRepo = OrderRepository()

    private val _cartState = MutableStateFlow<CartState>(CartState.Idle)
    val cartState: StateFlow<CartState> = _cartState.asStateFlow()

    fun loadCart(userId: String) {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            val result = cartRepo.getCart(userId)
            if (result.isSuccess) {
                _cartState.value = CartState.Success(result.getOrNull()!!)
            } else {
                _cartState.value = CartState.Error(result.exceptionOrNull()?.message ?: "Failed to load cart")
            }
        }
    }

    fun addItemToCart(userId: String, newItem: CartItem) {
        viewModelScope.launch {
            val currentState = _cartState.value
            if (currentState is CartState.Success) {
                var currentCart = currentState.cart

                // Validate same stall rule
                if (currentCart.stallId.isNotEmpty() && currentCart.stallId != newItem.stallId) {
                    _cartState.value = CartState.Error("You can only order from one stall at a time. Please clear your cart first.")
                    // Reset back to success after showing error
                    kotlinx.coroutines.delay(3000)
                    _cartState.value = currentState
                    return@launch
                }

                val updatedItems = currentCart.items.toMutableList()
                val existingItemIndex = updatedItems.indexOfFirst { it.productId == newItem.productId }
                
                if (existingItemIndex != -1) {
                    val existing = updatedItems[existingItemIndex]
                    updatedItems[existingItemIndex] = existing.copy(quantity = existing.quantity + newItem.quantity)
                } else {
                    val finalItem = if (newItem.id.isEmpty()) newItem.copy(id = UUID.randomUUID().toString()) else newItem
                    updatedItems.add(finalItem)
                }

                val newCart = currentCart.copy(
                    stallId = newItem.stallId,
                    items = updatedItems
                )

                _cartState.value = CartState.Loading
                val result = cartRepo.updateCart(userId, newCart)
                if (result.isSuccess) {
                    _cartState.value = CartState.Success(newCart)
                } else {
                    _cartState.value = CartState.Error("Failed to update cart")
                }
            }
        }
    }

    fun updateQuantity(userId: String, itemId: String, newQuantity: Int) {
        viewModelScope.launch {
            val currentState = _cartState.value
            if (currentState is CartState.Success) {
                val updatedItems = currentState.cart.items.map { 
                    if (it.id == itemId) it.copy(quantity = newQuantity) else it
                }.filter { it.quantity > 0 }

                val newCart = currentState.cart.copy(
                    stallId = if (updatedItems.isEmpty()) "" else currentState.cart.stallId,
                    items = updatedItems
                )

                _cartState.value = CartState.Loading
                val result = cartRepo.updateCart(userId, newCart)
                if (result.isSuccess) {
                    _cartState.value = CartState.Success(newCart)
                } else {
                    _cartState.value = CartState.Error("Failed to update cart")
                }
            }
        }
    }

    fun clearCart(userId: String) {
        viewModelScope.launch {
            _cartState.value = CartState.Loading
            val result = cartRepo.clearCart(userId)
            if (result.isSuccess) {
                _cartState.value = CartState.Success(Cart())
            } else {
                _cartState.value = CartState.Error("Failed to clear cart")
            }
        }
    }

    fun checkout(userId: String, pickupSlot: String) {
        viewModelScope.launch {
            val currentState = _cartState.value
            if (currentState is CartState.Success) {
                val cart = currentState.cart
                if (cart.items.isEmpty()) return@launch

                _cartState.value = CartState.Loading
                val totalAmount = cart.items.sumOf { it.price * it.quantity }
                val newOrder = Order(
                    customerId = userId,
                    stallId = cart.stallId,
                    items = cart.items,
                    totalAmount = totalAmount,
                    status = "PENDING_PAYMENT",
                    pickupSlot = pickupSlot
                )

                val orderResult = orderRepo.placeOrder(newOrder)
                if (orderResult.isSuccess) {
                    val orderId = orderResult.getOrNull() ?: ""
                    cartRepo.clearCart(userId) // Empty cart on success
                    _cartState.value = CartState.CheckoutSuccess(orderId, totalAmount)
                } else {
                    _cartState.value = CartState.Error("Failed to place order")
                }
            }
        }
    }
}
