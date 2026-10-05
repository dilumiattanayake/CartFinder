package com.sjay.cartfinder.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Security
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
        },
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "vendor")
        }
    ) { padding ->
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = shopState is ShopState.Loading,
            onRefresh = { if (currentUserId.isNotEmpty()) viewModel.loadVendorShop(currentUserId) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when (val state = shopState) {
                    is ShopState.Loading -> {
                        // Managed by PullToRefreshBox
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
                            rememberScrollState().let { scrollState ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Spacer(modifier = Modifier.height(32.dp))
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
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                    ) {
                                        Icon(Icons.Filled.ShoppingCart, contentDescription = null)
                                        Spacer(Modifier.width(8.dp))
                                        Text("Manage Products")
                                    }

                                    Button(
                                        onClick = { navController.navigate(Screen.VendorOrders.route) },
                                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                                    ) {
                                        Icon(Icons.Filled.ShoppingCart, contentDescription = null) // Replace with better icon if needed
                                        Spacer(Modifier.width(8.dp))
                                        Text("View Incoming Orders")
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Divider()
                                    Spacer(modifier = Modifier.height(16.dp))
                                    
                                    val phiViewModel: com.sjay.cartfinder.phi.PhiViewModel = viewModel()
                                    val phiState by phiViewModel.phiState.collectAsState()
                                    
                                    LaunchedEffect(stall.id) {
                                        phiViewModel.loadCertificate(stall.id)
                                    }
                                    
                                    Text("PHI Certificate Status", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    when (val pState = phiState) {
                                        is com.sjay.cartfinder.phi.PhiState.CertificateData -> {
                                            if (pState.certificate != null) {
                                                val cert = pState.certificate
                                                val bgColor = if (cert.status == "ACTIVE") androidx.compose.ui.graphics.Color(0xFFD1FAE5) else if (cert.status == "PENDING_REQUEST") androidx.compose.ui.graphics.Color(0xFFFEF3C7) else androidx.compose.ui.graphics.Color(0xFFFEE2E2)
                                                val contentColor = if (cert.status == "ACTIVE") androidx.compose.ui.graphics.Color(0xFF065F46) else if (cert.status == "PENDING_REQUEST") androidx.compose.ui.graphics.Color(0xFF92400E) else androidx.compose.ui.graphics.Color(0xFF991B1B)
                                                
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                                    colors = CardDefaults.cardColors(containerColor = bgColor)
                                                ) {
                                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(
                                                            if (cert.status == "ACTIVE") Icons.Filled.VerifiedUser else Icons.Filled.Security,
                                                            contentDescription = null,
                                                            tint = contentColor,
                                                            modifier = Modifier.size(48.dp)
                                                        )
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text("Status: ${cert.status}", fontWeight = FontWeight.Bold, color = contentColor)
                                                        if (cert.status == "ACTIVE") {
                                                            Text("Grade: ${cert.grade} (Score: ${cert.score}/100)", color = contentColor)
                                                        }
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Button(
                                                            onClick = { navController.navigate(Screen.PhiCertificate.createRoute(stall.id, stall.name)) },
                                                            colors = ButtonDefaults.buttonColors(containerColor = contentColor)
                                                        ) {
                                                            Text("View / Download Full Certificate")
                                                        }
                                                    }
                                                }
                                            } else {
                                                Card(
                                                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color(0xFFF3F4F6))
                                                ) {
                                                    Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                                                        Icon(Icons.Filled.Security, contentDescription = null, tint = androidx.compose.ui.graphics.Color.Gray, modifier = Modifier.size(48.dp))
                                                        Spacer(modifier = Modifier.height(8.dp))
                                                        Text("No Certificate Found", color = androidx.compose.ui.graphics.Color.DarkGray)
                                                        Spacer(modifier = Modifier.height(12.dp))
                                                        Button(onClick = { phiViewModel.requestCertificate(stall.id) }) {
                                                            Text("Request PHI Certificate")
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        is com.sjay.cartfinder.phi.PhiState.Loading -> {
                                            CircularProgressIndicator()
                                        }
                                        else -> {
                                            Text("Loading PHI status...")
                                        }
                                    }
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}
