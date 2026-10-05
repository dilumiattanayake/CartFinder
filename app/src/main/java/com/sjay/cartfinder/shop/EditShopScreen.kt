package com.sjay.cartfinder.shop

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.Location
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditShopScreen(
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val shopState by viewModel.shopState.collectAsState()
    
    // We observe the savedStateHandle for location updates from the map screen
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
    val pickedLat = savedStateHandle?.getLiveData<Double>("picked_lat")?.value
    val pickedLon = savedStateHandle?.getLiveData<Double>("picked_lon")?.value

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf(Location()) }
    var imageUrl by remember { mutableStateOf<String?>(null) }
    
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUrl = uri.toString()
        }
    }

    // Pre-fill existing data if we have it
    LaunchedEffect(shopState) {
        if (shopState is ShopState.Success) {
            val stall = (shopState as ShopState.Success).stall
            if (stall != null) {
                if (name.isEmpty()) name = stall.name
                if (description.isEmpty()) description = stall.description
                if (category.isEmpty()) category = stall.category
                if (phone.isEmpty()) phone = stall.phone ?: ""
                if (imageUrl == null) imageUrl = stall.imageUrl
                
                // If we haven't just picked a new location, use the existing one
                if (pickedLat == null && pickedLon == null) {
                    location = stall.location
                }
            }
        }
    }

    // Update location if returned from map
    LaunchedEffect(pickedLat, pickedLon) {
        if (pickedLat != null && pickedLon != null) {
            location = location.copy(latitude = pickedLat, longitude = pickedLon)
            // Consume the value so it doesn't re-trigger on config change
            savedStateHandle?.remove<Double>("picked_lat")
            savedStateHandle?.remove<Double>("picked_lon")
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Stall Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Image Picker
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
                        contentDescription = "Shop Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(48.dp), tint = Color.Gray)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tap to upload Shop Image", color = Color.Gray, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Stall Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Category (e.g. Fast Food, Drinks)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Shop Location", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (location.latitude == 0.0 && location.longitude == 0.0) {
                        Text("No location selected yet.", color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Lat: ${location.latitude}")
                        Text("Lon: ${location.longitude}")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { navController.navigate(Screen.MapPicker.route) }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.LocationOn, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pick Location on Map")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            
            Button(
                onClick = { 
                    viewModel.createOrUpdateShop(currentUserId, name, description, category, phone, location, imageUrl)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = name.isNotBlank() && location.latitude != 0.0,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Save Stall", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
