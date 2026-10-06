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
        composable(
            route = Screen.Settings.route,
            arguments = listOf(androidx.navigation.navArgument("role") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "customer"
            SettingsScreen(navController = navController, role = role)
        }
        composable(Screen.Profile.route) {
            ProfileScreen(navController = navController)
        }
        composable(
            route = Screen.ReviewList.route,
            arguments = listOf(
                androidx.navigation.navArgument("stallId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("stallName") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
            val stallName = backStackEntry.arguments?.getString("stallName") ?: ""
            ReviewListScreen(navController = navController, stallId = stallId, stallName = stallName)
        }
        composable(
            route = Screen.SubmitReview.route,
            arguments = listOf(
                androidx.navigation.navArgument("stallId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("stallName") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
            val stallName = backStackEntry.arguments?.getString("stallName") ?: ""
            SubmitReviewScreen(navController = navController, stallId = stallId, stallName = stallName)
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
        composable(Screen.VendorReviews.route) {
            com.sjay.cartfinder.shop.VendorReviewsScreen(navController = navController)
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
        composable(Screen.CustomerMap.route) {
            com.sjay.cartfinder.checkout.CustomerMapScreen(navController = navController)
        }
        composable(
            route = Screen.StallMenu.route,
            arguments = listOf(
                androidx.navigation.navArgument("stallId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("stallName") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("distance") { 
                    type = androidx.navigation.NavType.FloatType
                    defaultValue = -1f 
                }
            )
        ) { backStackEntry ->
            val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
            val stallName = backStackEntry.arguments?.getString("stallName") ?: ""
            val distanceFloat = backStackEntry.arguments?.getFloat("distance") ?: -1f
            val distance = if (distanceFloat >= 0f) distanceFloat.toDouble() else null
            com.sjay.cartfinder.checkout.StallMenuScreen(
                stallId = stallId,
                stallName = stallName,
                distance = distance,
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
        composable(Screen.PhiDashboard.route) {
            com.sjay.cartfinder.phi.PhiDashboardScreen(navController = navController)
        }
        composable(
            route = Screen.StallPhiDetails.route,
            arguments = listOf(androidx.navigation.navArgument("stallId") { type = androidx.navigation.NavType.StringType })
        ) { backStackEntry ->
            val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
            com.sjay.cartfinder.phi.StallPhiDetailsScreen(
                navController = navController,
                stallId = stallId
            )
        }
        composable(Screen.PhiAlerts.route) {
            com.sjay.cartfinder.phi.PhiAlertsScreen(navController = navController)
        }
        composable(
            route = Screen.PhiSpotAudit.route,
            arguments = listOf(androidx.navigation.navArgument("stallId") {
                type = androidx.navigation.NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
            com.sjay.cartfinder.phi.PhiSpotAuditScreen(
                navController = navController,
                stallId = stallId
            )
        }
        composable(
            route = Screen.PhiCertificate.route,
            arguments = listOf(
                androidx.navigation.navArgument("stallId") { type = androidx.navigation.NavType.StringType },
                androidx.navigation.navArgument("stallName") { type = androidx.navigation.NavType.StringType }
            )
        ) { backStackEntry ->
            val stallId = backStackEntry.arguments?.getString("stallId") ?: ""
            val stallName = backStackEntry.arguments?.getString("stallName") ?: ""
            com.sjay.cartfinder.phi.PhiCertificateScreen(
                navController = navController,
                stallId = stallId,
                stallName = stallName
            )
        }
    }
}
