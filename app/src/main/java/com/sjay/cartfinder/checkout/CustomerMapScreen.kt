package com.sjay.cartfinder.checkout

import android.location.Location as AndroidLocation
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerMapScreen(
    navController: NavController,
    viewModel: CustomerDashboardViewModel = viewModel()
) {
    val stallsState by viewModel.stallsState.collectAsState()
    val context = LocalContext.current

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

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        viewModel.loadAllStalls()
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().osmdroidBasePath = File(context.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
    }

    var searchRadius by remember { mutableStateOf(5) } // km
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var myLocation by remember { mutableStateOf(GeoPoint(6.9271, 79.8612)) } // Default to Colombo, will update via GPS

    Scaffold(
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "customer")
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Map Background
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    MapView(ctx).apply {
                        setMultiTouchControls(true)
                        controller.setZoom(13.0)
                        controller.setCenter(myLocation)
                        
                        // Current location overlay using GPS
                        val myLocationOverlay = org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay(
                            org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider(ctx), this
                        )
                        myLocationOverlay.enableMyLocation()
                        myLocationOverlay.runOnFirstFix {
                            val loc = myLocationOverlay.myLocation
                            if (loc != null) {
                                (ctx as? android.app.Activity)?.runOnUiThread {
                                    myLocation = GeoPoint(loc.latitude, loc.longitude)
                                    controller.animateTo(myLocation)
                                }
                            }
                        }
                        overlays.add(myLocationOverlay)
                    }
                },
                update = { mapView ->
                    val myLocOverlay = mapView.overlays.filterIsInstance<org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay>().firstOrNull()
                    if (hasLocationPermission && myLocOverlay != null && !myLocOverlay.isMyLocationEnabled) {
                        myLocOverlay.enableMyLocation()
                    }
                    
                    mapView.overlays.removeAll { it is Marker }
                    
                    // Always show the current location marker explicitly
                    val myMarker = Marker(mapView).apply {
                        position = myLocation
                        setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        title = "My Location"
                        icon = context.getDrawable(android.R.drawable.ic_menu_mylocation)
                    }
                    mapView.overlays.add(myMarker)

                    if (stallsState is StallListState.Success) {
                        val allStalls = (stallsState as StallListState.Success).stalls
                        val filteredStalls = allStalls.filter {
                            val stallLoc = GeoPoint(it.location.latitude, it.location.longitude)
                            val dist = myLocation.distanceToAsDouble(stallLoc) / 1000.0 // in km
                            val matchesSearch = searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
                            val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
                            dist <= searchRadius && it.isOpen && matchesSearch && matchesCategory
                        }
                        
                        filteredStalls.forEach { stall ->
                            val marker = Marker(mapView).apply {
                                position = GeoPoint(stall.location.latitude, stall.location.longitude)
                                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                title = stall.name
                                setOnMarkerClickListener { _, _ ->
                                    val dist = myLocation.distanceToAsDouble(position) / 1000.0
                                    navController.navigate("stall_menu/${stall.id}/${stall.name}?distance=${dist}")
                                    true
                                }
                            }
                            mapView.overlays.add(marker)
                        }
                    }
                    mapView.invalidate()
                }
            )
            
            // Top Overlay (Search & Radius)
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp).align(Alignment.TopCenter)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Radius Selector
                    var expanded by remember { mutableStateOf(false) }
                    Box {
                        Row(
                            modifier = Modifier
                                .background(Color.White, RoundedCornerShape(24.dp))
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .clickable { expanded = true },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.MyLocation, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Within $searchRadius km", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            listOf(1, 3, 5, 10, 20).forEach { radius ->
                                DropdownMenuItem(
                                    text = { Text("$radius km") },
                                    onClick = { searchRadius = radius; expanded = false }
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    // Search Bar
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).height(50.dp),
                        placeholder = { Text("Search vendor...", fontSize = 14.sp) },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search", modifier = Modifier.size(20.dp)) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true
                    )
                }
                
                // Categories Row
                if (stallsState is StallListState.Success) {
                    val allStalls = (stallsState as StallListState.Success).stalls
                    val categories = listOf("All") + allStalls.map { it.category }.distinct().sorted()
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryOrange,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            // Bottom Overlay (Filtered Stalls)
            if (stallsState is StallListState.Success) {
                val allStalls = (stallsState as StallListState.Success).stalls
                val filteredStalls = allStalls.filter {
                    val stallLoc = GeoPoint(it.location.latitude, it.location.longitude)
                    val dist = myLocation.distanceToAsDouble(stallLoc) / 1000.0
                    val matchesSearch = searchQuery.isEmpty() || it.name.contains(searchQuery, ignoreCase = true)
                    val matchesCategory = selectedCategory == "All" || it.category == selectedCategory
                    dist <= searchRadius && it.isOpen && matchesSearch && matchesCategory
                }
                
                if (filteredStalls.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 16.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredStalls) { stall ->
                            val stallLoc = GeoPoint(stall.location.latitude, stall.location.longitude)
                            val dist = myLocation.distanceToAsDouble(stallLoc) / 1000.0
                            CompactStallCard(stall = stall, distance = dist) {
                                navController.navigate("stall_menu/${stall.id}/${stall.name}?distance=${dist}")
                            }
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 32.dp)
                            .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text("No vendors found within $searchRadius km", fontWeight = FontWeight.Bold)
                    }
                }
            } else if (stallsState is StallListState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = PrimaryOrange)
            }
        }
    }
}

@Composable
fun CompactStallCard(stall: Stall, distance: Double, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(280.dp)
            .height(110.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
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
                if (stall.imageUrl != null) {
                    androidx.compose.foundation.Image(
                        painter = coil.compose.rememberAsyncImagePainter(stall.imageUrl),
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
                    Text(String.format(java.util.Locale.US, "%.1f km", distance), color = Color.Gray, fontSize = 12.sp)
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
