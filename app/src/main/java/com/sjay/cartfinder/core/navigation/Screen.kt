package com.sjay.cartfinder.core.navigation

sealed class Screen(val route: String) {
    object Launch : Screen("launch")
    object Onboarding : Screen("onboarding")
    object ChooseRole : Screen("choose_role")
    object Login : Screen("login/{role}") {
        fun createRoute(role: String) = "login/$role"
    }
    object SignUp : Screen("sign_up/{role}") {
        fun createRoute(role: String) = "sign_up/$role"
    }
    object Settings : Screen("settings")
    object Profile : Screen("profile")
    
    // Member 1 - Reviews
    object ReviewList : Screen("review_list")
    object SubmitReview : Screen("submit_review")

    // Member 2 - Shop & Products
    object VendorDashboard : Screen("vendor_dashboard")
    object EditShop : Screen("edit_shop")
    object MapPicker : Screen("map_picker")
    object ProductManagement : Screen("product_management")

    // Member 3 - Cart & Checkout
    object Cart : Screen("cart")
    object CustomerOrders : Screen("customer_orders")
    object VendorOrders : Screen("vendor_orders")
}
