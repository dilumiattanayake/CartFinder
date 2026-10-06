package com.sjay.cartfinder.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sjay.cartfinder.data.model.Order
import com.sjay.cartfinder.data.model.Notification
import kotlinx.coroutines.tasks.await

class OrderRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val ordersCollection = firestore.collection("orders")

    suspend fun placeOrder(order: Order): Result<String> {
        return try {
            val document = ordersCollection.document()
            val newOrder = order.copy(id = document.id)
            document.set(newOrder).await()
            
            // Notify Vendor
            try {
                val stallDoc = firestore.collection("stalls").document(order.stallId).get().await()
                val vendorId = stallDoc.getString("vendorId")
                if (vendorId != null) {
                    NotificationRepository().sendNotification(
                        Notification(
                            userId = vendorId,
                            title = "New Order!",
                            message = "You have received a new order for ${order.items.size} items.",
                            type = "NEW_ORDER",
                            referenceId = document.id
                        )
                    )
                }
            } catch (e: Exception) {}

            Result.success(document.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCustomerOrders(customerId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("customerId", customerId)
                .get()
                .await()
            
            val orders = snapshot.documents
                .mapNotNull { it.toObject(Order::class.java) }
                .sortedByDescending { it.createdAt }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVendorOrders(stallId: String): Result<List<Order>> {
        return try {
            val snapshot = ordersCollection
                .whereEqualTo("stallId", stallId)
                .get()
                .await()
            
            val orders = snapshot.documents
                .mapNotNull { it.toObject(Order::class.java) }
                .sortedByDescending { it.createdAt }
            Result.success(orders)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateOrderStatus(orderId: String, newStatus: String): Result<Unit> {
        return try {
            ordersCollection.document(orderId).update("status", newStatus).await()
            
            // Notify Customer
            try {
                val orderDoc = ordersCollection.document(orderId).get().await()
                val order = orderDoc.toObject(Order::class.java)
                if (order != null) {
                    NotificationRepository().sendNotification(
                        Notification(
                            userId = order.customerId,
                            title = "Order Status Updated",
                            message = "Your order status is now: $newStatus",
                            type = "ORDER_STATUS",
                            referenceId = orderId
                        )
                    )
                }
            } catch (e: Exception) {}

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
