package com.sjay.cartfinder.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    // Customer
    object CustomerHome : BottomNavItem("Home", Icons.Filled.Home, Screen.CustomerDashboard.route)
    object CustomerMap : BottomNavItem("Map", Icons.Filled.Map, Screen.CustomerMap.route)
    object CustomerCart : BottomNavItem("Cart", Icons.Filled.ShoppingCart, Screen.Cart.route)
    object CustomerOrders : BottomNavItem("Orders", Icons.Filled.List, Screen.CustomerOrders.route)

    // Vendor
    object VendorHome : BottomNavItem("Dashboard", Icons.Filled.Home, Screen.VendorDashboard.route)
    object VendorProducts : BottomNavItem("Products", Icons.Filled.Store, Screen.ProductManagement.route)
    object VendorOrders : BottomNavItem("Orders", Icons.Filled.List, Screen.VendorOrders.route)

    // PHI
    object PhiAlerts : BottomNavItem("Alerts", Icons.Filled.List, Screen.PhiAlerts.route)
    object PhiHome : BottomNavItem("Stalls", Icons.Filled.Store, Screen.PhiDashboard.route)
    object PhiSpotAudit : BottomNavItem("Spot Audit", Icons.Filled.List, Screen.PhiSpotAudit.createRoute()) // No dummy argument needed

    // Shared
    class Settings(role: String) : BottomNavItem("Profile", Icons.Filled.Settings, Screen.Settings.createRoute(role))
}

@Composable
fun BottomNavigationBar(navController: NavController, role: String) {
    val items = when (role.lowercase()) {
        "customer" -> listOf(
            BottomNavItem.CustomerHome,
            BottomNavItem.CustomerMap,
            BottomNavItem.CustomerCart,
            BottomNavItem.CustomerOrders,
            BottomNavItem.Settings("customer")
        )
        "vendor" -> listOf(
            BottomNavItem.VendorHome,
            BottomNavItem.VendorProducts,
            BottomNavItem.VendorOrders,
            BottomNavItem.Settings("vendor")
        )
        "phi" -> listOf(
            BottomNavItem.PhiAlerts,
            BottomNavItem.PhiHome,
            BottomNavItem.PhiSpotAudit,
            BottomNavItem.Settings("phi")
        )
        else -> emptyList()
    }

    if (items.isEmpty()) return

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            val isSelected = currentRoute == item.route || (item is BottomNavItem.Settings && currentRoute?.startsWith("settings") == true)
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                label = { Text(text = item.title) },
                alwaysShowLabel = true,
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                }
            )
        }
    }
}
