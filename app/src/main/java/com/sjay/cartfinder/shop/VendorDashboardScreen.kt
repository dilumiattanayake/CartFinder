package com.sjay.cartfinder.shop

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor Dashboard", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when (val state = shopState) {
                is ShopState.Loading -> {
                    CircularProgressIndicator()
                }
                is ShopState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error)
                }
                is ShopState.Success -> {
                    if (state.stall == null) {
                        // Vendor doesn't have a shop yet
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "You haven't set up your stall yet.",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(bottom = 16.dp)
                            )
                            Button(onClick = { navController.navigate(Screen.EditShop.route) }) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Create Stall")
                            }
                        }
                    } else {
                        // Vendor has a shop
                        val stall = state.stall
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = stall.name,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = stall.description,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(vertical = 8.dp)
                            )
                            
                            Spacer(modifier = Modifier.height(32.dp))

                            Button(
                                onClick = { navController.navigate(Screen.EditShop.route) },
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                            ) {
                                Icon(Icons.Filled.Edit, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Edit Stall Details & Location")
                            }

                            Button(
                                onClick = { navController.navigate(Screen.ProductManagement.route) },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Manage Products")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}
