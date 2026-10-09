package com.sjay.cartfinder.checkout

import android.location.LocationManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDashboardScreen(
    navController: NavController,
    viewModel: CustomerDashboardViewModel = viewModel()
) {
    val stallsState by viewModel.stallsState.collectAsState()
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val userName = auth.currentUser?.displayName.takeIf { !it.isNullOrBlank() } ?: "Customer"

    var hasLocationPermission by remember {
        mutableStateOf(
            androidx.core.content.ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        contract = androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    var myLocation by remember { mutableStateOf(GeoPoint(6.9271, 79.8612)) }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        viewModel.loadAllStalls()
    }
    
    LaunchedEffect(hasLocationPermission) {
        if (hasLocationPermission) {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            try {
                // Request a single update to get the latest location
                locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, { loc ->
                    myLocation = GeoPoint(loc.latitude, loc.longitude)
                }, null)
                // Also try network provider for faster, less accurate result
                locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, { loc ->
                    myLocation = GeoPoint(loc.latitude, loc.longitude)
                }, null)
            } catch (e: SecurityException) {
                // ignore
            }
        }
    }

    Scaffold(
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "customer")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(padding)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Hello, $userName 👋", fontWeight = FontWeight.ExtraBold, fontSize = 24.sp)
                    Text("What are you craving today?", color = Color.Gray, fontSize = 14.sp)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF0F0F0))
                            .clickable { navController.navigate(Screen.Notifications.route) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(androidx.compose.material.icons.Icons.Filled.Notifications, contentDescription = "Notifications", tint = Color.Gray)
                    }
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryOrange.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Person, contentDescription = "Profile", tint = PrimaryOrange)
                    }
                }
            }

            // Quick Actions / Banner
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Card(
                    modifier = Modifier.weight(1f).height(100.dp).clickable { navController.navigate(Screen.CustomerMap.route) },
                    colors = CardDefaults.cardColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center) {
                        Icon(Icons.Filled.Map, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Map View", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Card(
                        modifier = Modifier.fillMaxWidth().height(42.dp).clickable { navController.navigate(Screen.CustomerOrders.route) },
                        colors = CardDefaults.cardColors(containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.DarkGray else Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.List, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("My Orders", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth().height(42.dp).clickable { navController.navigate(Screen.Cart.route) },
                        colors = CardDefaults.cardColors(containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.DarkGray else Color.White),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("My Cart", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "NEARBY VENDORS", 
                fontWeight = FontWeight.ExtraBold, 
                fontSize = 16.sp, 
                color = PrimaryOrange, 
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            // Vendors List
            if (stallsState is StallListState.Success) {
                val allStalls = (stallsState as StallListState.Success).stalls
                val nearbyStalls = allStalls
                    .filter { it.isOpen }
                    .sortedBy { 
                        val stallLoc = GeoPoint(it.location.latitude, it.location.longitude)
                        myLocation.distanceToAsDouble(stallLoc)
                    }
                    .take(10) // Display nearby 10 vendor shops

                if (nearbyStalls.isNotEmpty()) {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(nearbyStalls) { stall ->
                            val stallLoc = GeoPoint(stall.location.latitude, stall.location.longitude)
                            val dist = myLocation.distanceToAsDouble(stallLoc) / 1000.0
                            
                            // Using a full-width variation of the CompactStallCard
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(110.dp)
                                    .clickable { navController.navigate("stall_menu/${stall.id}/${stall.name}?distance=${dist}") },
                                colors = CardDefaults.cardColors(containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.DarkGray else Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Row(modifier = Modifier.fillMaxSize().padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFFFFF3E0)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (!stall.imageUrl.isNullOrEmpty()) {
                                            androidx.compose.foundation.Image(
                                                painter = com.sjay.cartfinder.common.rememberSafeImagePainter(stall.imageUrl),
                                                contentDescription = stall.name,
                                                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                        } else {
                                            Icon(Icons.Filled.Store, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(32.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(stall.name, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, maxLines = 1, overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis)
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(stall.category, color = Color.Gray, fontSize = 12.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Icon(Icons.Filled.LocationOn, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(12.dp))
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Text(String.format(java.util.Locale.US, "%.1f km", dist), color = Color.Gray, fontSize = 12.sp)
                                        }
                                        Spacer(modifier = Modifier.weight(1f))
                                        
                                        val phiViewModel: com.sjay.cartfinder.phi.PhiViewModel = viewModel(key = stall.id)
                                        val phiState by phiViewModel.phiState.collectAsState()
                                        
                                        LaunchedEffect(stall.id) {
                                            phiViewModel.loadCertificate(stall.id)
                                        }
                                        
                                        var certScore: String? = null
                                        if (phiState is com.sjay.cartfinder.phi.PhiState.CertificateData) {
                                            val cert = (phiState as com.sjay.cartfinder.phi.PhiState.CertificateData).certificate
                                            if (cert != null && cert.status == "ACTIVE") {
                                                certScore = "Grade ${cert.grade}"
                                            }
                                        }
                                        
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color(0xFFF1C40F), modifier = Modifier.size(14.dp))
                                            Text(if (stall.ratingCount > 0) String.format(java.util.Locale.US, " %.1f ", stall.ratingAverage) else " New ", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            
                                            if (certScore != null) {
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(14.dp))
                                                Text(certScore!!, color = Color(0xFF27AE60), fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text("No open vendors nearby.", color = Color.Gray)
                    }
                }
            } else if (stallsState is StallListState.Loading) {
                Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = PrimaryOrange)
                }
            }
        }
    }
}
