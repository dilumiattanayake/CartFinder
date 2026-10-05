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
import com.sjay.cartfinder.data.model.Order
import com.sjay.cartfinder.shop.ShopState
import com.sjay.cartfinder.shop.ShopViewModel
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.ExperimentalMaterialApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun VendorOrdersScreen(
    navController: NavController,
    orderViewModel: OrderViewModel = viewModel(),
    shopViewModel: ShopViewModel = viewModel()
) {
    val shopState by shopViewModel.shopState.collectAsState()
    val orderState by orderViewModel.orderState.collectAsState()
    
    val stallId = (shopState as? ShopState.Success)?.stall?.id

    val pullRefreshState = rememberPullRefreshState(
        refreshing = orderState is OrderListState.Loading,
        onRefresh = { if (stallId != null) orderViewModel.loadVendorOrders(stallId) }
    )

    LaunchedEffect(stallId) {
        if (stallId != null) {
            orderViewModel.loadVendorOrders(stallId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Incoming Orders") },
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
                        Text("No incoming orders yet.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            items(state.orders) { order ->
                                VendorOrderCard(
                                    order = order,
                                    onUpdateStatus = { newStatus -> 
                                        if (stallId != null) orderViewModel.updateOrderStatus(order.id, newStatus, stallId)
                                    }
                                )
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
fun VendorOrderCard(order: Order, onUpdateStatus: (String) -> Unit) {
    val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val dateString = formatter.format(Date(order.createdAt))
    var expanded by remember { mutableStateOf(false) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Order ID: ${order.id.takeLast(6).uppercase()}", fontWeight = FontWeight.Bold)
                Text(order.status, fontWeight = FontWeight.Bold, color = PrimaryOrange)
            }
            Text(dateString, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            order.items.forEach { item ->
                Text("${item.quantity}x ${item.productName}")
            }
            Divider(modifier = Modifier.padding(vertical = 8.dp))
            Text("Total: Rs. ${order.totalAmount}", fontWeight = FontWeight.Bold, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons to progress status
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                when (order.status) {
                    "PENDING" -> {
                        Button(onClick = { onUpdateStatus("PREPARING") }) { Text("Accept & Prepare") }
                        OutlinedButton(onClick = { onUpdateStatus("CANCELLED") }) { Text("Cancel") }
                    }
                    "PREPARING" -> {
                        Button(onClick = { onUpdateStatus("READY") }) { Text("Mark as Ready") }
                    }
                    "READY" -> {
                        Button(onClick = { onUpdateStatus("COMPLETED") }) { Text("Complete Order") }
                    }
                }
            }
        }
    }
}
