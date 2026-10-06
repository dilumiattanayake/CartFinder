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
    cartViewModel: CartViewModel = viewModel(),
    reviewViewModel: com.sjay.cartfinder.reviews.ReviewViewModel = viewModel()
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
        reviewViewModel.getReviewsForStall(stallId)
        if (dashboardViewModel.stallsState.value !is StallListState.Success) {
            dashboardViewModel.loadAllStalls()
        }
        if (currentUserId.isNotEmpty()) {
            cartViewModel.loadCart(currentUserId, stallId)
        }
    }
    
    val reviewsState by reviewViewModel.reviewsState.collectAsState()
    var averageRating by remember { mutableDoubleStateOf(0.0) }
    var reviewCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(reviewsState) {
        if (reviewsState is com.sjay.cartfinder.reviews.ReviewState.Success) {
            val reviews = (reviewsState as com.sjay.cartfinder.reviews.ReviewState.Success).reviews
            reviewCount = reviews.size
            if (reviewCount > 0) {
                averageRating = reviews.map { it.rating }.average()
            } else {
                averageRating = 0.0
            }
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
                if (!stall?.imageUrl.isNullOrEmpty()) {
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
                    onClick = { navController.navigate(Screen.CartDetail.createRoute(stallId)) },
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
                        
                        val context = androidx.compose.ui.platform.LocalContext.current
                        if (stall != null && stall.location.latitude != 0.0 && stall.location.longitude != 0.0) {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = {
                                    val intent = android.content.Intent(
                                        android.content.Intent.ACTION_VIEW, 
                                        android.net.Uri.parse("google.navigation:q=${stall.location.latitude},${stall.location.longitude}")
                                    )
                                    intent.setPackage("com.google.android.apps.maps")
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryOrange)
                            ) {
                                Icon(androidx.compose.material.icons.Icons.Filled.Navigation, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Navigate to Shop (Live Sync)")
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        // Rating & Open Status
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { navController.navigate(Screen.ReviewList.createRoute(stallId, stall?.name ?: stallName)) }
                                    .padding(4.dp)
                            ) {
                                Text(if (reviewCount > 0) String.format("%.1f", averageRating) else "New", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                if (reviewCount > 0) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("($reviewCount reviews) See Reviews", color = PrimaryOrange, fontSize = 14.sp, fontWeight = FontWeight.Medium)
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
                            val groupedItems = state.items.groupBy { it.categoryId.ifEmpty { "Other" } }
                            groupedItems.forEach { (category, items) ->
                                item {
                                    Text(
                                        text = category.uppercase(),
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 18.sp,
                                        color = PrimaryOrange,
                                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                                    )
                                }
                                item {
                                    androidx.compose.foundation.lazy.LazyRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentPadding = PaddingValues(horizontal = 16.dp),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        items(items) { menuItem ->
                                            MenuItemCard(menuItem = menuItem) {
                                                if (currentUserId.isNotEmpty()) {
                                                    val cartItem = CartItem(
                                                        productId = menuItem.id,
                                                        productName = menuItem.name,
                                                        price = menuItem.price,
                                                        quantity = 1,
                                                        stallId = stallId,
                                                        preparationTime = menuItem.preparationTime
                                                    )
                                                    cartViewModel.addItemToCart(currentUserId, cartItem, stall?.name ?: stallName)
                                                    Toast.makeText(context, "Added to cart", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(16.dp))
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

@Composable
fun MenuItemCard(menuItem: MenuItem, onAddToCart: () -> Unit) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.DarkGray else Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                if (!menuItem.imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(menuItem.imageUrl),
                        contentDescription = menuItem.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Filled.RestaurantMenu, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(48.dp))
                }
            }
            
            Column(modifier = Modifier.padding(12.dp)) {
                Text(menuItem.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                Text(menuItem.description, color = Color.Gray, fontSize = 12.sp, maxLines = 2, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Text("Rs. ${menuItem.price}", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("• ${menuItem.preparationTime} mins", color = Color.Gray, fontSize = 12.sp)
                    }
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
}
