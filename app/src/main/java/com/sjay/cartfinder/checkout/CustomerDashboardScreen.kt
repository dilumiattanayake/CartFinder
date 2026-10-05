package com.sjay.cartfinder.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboardScreen(
    navController: NavController,
    viewModel: CustomerDashboardViewModel = viewModel()
) {
    val stallsState by viewModel.stallsState.collectAsState()

    val pullRefreshState = androidx.compose.material.pullrefresh.rememberPullRefreshState(
        refreshing = stallsState is StallListState.Loading,
        onRefresh = { viewModel.loadAllStalls() }
    )

    LaunchedEffect(Unit) {
        viewModel.loadAllStalls()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Food Stalls", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { navController.navigate(Screen.CustomerOrders.route) }) {
                        Icon(Icons.Filled.List, contentDescription = "My Orders")
                    }
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
            when (val state = stallsState) {
                is StallListState.Loading -> {
                    // Handled by PullRefreshIndicator
                }
                is StallListState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is StallListState.Success -> {
                    if (state.stalls.isEmpty()) {
                        Text("No active stalls found nearby.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(16.dp)
                        ) {
                            items(state.stalls) { stall ->
                                StallCard(stall = stall) {
                                    navController.navigate("stall_menu/${stall.id}/${stall.name}")
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                        }
                    }
                }
                else -> {}
            }

            androidx.compose.material.pullrefresh.PullRefreshIndicator(
                refreshing = stallsState is StallListState.Loading,
                state = pullRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}

@Composable
fun StallCard(stall: Stall, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stall.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stall.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
