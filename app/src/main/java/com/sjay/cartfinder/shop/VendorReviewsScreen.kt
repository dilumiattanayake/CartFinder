package com.sjay.cartfinder.shop

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.reviews.ReviewListScreen
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import com.sjay.cartfinder.shop.ShopViewModel
import com.sjay.cartfinder.shop.ShopState
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.LaunchedEffect

@Composable
fun VendorReviewsScreen(
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val shopState by viewModel.shopState.collectAsState()

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadVendorShop(currentUserId)
        }
    }

    when (val state = shopState) {
        is ShopState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = PrimaryOrange)
            }
        }
        is ShopState.Success -> {
            val stall = state.stall
            if (stall != null) {
                ReviewListScreen(
                    stallId = stall.id,
                    stallName = stall.name,
                    navController = navController,
                    role = "Vendor"
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Store not found.")
                }
            }
        }
        is ShopState.Error -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error loading store details.")
            }
        }
        else -> {
            // Do nothing for Idle or StallsList in this context
        }
    }
}
