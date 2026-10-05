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
        composable(
            route = Screen.Login.route,
            arguments = listOf(androidx.navigation.navArgument("role") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "Customer"
            LoginScreen(navController = navController, role = role)
        }
        composable(
            route = Screen.SignUp.route,
            arguments = listOf(androidx.navigation.navArgument("role") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "Customer"
            SignUpScreen(navController = navController, role = role)
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
        composable(Screen.VendorDashboard.route) {
            com.sjay.cartfinder.shop.VendorDashboardScreen(navController = navController)
        }
        composable(Screen.EditShop.route) {
            com.sjay.cartfinder.shop.EditShopScreen(navController = navController)
        }
        composable(Screen.MapPicker.route) {
            com.sjay.cartfinder.shop.MapPickerScreen(navController = navController)
        }
        composable(Screen.ProductManagement.route) {
            com.sjay.cartfinder.shop.ProductManagementScreen(navController = navController)
        }
        composable(Screen.Cart.route) {
            com.sjay.cartfinder.checkout.CartScreen(navController = navController)
        }
        composable(Screen.CustomerOrders.route) {
            com.sjay.cartfinder.checkout.CustomerOrdersScreen(navController = navController)
        }
        composable(Screen.CustomerDashboard.route) {
            com.sjay.cartfinder.checkout.CustomerDashboardScreen(navController = navController)
        }
        composable(
            route = Screen.StallMenu.route,
            arguments = listOf(
                androidx.navigation.navArgument("stallId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("stallName") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
            val stallName = backStackEntry.arguments?.getString("stallName") ?: ""
            com.sjay.cartfinder.checkout.StallMenuScreen(
                stallId = stallId,
                stallName = stallName,
                navController = navController
            )
        }
        composable(Screen.VendorOrders.route) {
            com.sjay.cartfinder.checkout.VendorOrdersScreen(navController = navController)
        }
        composable(
            route = Screen.PayHereSandbox.route,
            arguments = listOf(
                androidx.navigation.navArgument("orderId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("amount") { type = androidx.navigation.NavType.FloatType }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            val amount = backStackEntry.arguments?.getFloat("amount")?.toDouble() ?: 0.0
            
            com.sjay.cartfinder.checkout.PayHereSandboxScreen(
                navController = navController,
                orderId = orderId,
                totalAmount = amount,
                onPaymentSuccess = {
                    navController.navigate(Screen.CustomerOrders.route) {
                        popUpTo(Screen.CustomerDashboard.route) // go back to dashboard context
                    }
                }
            )
        }
    }
}
