package com.sjay.cartfinder.checkout

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.CartItem
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StallMenuScreen(
    stallId: String,
    stallName: String,
    distance: Double? = null,
    navController: NavController,
    dashboardViewModel: CustomerDashboardViewModel = viewModel(),
    cartViewModel: CartViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val menuState by dashboardViewModel.menuState.collectAsState()
    val stallsState by dashboardViewModel.stallsState.collectAsState()
    val cartState by cartViewModel.cartState.collectAsState()
    val context = LocalContext.current

    var currentStall by remember { mutableStateOf<Stall?>(null) }
    
    // Fallback to state if fresh load takes time
    val cachedStall = (stallsState as? StallListState.Success)?.stalls?.find { it.id == stallId }
    val stall = currentStall ?: cachedStall

    LaunchedEffect(stallId) {
        dashboardViewModel.loadStallMenu(stallId)
        currentStall = dashboardViewModel.getStallById(stallId)
        if (dashboardViewModel.stallsState.value !is StallListState.Success) {
            dashboardViewModel.loadAllStalls()
        }
    }

    LaunchedEffect(cartState) {
        if (cartState is CartState.Error) {
            Toast.makeText(context, (cartState as CartState.Error).message, Toast.LENGTH_SHORT).show()
        }
    }

    Scaffold(
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "customer")
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Header Image with Overlay Buttons
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(Color.DarkGray)
            ) {
                if (stall?.imageUrl != null) {
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
                
                // Back Button
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
                }
                
                // Cart Button
                IconButton(
                    onClick = { navController.navigate(Screen.Cart.route) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(Icons.Filled.ShoppingCart, contentDescription = "Cart", tint = Color.Black)
                }
            }

            // Stall Info
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(stall?.name ?: stallName, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
                            
                            val phiViewModel: com.sjay.cartfinder.phi.PhiViewModel = viewModel()
                            val phiState by phiViewModel.phiState.collectAsState()
                            
                            LaunchedEffect(stallId) {
                                phiViewModel.loadCertificate(stallId)
                            }
                            
                            if (phiState is com.sjay.cartfinder.phi.PhiState.CertificateData) {
                                val cert = (phiState as com.sjay.cartfinder.phi.PhiState.CertificateData).certificate
                                if (cert != null && cert.status == "ACTIVE") {
                                    Row(
                                        modifier = Modifier.background(Color(0xFFE8F5E9), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Grade ${cert.grade} (${cert.score})", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(stall?.location?.address ?: "No Address Provided", color = Color.Gray, fontSize = 14.sp)
                            if (distance != null) {
                                Text(
                                    text = String.format(java.util.Locale.US, " • %.1f km", distance),
                                    color = PrimaryOrange,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                        Text(stall?.category ?: "Food Stall", color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Rating & Open Status
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("4.5", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("120+ ratings", color = Color.Gray, fontSize = 14.sp)
                            }
                            
                            val isOpen = stall?.isOpen ?: true
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(if (isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE), RoundedCornerShape(24.dp))
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(if (isOpen) "Open Now" else "Closed", color = if (isOpen) Color(0xFF2E7D32) else Color(0xFFC62828), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                    
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                    
                    Text("MENU ITEMS", fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
                }

                when (val state = menuState) {
                    is StallMenuState.Loading -> {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = PrimaryOrange)
                            }
                        }
                    }
                    is StallMenuState.Error -> {
                        item {
                            Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
                        }
                    }
                    is StallMenuState.Success -> {
                        if (state.items.isEmpty()) {
                            item {
                                Text("This stall has no products available yet.", modifier = Modifier.padding(16.dp), color = Color.Gray)
                            }
                        } else {
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
                    else -> {}
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(menuItem: MenuItem, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                if (menuItem.imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(menuItem.imageUrl),
                        contentDescription = menuItem.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Filled.RestaurantMenu, contentDescription = null, tint = PrimaryOrange)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(menuItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(menuItem.description, color = Color.Gray, fontSize = 12.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Rs. ${menuItem.price}", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text("Add", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
