package com.sjay.cartfinder.common.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class CustomerStats(
    val orders: Int = 0,
    val reviews: Int = 0,
    val carts: Int = 0
)

data class VendorStats(
    val products: Int = 0,
    val orders: Int = 0,
    val reviews: Int = 0
)

data class PhiStats(
    val inspections: Int = 0,
    val certified: Int = 0
)

class SettingsViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()

    private val _customerStats = MutableStateFlow(CustomerStats())
    val customerStats: StateFlow<CustomerStats> = _customerStats.asStateFlow()

    private val _vendorStats = MutableStateFlow(VendorStats())
    val vendorStats: StateFlow<VendorStats> = _vendorStats.asStateFlow()

    private val _phiStats = MutableStateFlow(PhiStats())
    val phiStats: StateFlow<PhiStats> = _phiStats.asStateFlow()

    fun loadCustomerStats(userId: String) {
        viewModelScope.launch {
            try {
                val reviewsSnapshot = db.collection("reviews")
                    .whereEqualTo("userId", userId)
                    .get().await()
                val reviews = reviewsSnapshot.size()

                val ordersSnapshot = db.collection("orders")
                    .whereEqualTo("customerId", userId)
                    .get().await()
                val orders = ordersSnapshot.size()
                
                val cartsSnapshot = db.collection("carts")
                    .whereEqualTo("userId", userId)
                    .get().await()
                val carts = cartsSnapshot.size()

                _customerStats.value = CustomerStats(
                    orders = orders,
                    reviews = reviews,
                    carts = carts
                )
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun loadVendorStats(userId: String) {
        viewModelScope.launch {
            try {
                // First get the stall associated with this vendor
                val stallSnapshot = db.collection("stalls")
                    .whereEqualTo("ownerId", userId)
                    .limit(1)
                    .get().await()

                if (stallSnapshot.isEmpty) {
                    _vendorStats.value = VendorStats(0, 0, 0)
                    return@launch
                }
                
                val stallId = stallSnapshot.documents[0].id

                val productsSnapshot = db.collection("stalls").document(stallId).collection("menuItems").get().await()
                val products = productsSnapshot.size()

                val ordersSnapshot = db.collection("orders")
                    .whereEqualTo("stallId", stallId)
                    .get().await()
                val orders = ordersSnapshot.size()

                val reviewsSnapshot = db.collection("reviews")
                    .whereEqualTo("stallId", stallId)
                    .get().await()
                val reviews = reviewsSnapshot.size()

                _vendorStats.value = VendorStats(
                    products = products,
                    orders = orders,
                    reviews = reviews
                )
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun loadPhiStats() {
        viewModelScope.launch {
            try {
                val inspectionsSnapshot = db.collection("inspections").get().await()
                val inspections = inspectionsSnapshot.size()

                val certSnapshot = db.collection("certificates")
                    .whereEqualTo("status", "ACTIVE")
                    .get().await()
                val certified = certSnapshot.size()

                _phiStats.value = PhiStats(
                    inspections = inspections,
                    certified = certified
                )
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    suspend fun getUserDetails(userId: String): Map<String, Any> {
        return try {
            val doc = db.collection("users").document(userId).get().await()
            doc.data ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }

    fun updateProfile(name: String, email: String, phone: String, onComplete: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        if (user == null) {
            onComplete("Not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                // Update FirebaseAuth email if changed
                if (user.email != email) {
                    user.updateEmail(email).await()
                }

                // Update FirebaseAuth profile (display name) if changed
                if (user.displayName != name) {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    user.updateProfile(profileUpdates).await()
                }

                // Update Firestore
                val userMap = hashMapOf<String, Any>(
                    "fullName" to name,
                    "email" to email,
                    "phone" to phone
                )
                db.collection("users").document(user.uid).update(userMap).await()
                
                onComplete("Profile updated successfully.")
            } catch (e: Exception) {
                onComplete(e.message ?: "Failed to update profile.")
            }
        }
    }

    fun updatePassword(currentPass: String, newPass: String, onComplete: (String) -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser
        val email = user?.email
        if (user == null || email == null) {
            onComplete("Not logged in.")
            return
        }

        viewModelScope.launch {
            try {
                // Re-authenticate
                val credential = com.google.firebase.auth.EmailAuthProvider.getCredential(email, currentPass)
                user.reauthenticate(credential).await()
                
                // Update password
                user.updatePassword(newPass).await()
                onComplete("Password updated successfully.")
            } catch (e: Exception) {
                onComplete(e.message ?: "Failed to update password.")
            }
        }
    }
}
