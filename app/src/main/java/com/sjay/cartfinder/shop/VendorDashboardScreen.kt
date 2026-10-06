package com.sjay.cartfinder.shop

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.data.repository.OrderRepository
import com.sjay.cartfinder.phi.PhiState
import com.sjay.cartfinder.phi.PhiViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorDashboardScreen(
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val shopState by viewModel.shopState.collectAsState()
    val productsState by viewModel.productsState.collectAsState()
    val phiViewModel: PhiViewModel = viewModel()
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
                            NoStallPlaceholder(navController)
                        } else {
                            val stall = state.stall
                            LaunchedEffect(stall.id) {
                                phiViewModel.loadCertificate(stall.id)
                                viewModel.loadProducts(stall.id)
                            }
                            VendorProfileContent(stall, navController, phiState, phiViewModel, productsState, viewModel)
                        }
                    }
                    is ShopState.Error -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(Icons.Filled.ErrorOutline, contentDescription = null, tint = Color(0xFFE53935), modifier = Modifier.size(48.dp))
                            Spacer(Modifier.height(12.dp))
                            Text("Could not load stall data.", color = Color.Gray)
                        }
                    }
                    else -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFFF39C12))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NoStallPlaceholder(navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFFFF3E0), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.StoreMallDirectory, contentDescription = null, modifier = Modifier.size(52.dp), tint = Color(0xFFF39C12))
        }
        Spacer(Modifier.height(24.dp))
        Text("No Stall Yet", fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Set up your stall to start selling and reach customers.", color = Color.Gray, fontSize = 14.sp)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { navController.navigate(Screen.EditShop.route) },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF39C12)),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
            Spacer(Modifier.width(8.dp))
            Text("Create My Stall", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
        }
    }
}

