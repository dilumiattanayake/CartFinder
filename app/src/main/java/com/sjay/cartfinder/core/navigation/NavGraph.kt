package com.sjay.cartfinder.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sjay.cartfinder.common.onboarding.LaunchScreen
import com.sjay.cartfinder.common.onboarding.OnboardingScreen
import com.sjay.cartfinder.common.auth.ChooseRoleScreen
import com.sjay.cartfinder.common.auth.LoginScreen
import com.sjay.cartfinder.common.auth.SignUpScreen
import com.sjay.cartfinder.common.settings.SettingsScreen
import com.sjay.cartfinder.common.profile.ProfileScreen
import com.sjay.cartfinder.reviews.ReviewListScreen
import com.sjay.cartfinder.reviews.SubmitReviewScreen

@Composable
fun CartFinderNavGraph(
    navController: NavHostController,
    startDestination: String = Screen.Launch.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Launch.route) {
            LaunchScreen(navController = navController)
        }
        composable(Screen.Onboarding.route) {
            OnboardingScreen(navController = navController)
        }
        composable(Screen.ChooseRole.route) {
            ChooseRoleScreen(navController = navController)
        }
        composable(Screen.Login.route) {
            LoginScreen(navController = navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(Screen.ReviewList.route) {
            ReviewListScreen(navController = navController)
        }
        composable(Screen.SubmitReview.route) {
            SubmitReviewScreen(navController = navController)
        }
    }
}
