package com.sjay.cartfinder.checkout

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.sjay.cartfinder.data.model.CartItem
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val cartState by viewModel.cartState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadCart(currentUserId)
        }
    }

    LaunchedEffect(cartState) {
        if (cartState is CartState.CheckoutSuccess) {
            Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_SHORT).show()
            // Optional: navigate to Customer Orders screen instead of popping back
            navController.popBackStack() 
        } else if (cartState is CartState.Error) {
            val message = (cartState as CartState.Error).message
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (cartState is CartState.Success && (cartState as CartState.Success).cart.items.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearCart(currentUserId) }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Clear Cart", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (cartState is CartState.Success) {
                val items = (cartState as CartState.Success).cart.items
                if (items.isNotEmpty()) {
                    val total = items.sumOf { it.price * it.quantity }
                    Surface(
                        shadowElevation = 8.dp,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total", fontWeight = FontWeight.SemiBold)
                                Text("Rs. $total", fontWeight = FontWeight.Bold, fontSize = 20.sp, color = PrimaryOrange)
                            }
                            Button(
                                onClick = { viewModel.checkout(currentUserId) },
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                            ) {
                                Text("Checkout", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
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
                                    onIncrease = { viewModel.updateQuantity(currentUserId, item.id, item.quantity + 1) },
                                    onDecrease = { viewModel.updateQuantity(currentUserId, item.id, item.quantity - 1) }
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