@Composable
fun VendorProfileContent(
    stall: Stall,
    navController: NavController,
    phiState: PhiState,
    phiViewModel: PhiViewModel,
    productsState: ProductsState,
    viewModel: ShopViewModel
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    var orderCount by remember { mutableIntStateOf(0) }
    var totalRevenue by remember { mutableDoubleStateOf(0.0) }
    var isRequestingCert by remember { mutableStateOf(false) }
    var showRequestDialog by remember { mutableStateOf(false) }

    LaunchedEffect(stall.id) {
        scope.launch {
            val result = OrderRepository().getVendorOrders(stall.id)
            if (result.isSuccess) {
                val orders = result.getOrDefault(emptyList())
                orderCount = orders.size
                totalRevenue = orders.sumOf { it.totalAmount }
            }
        }
    }

    if (showRequestDialog) {
        AlertDialog(
            onDismissRequest = { showRequestDialog = false },
            icon = { Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(36.dp)) },
            title = { Text("Request PHI Certificate", fontWeight = FontWeight.Bold) },
            text = { Text("A PHI Officer will review your stall and conduct an inspection. Once certified, a badge will appear on your stall profile visible to all customers.") },
            confirmButton = {
                Button(
                    onClick = {
                        showRequestDialog = false
                        isRequestingCert = true
                        phiViewModel.requestCertificate(stall.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
                ) {
                    Text("Request Now", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
    ) {
        // ── Cover Image / Hero Section ───────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            if (!stall.imageUrl.isNullOrEmpty()) {
                Image(
                    painter = com.sjay.cartfinder.common.rememberSafeImagePainter(stall.imageUrl),
                    contentDescription = "Cover Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF2C3E50), Color(0xFF4A6FA5))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Store, contentDescription = null, modifier = Modifier.size(72.dp), tint = Color.White.copy(alpha = 0.3f))
                }
            }

            // Gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.65f))
                        )
                    )
            )

            // Top action buttons
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { navController.navigate(Screen.Notifications.route) },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.45f), CircleShape)
                ) {
                    Icon(androidx.compose.material.icons.Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.White)
                }
                IconButton(
                    onClick = { navController.navigate(Screen.Settings.route) },
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.45f), CircleShape)
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            // Bottom info overlay
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .clickable { navController.navigate(Screen.EditShop.route) }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color(0xFFF39C12), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Update Photo", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "#${stall.id.take(6).uppercase()}",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFF00B894).copy(alpha = 0.85f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        // ── Stall Name & Controls ────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(stall.name, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f, fill = false))
                    Spacer(modifier = Modifier.width(6.dp))
                    if (phiState is PhiState.CertificateData && (phiState as PhiState.CertificateData).certificate?.status == "ACTIVE") {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = "Certified", tint = Color(0xFF27AE60), modifier = Modifier.size(20.dp))
                    }
                }
                OutlinedButton(
                    onClick = { navController.navigate(Screen.EditShop.route) },
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Edit Info", color = Color(0xFFF39C12), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(Modifier.height(4.dp))
            Text(stall.location.address ?: "No Address Provided", color = Color.Gray, fontSize = 13.sp)
            Text(stall.category, color = Color.DarkGray, fontSize = 13.sp, fontWeight = FontWeight.Medium)

            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                // Rating
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate(Screen.VendorReviews.route) }
                ) {
                    Text(
                        if (stall.ratingCount > 0) String.format(java.util.Locale.US, "%.1f", stall.ratingAverage) else "New",
                        fontWeight = FontWeight.Bold, fontSize = 15.sp
                    )
                    if (stall.ratingCount > 0) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("(${stall.ratingCount})", color = Color.Gray, fontSize = 13.sp)
                    }
                }
                // Open / Close toggle
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            if (stall.isOpen) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                            RoundedCornerShape(24.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        if (stall.isOpen) "Open" else "Closed",
                        color = if (stall.isOpen) Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontWeight = FontWeight.Bold, fontSize = 13.sp
                    )
                    Spacer(Modifier.width(6.dp))
                    Switch(
                        checked = stall.isOpen,
                        onCheckedChange = { viewModel.toggleStallStatus(stall, it) },
                        modifier = Modifier.height(24.dp),
                        colors = SwitchDefaults.colors(
                            checkedTrackColor = Color(0xFF27AE60),
                            uncheckedTrackColor = Color(0xFFE53935)
                        )
                    )
                }
            }
        }

        // ── Stats Row ────────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val productCount = if (productsState is ProductsState.Success) (productsState as ProductsState.Success).products.size else 0
            StatCard(
                label = "Orders",
                value = "$orderCount",
                icon = Icons.Filled.ShoppingBag,
                iconTint = Color(0xFF1976D2),
                bgColor = Color(0xFFE3F2FD),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Revenue",
                value = "Rs. ${totalRevenue.toInt()}",
                icon = Icons.Filled.ShowChart,
                iconTint = Color(0xFF27AE60),
                bgColor = Color(0xFFE8F5E9),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                label = "Products",
                value = "$productCount",
                icon = Icons.Filled.Inventory2,
                iconTint = Color(0xFFF39C12),
                bgColor = Color(0xFFFFF3E0),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(8.dp))

        // ── PHI Certificate Section ──────────────────────────────────────────
        PhiCertificateSection(
            stall = stall,
            phiState = phiState,
            phiViewModel = phiViewModel,
            navController = navController,
            onRequestClick = { showRequestDialog = true }
        )

        // ── Opening Hours ────────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Schedule, contentDescription = null, tint = Color(0xFFF39C12), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("OPENING HOURS", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                    Text(
                        "Edit",
                        color = Color(0xFFF39C12),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        modifier = Modifier.clickable { navController.navigate(Screen.EditShop.route) }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                val hoursStr = stall.openingHours ?: "No hours specified"
                if (hoursStr == "No hours specified") {
                    Text(hoursStr, fontSize = 12.sp, color = Color.Gray)
                } else {
                    val hoursList = hoursStr.split("\n")
                    val mid = (hoursList.size + 1) / 2
                    Row(modifier = Modifier.fillMaxWidth()) {
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

        // ── Recent Products ──────────────────────────────────────────────────
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text("DISHES & MENU", fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                    Text("Manage stock & visibility", color = Color.Gray, fontSize = 12.sp)
                }
                TextButton(onClick = { navController.navigate(Screen.ProductManagement.route) }) {
                    Text("Manage All →", color = Color(0xFFF39C12), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            Spacer(Modifier.height(8.dp))
            if (productsState is ProductsState.Success) {
                val products = (productsState as ProductsState.Success).products
                if (products.isEmpty()) {
                    Text("No products yet. Tap 'Manage All' to add items.", color = Color.Gray, modifier = Modifier.padding(vertical = 8.dp), fontSize = 13.sp)
                } else {
                    products.take(3).forEach { product ->
                        VendorProductSummaryCard(product, navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            } else {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = Color(0xFFF39C12))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    icon: ImageVector,
    iconTint: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(bgColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(8.dp))
            Text(value, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Text(label, color = Color.Gray, fontSize = 11.sp)
        }
    }
}

@Composable
fun PhiCertificateSection(
    stall: Stall,
    phiState: PhiState,
    phiViewModel: PhiViewModel,
    navController: NavController,
    onRequestClick: () -> Unit
) {
    AnimatedVisibility(
        visible = phiState !is PhiState.Loading && phiState !is PhiState.Idle,
        enter = fadeIn() + slideInVertically()
    ) {
        when (phiState) {
            is PhiState.CertificateData -> {
                val cert = phiState.certificate
                if (cert != null && cert.status == "ACTIVE") {
                    // ✅ Active Certificate Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp)
                            .clickable {
                                navController.navigate(
                                    com.sjay.cartfinder.core.navigation.Screen.PhiCertificate.createRoute(stall.id, stall.name)
                                )
                            },
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color(0xFF27AE60), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("PHI Certified", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1B5E20))
                                Text("Grade ${cert.grade} · Score ${cert.score}/100", color = Color(0xFF2E7D32), fontSize = 12.sp)
                                Text("Tap to view full certificate", color = Color(0xFF388E3C), fontSize = 11.sp)
                            }
                            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFF27AE60))
                        }
                    }
                } else if (cert != null && cert.status == "PENDING_REQUEST") {
                    // ⏳ Pending Request Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 6.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color(0xFFFF8F00), RoundedCornerShape(12.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.HourglassTop, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                            }
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Certificate Requested", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF7F4F00))
                                Text("Awaiting PHI Officer review & inspection.", color = Color(0xFFBF6F00), fontSize = 12.sp)
                                Text("We'll notify you when it's processed.", color = Color.Gray, fontSize = 11.sp)
                            }
                        }
                    }
                } else {
                    // ❌ No Certificate — Request prompt
                    NoCertificateCard(onRequestClick = onRequestClick)
                }
            }
            is PhiState.Error -> {
                // Show request card on error (cert not found)
                NoCertificateCard(onRequestClick = onRequestClick)
            }
            else -> {}
        }
    }
}

