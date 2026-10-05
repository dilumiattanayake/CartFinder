package com.sjay.cartfinder.shop

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPickerScreen(navController: NavController) {
    val context = LocalContext.current
    
    // Configure osmdroid
    LaunchedEffect(Unit) {
        Configuration.getInstance().userAgentValue = context.packageName
        Configuration.getInstance().osmdroidBasePath = File(context.cacheDir, "osmdroid")
        Configuration.getInstance().osmdroidTileCache = File(context.cacheDir, "osmdroid/tiles")
    }
    
    var selectedPoint by remember { mutableStateOf<GeoPoint?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pick Shop Location") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (selectedPoint != null) {
                        IconButton(onClick = {
                            navController.previousBackStackEntry?.savedStateHandle?.set("picked_lat", selectedPoint?.latitude)
                            navController.previousBackStackEntry?.savedStateHandle?.set("picked_lon", selectedPoint?.longitude)
                            navController.popBackStack()
                        }) {
                            Icon(Icons.Filled.Check, contentDescription = "Confirm")
                        }
                    }
                }
            )
        }
    ) { padding ->
        AndroidView(
            modifier = Modifier.fillMaxSize().padding(padding),
            factory = { ctx ->
                MapView(ctx).apply {
                    setMultiTouchControls(true)
                    controller.setZoom(15.0)
                    
                    val myLocationOverlay = org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay(org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider(ctx), this)
                    myLocationOverlay.enableMyLocation()
                    myLocationOverlay.enableFollowLocation()
                    overlays.add(myLocationOverlay)
                    
                    // Set to a default location (e.g., Colombo, Sri Lanka) if location is not available yet
                    controller.setCenter(GeoPoint(6.9271, 79.8612))
                    
                    val mapEventsReceiver = object : MapEventsReceiver {
                        override fun singleTapConfirmedHelper(p: GeoPoint?): Boolean {
                            if (p != null) {
                                selectedPoint = p
                                // Clear old custom markers and add new one
                                overlays.removeAll { it is Marker }
                                val marker = Marker(this@apply).apply {
                                    position = p
                                    setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                                    title = "Selected Location"
                                }
                                overlays.add(marker)
                                invalidate()
                            }
                            return true
                        }
                        override fun longPressHelper(p: GeoPoint?): Boolean = false
                    }
                    overlays.add(MapEventsOverlay(mapEventsReceiver))
                }
            },
            update = { mapView ->
                // no-op for now
            }
        )
    }
}
