package com.sjay.cartfinder.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.data.model.Order
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.ExperimentalMaterialApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CustomerOrdersScreen(
    navController: NavController,
    viewModel: OrderViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val orderState by viewModel.orderState.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
        refreshing = orderState is OrderListState.Loading,
        onRefresh = { if (currentUserId.isNotEmpty()) viewModel.loadCustomerOrders(currentUserId) }
    )

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadCustomerOrders(currentUserId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
        ) {
            when (val state = orderState) {
                is OrderListState.Loading -> {
                    // Handled by PullRefreshIndicator
                }
                is OrderListState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is OrderListState.Success -> {
                    if (state.orders.isEmpty()) {
                        Text("You haven't placed any orders yet.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            items(state.orders) { order ->
                                CustomerOrderCard(order = order)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                else -> {}
            }

            PullRefreshIndicator(
                refreshing = orderState is OrderListState.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun CustomerOrderCard(order: Order) {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(order.createdAt))

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Order Date: $dateString", style = MaterialTheme.typography.bodySmall)
                Text(
                    order.status,
                    fontWeight = FontWeight.Bold,
                    color = if (order.status == "COMPLETED") androidx.compose.ui.graphics.Color(0xFF4CAF50) else PrimaryOrange
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach { item ->
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("${item.quantity}x ${item.productName}")
                    Text("Rs. ${item.price * item.quantity}")
                }
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Total", fontWeight = FontWeight.Bold)
                Text("Rs. ${order.totalAmount}", fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 16.sp)
            }
        }
    }
}
