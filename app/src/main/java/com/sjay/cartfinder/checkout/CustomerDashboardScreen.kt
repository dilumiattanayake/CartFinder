package com.sjay.cartfinder.checkout

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
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
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.ExperimentalMaterialApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CustomerDashboardScreen(
    navController: NavController,
    viewModel: CustomerDashboardViewModel = viewModel()
) {
    val stallsState by viewModel.stallsState.collectAsState()

    val pullRefreshState = rememberPullRefreshState(
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
        },
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "customer")
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .pullRefresh(pullRefreshState)
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

            PullRefreshIndicator(
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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text(stall.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                // Fetch Certificate specifically for this card
                val phiViewModel: com.sjay.cartfinder.phi.PhiViewModel = viewModel(key = stall.id)
                val phiState by phiViewModel.phiState.collectAsState()
                
                LaunchedEffect(stall.id) {
                    phiViewModel.loadCertificate(stall.id)
                }
                
                var certScore: String? = null
                if (phiState is com.sjay.cartfinder.phi.PhiState.CertificateData) {
                    val cert = (phiState as com.sjay.cartfinder.phi.PhiState.CertificateData).certificate
                    if (cert != null && cert.status == "ACTIVE") {
                        certScore = "Grade ${cert.grade} (${cert.score})"
                    }
                }
                
                if (certScore != null) {
                    Badge(containerColor = androidx.compose.ui.graphics.Color(0xFF27AE60)) {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = androidx.compose.ui.graphics.Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(certScore!!, color = androidx.compose.ui.graphics.Color.White)
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(stall.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
