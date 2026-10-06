package com.sjay.cartfinder.checkout

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import com.sjay.cartfinder.data.model.Cart
import com.sjay.cartfinder.data.model.CartItem
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import com.sjay.cartfinder.core.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartsListScreen(
    navController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val cartListState by viewModel.cartListState.collectAsState()
    
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadAllCarts(currentUserId)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Carts") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "customer")
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = cartListState) {
                is CartListState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CartListState.Success -> {
                    val activeCarts = state.carts.filter { it.items.isNotEmpty() }
                    if (activeCarts.isEmpty()) {
                        Text(
                            "You have no active carts.",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        ) {
                            items(activeCarts) { cart ->
                                CartShopCard(
                                    cart = cart,
                                    onClick = {
                                        navController.navigate(Screen.CartDetail.createRoute(cart.stallId))
                                    }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartShopCard(cart: Cart, onClick: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Text(if (cart.stallName.isNotEmpty()) cart.stallName else "Shop ID: ${cart.stallId.takeLast(6).uppercase()}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val totalItems = cart.items.sumOf { it.quantity }
            val totalAmount = cart.items.sumOf { it.price * it.quantity }
            Text("$totalItems items", style = MaterialTheme.typography.bodyMedium)
            Text("Total: Rs. $totalAmount", color = PrimaryOrange, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartDetailScreen(
    navController: NavController,
    stallId: String,
    viewModel: CartViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val cartState by viewModel.cartState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(currentUserId, stallId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadCart(currentUserId, stallId)
        }
    }

    LaunchedEffect(cartState) {
        if (cartState is CartState.CheckoutSuccess) {
            val successState = cartState as CartState.CheckoutSuccess
            Toast.makeText(context, "Order created. Proceeding to payment...", Toast.LENGTH_SHORT).show()
            navController.navigate(Screen.PayHereSandbox.createRoute(successState.orderId, successState.totalAmount)) {
                popUpTo(Screen.CustomerDashboard.route)
            }
        } else if (cartState is CartState.Error) {
            val message = (cartState as CartState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    val cart = (cartState as? CartState.Success)?.cart
                    val name = if (cart != null && cart.stallName.isNotEmpty()) cart.stallName else "Checkout"
                    Text(name)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (cartState is CartState.Success && (cartState as CartState.Success).cart.items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart(currentUserId, stallId) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Clear Cart", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            Column {
                if (cartState is CartState.Success) {
                    val items = (cartState as CartState.Success).cart.items
                if (items.isNotEmpty()) {
                    val formatter = java.text.SimpleDateFormat("hh:mm a", java.util.Locale.US)
                    val pickupSlots = remember(items) {
                        val currentTime = System.currentTimeMillis()
                        val totalPrepTimeMins = items.sumOf { it.preparationTime * it.quantity }
                        // Add some buffer (e.g. 5 mins for processing) if we want, but let's just use exact prep time
                        val earliestPickupTime = currentTime + (totalPrepTimeMins * 60 * 1000L)
                        
                        (0..3).map { i ->
                            val start = earliestPickupTime + (i * 15 * 60 * 1000L)
                            val end = start + (15 * 60 * 1000L)
                            "${formatter.format(java.util.Date(start))} - ${formatter.format(java.util.Date(end))}"
                        }
                    }
                    var selectedPickupSlot by remember(pickupSlots) { mutableStateOf(pickupSlots.first()) }
                    var expanded by remember { mutableStateOf(false) }

                    val total = items.sumOf { it.price * it.quantity }
                    Surface(
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                            // Pickup Slot Selector
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Pickup Time:", fontWeight = FontWeight.SemiBold)
                                Box {
                                    TextButton(onClick = { expanded = true }) {
                                        Text(selectedPickupSlot, color = PrimaryOrange, fontWeight = FontWeight.Bold)
                                    }
                                    DropdownMenu(
                                        expanded = expanded,
                                        onDismissRequest = { expanded = false }
                                    ) {
                                        pickupSlots.forEach { slot ->
                                            DropdownMenuItem(
                                                text = { Text(slot) },
                                                onClick = {
                                                    selectedPickupSlot = slot
                                                    expanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Total", fontWeight = FontWeight.SemiBold)
                                    Text("Rs. $total", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryOrange)
                                }
                                Button(
                                    onClick = { viewModel.checkout(currentUserId, selectedPickupSlot) },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                                ) {
                                    Text("Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "customer")
        }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = cartState) {
                is CartState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is CartState.Success -> {
                    if (state.cart.items.isEmpty()) {
                        Text(
                            "Your cart is empty.",
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            contentPadding = PaddingValues(bottom = 80.dp) // Leave space for bottom bar
                        ) {
                            items(state.cart.items) { item ->
                                CartItemCard(
                                    item = item,
                                    onIncrease = { viewModel.updateQuantity(currentUserId, stallId, item.id, item.quantity + 1) },
                                    onDecrease = { viewModel.updateQuantity(currentUserId, stallId, item.id, item.quantity - 1) }
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun CartItemCard(
    item: CartItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.productName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Rs. ${item.price}", color = PrimaryOrange, fontWeight = FontWeight.SemiBold)
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDecrease) {
                    Text("-", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                Text("${item.quantity}", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(horizontal = 8.dp))
                IconButton(onClick = onIncrease) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
