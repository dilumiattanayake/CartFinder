package com.sjay.cartfinder.checkout

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.CartItem
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StallMenuScreen(
    stallId: String,
    stallName: String,
    navController: NavController,
    dashboardViewModel: CustomerDashboardViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val menuState by dashboardViewModel.menuState.collectAsState()
    val cartState by cartViewModel.cartState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val pullRefreshState = androidx.compose.material.pullrefresh.rememberPullRefreshState(
        refreshing = menuState is StallMenuState.Loading,
        onRefresh = { dashboardViewModel.loadStallMenu(stallId) }
    )

    LaunchedEffect(stallId) {
        dashboardViewModel.loadStallMenu(stallId)
    }

    LaunchedEffect(cartState) {
        if (cartState is CartState.Error) {
            Toast.makeText(context, (cartState as CartState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stallName, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.Cart.route) }) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "My Cart")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .androidx.compose.material.pullrefresh.pullRefresh(pullRefreshState)
        ) {
            when (val state = menuState) {
                is StallMenuState.Loading -> {
                    // Handled by PullRefreshIndicator
                }
                is StallMenuState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is StallMenuState.Success -> {
                    if (state.items.isEmpty()) {
                        Text("This stall has no products available yet.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        ) {
                            items(state.items) { menuItem ->
                                MenuItemCard(menuItem = menuItem) {
                                    if (currentUserId.isNotEmpty()) {
                                        val cartItem = CartItem(
                                            productId = menuItem.id,
                                            productName = menuItem.name,
                                            price = menuItem.price,
                                            quantity = 1,
                                            stallId = stallId
                                        )
                                        cartViewModel.addItemToCart(currentUserId, cartItem)
                                        Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                else -> {}
            }

            androidx.compose.material.pullrefresh.PullRefreshIndicator(
                refreshing = menuState is StallMenuState.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun MenuItemCard(menuItem: MenuItem, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(menuItem.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(menuItem.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                Text("Rs. ${menuItem.price}", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
            Button(
                onClick = onAddToCart,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Add")
            }
        }
    }
}
