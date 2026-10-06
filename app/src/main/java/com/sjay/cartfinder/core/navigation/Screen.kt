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
    object Settings : Screen("settings/{role}") {
        fun createRoute(role: String) = "settings/$role"
    }
    object Profile : Screen("profile")
    object Notifications : Screen("notifications")
    
    // Member 1 - Reviews
    object ReviewList : Screen("review_list/{stallId}/{stallName}") {
        fun createRoute(stallId: String, stallName: String) = "review_list/$stallId/$stallName"
    }
    object SubmitReview : Screen("submit_review/{stallId}/{stallName}") {
        fun createRoute(stallId: String, stallName: String) = "submit_review/$stallId/$stallName"
    }

    // Member 2 - Shop & Products
    object VendorDashboard : Screen("vendor_dashboard")
    object EditShop : Screen("edit_shop")
    object MapPicker : Screen("map_picker")
    object ProductManagement : Screen("product_management")
    object VendorReviews : Screen("vendor_reviews")

    // Member 3 - Cart & Checkout & Browsing Stalls
    object CustomerDashboard : Screen("customer_dashboard")
    object CustomerMap : Screen("customer_map")
    object StallMenu : Screen("stall_menu/{stallId}/{stallName}?distance={distance}") {
        fun createRoute(stallId: String, stallName: String, distance: Double? = null): String {
            return if (distance != null) "stall_menu/$stallId/$stallName?distance=$distance" else "stall_menu/$stallId/$stallName"
        }
    }
    object Cart : Screen("cart")
    object CustomerOrders : Screen("customer_orders")
    object VendorOrders : Screen("vendor_orders")
    object PayHereSandbox : Screen("payhere_sandbox/{orderId}/{amount}") {
        fun createRoute(orderId: String, amount: Double) = "payhere_sandbox/$orderId/$amount"
    }

    // Member 4 - PHI
    object PhiDashboard : Screen("phi_dashboard")
    object PhiAlerts : Screen("phi_alerts")
    object PhiSpotAudit : Screen("phi_spot_audit?stallId={stallId}") {
        fun createRoute(stallId: String? = null) = if (stallId != null) "phi_spot_audit?stallId=$stallId" else "phi_spot_audit"
    }
    object PhiCertificate : Screen("phi_certificate/{stallId}/{stallName}") {
        fun createRoute(stallId: String, stallName: String) = "phi_certificate/$stallId/$stallName"
    }
    object StallPhiDetails : Screen("phi_stall_details/{stallId}") {
        fun createRoute(stallId: String) = "phi_stall_details/$stallId"
    }
}