@Composable
fun NoCertificateCard(onRequestClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFCECEC)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Color(0xFFE53935), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.GppBad, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("Not PHI Certified", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFFB71C1C))
                    Text("Your stall lacks a PHI health certificate.", color = Color(0xFFC62828), fontSize = 12.sp)
                    Text("Certified stalls build more customer trust.", color = Color.Gray, fontSize = 11.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            Button(
                onClick = onRequestClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935))
            ) {
                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Request PHI Certificate", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun VendorProductSummaryCard(product: MenuItem, navController: NavController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navController.navigate(Screen.ProductManagement.route) },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFFFF3E0)),
                contentAlignment = Alignment.Center
            ) {
                if (!product.imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = com.sjay.cartfinder.common.rememberSafeImagePainter(product.imageUrl),
                        contentDescription = product.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(Icons.Filled.RestaurantMenu, contentDescription = null, tint = Color(0xFFF39C12))
                }
                if (product.stockQuantity > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(3.dp)
                            .size(10.dp)
                            .background(Color(0xFF27AE60), CircleShape)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                Text("Rs. ${product.price.toInt()}", color = Color(0xFFF39C12), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(
                    if (product.stockQuantity > 0) "In Stock (${product.stockQuantity})" else "Out of Stock",
                    color = if (product.stockQuantity > 0) Color(0xFF27AE60) else Color(0xFFE53935),
                    fontSize = 12.sp
                )
            }
            Switch(
                checked = product.stockQuantity > 0,
                onCheckedChange = { /* handled in product management */ },
                modifier = Modifier.scale(0.8f),
                colors = SwitchDefaults.colors(checkedTrackColor = Color(0xFF27AE60))
            )
        }
    }
}
