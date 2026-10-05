package com.sjay.cartfinder.phi

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.sjay.cartfinder.shop.ShopState
import com.sjay.cartfinder.shop.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhiDashboardScreen(
    navController: NavController,
    shopViewModel: ShopViewModel = viewModel()
) {
    val stallsState by shopViewModel.allStallsState.collectAsState()

    LaunchedEffect(Unit) {
        shopViewModel.loadAllStalls()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PHI Dashboard (Stall Directory)", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "phi")
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (val state = stallsState) {
                is ShopState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is ShopState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is ShopState.StallsList -> {
                    val stalls = state.stalls
                    if (stalls.isEmpty()) {
                        Text("No active stalls found.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            items(stalls) { stall ->
                                StallDirectoryItem(
                                    stall = stall,
                                    onClick = {
                                        navController.navigate("phi_stall_details/${stall.id}")
                                    }
                                )
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
fun StallDirectoryItem(stall: Stall, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stall.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Category: ${stall.category}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
