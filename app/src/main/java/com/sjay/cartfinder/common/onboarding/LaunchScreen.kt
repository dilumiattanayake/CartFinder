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
import kotlinx.coroutines.tasks.await

@Composable
fun LaunchScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        delay(2000)
        
        val sharedPrefs = context.getSharedPreferences("CartFinderPrefs", android.content.Context.MODE_PRIVATE)
        val onboardingCompleted = sharedPrefs.getBoolean("onboarding_completed", false)
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        
        if (auth.currentUser != null) {
            val role = sharedPrefs.getString("user_role", "Customer") ?: "Customer"
            if (role.equals("Vendor", ignoreCase = true)) {
                navController.navigate(Screen.VendorDashboard.route) { popUpTo(Screen.Launch.route) { inclusive = true } }
            } else if (role.equals("PHI", ignoreCase = true)) {
                navController.navigate(Screen.PhiDashboard.route) { popUpTo(Screen.Launch.route) { inclusive = true } }
            } else {
                navController.navigate(Screen.CustomerDashboard.route) { popUpTo(Screen.Launch.route) { inclusive = true } }
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
