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
}
