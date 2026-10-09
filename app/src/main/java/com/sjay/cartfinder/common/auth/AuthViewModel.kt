package com.sjay.cartfinder.common.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val message: String) : AuthState()
    data class Error(val error: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun resetState() {
        _authState.value = AuthState.Idle
    }

    fun signUp(email: String, password: String, fullName: String, phone: String, role: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.createUserWithEmailAndPassword(email, password).await()
                val userId = authResult.user?.uid
                if (userId != null) {
                    // Fetch FCM token
                    var fcmToken = ""
                    try {
                        fcmToken = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
                    } catch (e: Exception) {
                        // ignore if token fetch fails
                    }

                    val userMap = hashMapOf(
                        "uid" to userId,
                        "email" to email,
                        "fullName" to fullName,
                        "phone" to phone,
                        "role" to role,
                        "fcmToken" to fcmToken,
                        "createdAt" to System.currentTimeMillis()
                    )
                    // Store user data in Firestore
                    firestore.collection("users").document(userId).set(userMap).await()
                    _authState.value = AuthState.Success("Sign up successful")
                } else {
                    _authState.value = AuthState.Error("Failed to get user ID")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun login(email: String, password: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val authResult = auth.signInWithEmailAndPassword(email, password).await()
                
                // Update FCM token on login
                authResult.user?.uid?.let { uid ->
                    try {
                        val token = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
                        firestore.collection("users").document(uid).update("fcmToken", token).await()
                    } catch (e: Exception) {
                        // ignore
                    }
                }

                _authState.value = AuthState.Success("Login successful")
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signInWithGoogle(idToken: String, role: String) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val credential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = auth.signInWithCredential(credential).await()
                val user = authResult.user
                if (user != null) {
                    // Check if user exists in Firestore
                    val docRef = firestore.collection("users").document(user.uid)
                    val doc = docRef.get().await()
                    
                    var fcmToken = ""
                    try {
                        fcmToken = com.google.firebase.messaging.FirebaseMessaging.getInstance().token.await()
                    } catch (e: Exception) {}

                    if (!doc.exists()) {
                        // First time login with Google, create profile
                        val userMap = hashMapOf(
                            "uid" to user.uid,
                            "email" to (user.email ?: ""),
                            "fullName" to (user.displayName ?: "Google User"),
                            "phone" to "",
                            "role" to role,
                            "fcmToken" to fcmToken,
                            "createdAt" to System.currentTimeMillis()
                        )
                        docRef.set(userMap).await()
                        _authState.value = AuthState.Success("Google Login successful")
                    } else {
                        // If user exists but role is different?
                        val existingRole = doc.getString("role")
                        if (existingRole != null && existingRole.lowercase() != role.lowercase()) {
                            auth.signOut()
                            _authState.value = AuthState.Error("This email is already registered as $existingRole. Please login as $existingRole.")
                        } else {
                            // Update FCM token
                            if (fcmToken.isNotEmpty()) {
                                docRef.update("fcmToken", fcmToken).await()
                            }
                            _authState.value = AuthState.Success("Google Login successful")
                        }
                    }
                } else {
                    _authState.value = AuthState.Error("Google Login failed: No user returned")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google Sign In failed")
            }
        }
    }
}
