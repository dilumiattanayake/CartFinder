package com.sjay.cartfinder.common.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sjay.cartfinder.R
import com.sjay.cartfinder.core.navigation.Screen
import kotlinx.coroutines.delay

@Composable
fun LaunchScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        delay(2000)
        
        val sharedPrefs = context.getSharedPreferences("CartFinderPrefs", android.content.Context.MODE_PRIVATE)
        val onboardingCompleted = sharedPrefs.getBoolean("onboarding_completed", false)
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        
        if (auth.currentUser != null) {
            // Logged in, we need to check role to go to right dashboard.
            // For now, if role is unknown, just go to a default screen.
            // Ideally, fetch from Firestore. Let's assume we don't know yet, we just go to a temporary split screen or VendorDashboard.
            // Let's fetch role:
            try {
                val doc = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    .collection("users").document(auth.currentUser!!.uid).get()
                    .kotlinx.coroutines.tasks.await()
                val role = doc.getString("role") ?: "Customer"
                if (role == "Vendor") {
                    navController.navigate(Screen.VendorDashboard.route) { popUpTo(Screen.Launch.route) { inclusive = true } }
                } else {
                    // Navigate to customer dashboard (ReviewList for now as placeholder)
                    navController.navigate(Screen.ReviewList.route) { popUpTo(Screen.Launch.route) { inclusive = true } }
                }
            } catch (e: Exception) {
                // Fallback
                navController.navigate(Screen.ReviewList.route) { popUpTo(Screen.Launch.route) { inclusive = true } }
            }
        } else if (onboardingCompleted) {
            navController.navigate(Screen.ChooseRole.route) {
                popUpTo(Screen.Launch.route) { inclusive = true }
            }
        } else {
            navController.navigate(Screen.Onboarding.route) {
                popUpTo(Screen.Launch.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logotext),
            contentDescription = "CartFinder Logo",
            modifier = Modifier.size(250.dp)
        )
    }
}
