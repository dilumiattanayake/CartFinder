package com.sjay.cartfinder.common.onboarding

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sjay.cartfinder.R
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await

@Composable
fun LaunchScreen(navController: NavController) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    
    LaunchedEffect(Unit) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 800)
        )
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300)
        )
        delay(1000)
        
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
            .background(PrimaryOrange),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logotext),
            contentDescription = "CartFinder Logo",
            modifier = Modifier
                .size(250.dp)
                .scale(scale.value)
                .alpha(alpha.value)
        )
    }
}
