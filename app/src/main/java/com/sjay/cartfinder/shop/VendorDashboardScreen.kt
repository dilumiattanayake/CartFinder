package com.sjay.cartfinder.shop

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.data.model.Stall

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val shopState by viewModel.shopState.collectAsState()
    val productsState by viewModel.productsState.collectAsState()
    val phiViewModel: com.sjay.cartfinder.phi.PhiViewModel = viewModel()
    val phiState by phiViewModel.phiState.collectAsState()

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadVendorShop(currentUserId)
        }
    }

    Scaffold(
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
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentAlignment = Alignment.TopCenter
            ) {
                when (val state = shopState) {
                    is ShopState.Success -> {
                        if (state.stall == null) {
                            // Vendor doesn't have a shop yet
                            Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
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
                            val stall = state.stall
                            LaunchedEffect(stall.id) {
                                phiViewModel.loadCertificate(stall.id)
                                viewModel.loadProducts(stall.id)
                            }
                            VendorProfileContent(stall, navController, phiState, productsState, viewModel)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun VendorProfileContent(
    stall: Stall,
    navController: NavController,
    phiState: com.sjay.cartfinder.phi.PhiState,
    productsState: ProductsState,
    viewModel: ShopViewModel
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // Cover Image Section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(Color.DarkGray)
        ) {
            if (!stall.imageUrl.isNullOrEmpty()) {
                Image(
                    painter = rememberAsyncImagePainter(stall.imageUrl),
                    contentDescription = "Cover Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Icon(Icons.Filled.Store, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                }
            }
            
            // Top Right Settings & Notifications
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate(Screen.Notifications.route) },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(androidx.compose.material.icons.Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.Black)
                }
                IconButton(
                    onClick = { /* TODO settings */ },
                    modifier = Modifier.background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.Black)
                }
            }

            // Bottom Labels
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .clickable { navController.navigate(Screen.EditShop.route) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color(0xFFF39C12), modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Tap photo to update", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                
                Text(
                    text = "Stall ID: #${stall.id.take(6).uppercase()}",
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFF00B894), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Info Section
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stall.name, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.CheckCircle, contentDescription = "Verified", tint = Color(0xFF27AE60), modifier = Modifier.size(20.dp))
                }
                OutlinedButton(
                    onClick = { navController.navigate(Screen.EditShop.route) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Edit Info", color = Color(0xFFF39C12), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            
            Text(stall.location.address ?: "No Address Provided", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(stall.category, color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            
            Spacer(modifier = Modifier.height(12.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate(Screen.VendorReviews.route) }
                ) {
                    Text(if (stall.ratingCount > 0) String.format(java.util.Locale.US, "%.1f", stall.ratingAverage) else "New", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    if (stall.ratingCount > 0) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (stall.ratingCount > 0) "(${stall.ratingCount} reviews)" else "", color = Color.Gray, fontSize = 14.sp)
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(if (stall.isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), RoundedCornerShape(24.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(if (stall.isOpen) "Open Now" else "Closed", color = if (stall.isOpen) Color(0xFF2E7D32) else Color(0xFFC62828), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Switch(
                        checked = stall.isOpen,
                        onCheckedChange = { viewModel.toggleStallStatus(stall, it) },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }
        }
        
        // PHI Certificate
        if (phiState is com.sjay.cartfinder.phi.PhiState.CertificateData) {
            val cert = phiState.certificate
            if (cert != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { navController.navigate(com.sjay.cartfinder.core.navigation.Screen.PhiCertificate.createRoute(stall.id, stall.name)) },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(48.dp).background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color(0xFF27AE60))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("PHI Certified", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("• Grade ${cert.grade}", color = Color.Gray, fontSize = 12.sp)
                            }
                            Text("Audited by PHI", color = Color.Gray, fontSize = 12.sp)
                        }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("View", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.DarkGray)
                        }
                    }
                }
            }
        }

        // Opening Hours
        Card(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFFF39C12), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("OPENING HOURS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text("Edit Schedule", color = Color(0xFFF39C12), fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.clickable { navController.navigate(Screen.EditShop.route) })
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    val hoursStr = stall.openingHours ?: "No hours specified"
                    if (hoursStr == "No hours specified") {
                        Text(hoursStr, fontSize = 12.sp, color = Color.DarkGray)
                    } else {
                        val hoursList = hoursStr.split("\n")
                        val mid = (hoursList.size + 1) / 2
                        Column(modifier = Modifier.weight(1f)) {
                            hoursList.take(mid).forEach { line ->
                                Text(line.trim(), fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            hoursList.drop(mid).forEach { line ->
                                Text(line.trim(), fontSize = 12.sp, color = Color.DarkGray, modifier = Modifier.padding(vertical = 2.dp))
                            }
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        // Dishes & Menu Items Header
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("DISHES & MENU ITEMS", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text("Manage daily stock and stall visibility", color = Color.Gray, fontSize = 12.sp)
                }
                
                val productCount = if (productsState is ProductsState.Success) productsState.products.size else 0
                Text(
                    text = "$productCount Active Items",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.background(Color(0xFFE5E7EB), RoundedCornerShape(16.dp)).padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            if (productsState is ProductsState.Success) {
                productsState.products.take(2).forEach { product ->
                    VendorProductSummaryCard(product, navController)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (productsState.products.isEmpty()) {
                    Text("No products added yet. Go to Menu to add some.", color = Color.Gray, modifier = Modifier.padding(8.dp))
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun VendorProductSummaryCard(product: MenuItem, navController: NavController) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { navController.navigate(Screen.ProductManagement.route) },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Image Placeholder (or real if available)
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(product.imageUrl),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Filled.RestaurantMenu, contentDescription = null, tint = Color(0xFFF39C12))
                }
                
                // Green dot for availability
                if (product.stockQuantity > 0) {
                    Box(modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp).size(12.dp).background(Color(0xFF27AE60), CircleShape))
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Rs. ${product.price.toInt()}", color = Color(0xFFF39C12), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Row(
                        modifier = Modifier.background(Color(0xFFFFEBEE), RoundedCornerShape(4.dp)).padding(horizontal = 4.dp, vertical = 2.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.Favorite, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(10.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("Local Favorite", color = Color(0xFFE53935), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
                Text(if (product.stockQuantity > 0) "Stock: Plenty" else "Out of stock", color = Color.Gray, fontSize = 12.sp)
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Switch(checked = product.stockQuantity > 0, onCheckedChange = { /* toggle logic */ }, modifier = Modifier.scale(0.8f))
                Row {
                    Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Gray, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
