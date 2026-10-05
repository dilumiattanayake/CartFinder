package com.sjay.cartfinder.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sjay.cartfinder.data.model.Cart
import com.sjay.cartfinder.data.model.CartItem
import kotlinx.coroutines.tasks.await

class CartRepository {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCart(userId: String): Result<Cart> {
        return try {
            val snapshot = firestore.collection("users").document(userId)
                .collection("cart").document("current")
                .get().await()
            val cart = snapshot.toObject(Cart::class.java) ?: Cart()
            Result.success(cart)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateCart(userId: String, cart: Cart): Result<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .collection("cart").document("current")
                .set(cart).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearCart(userId: String): Result<Unit> {
        return updateCart(userId, Cart())
    }
}
