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
import androidx.compose.runtime.saveable.rememberSaveable
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

    var name by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var category by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var openingHours by rememberSaveable { mutableStateOf("") }
    
    var locationLat by rememberSaveable { mutableStateOf(0.0) }
    var locationLon by rememberSaveable { mutableStateOf(0.0) }
    var imageUrl by rememberSaveable { mutableStateOf<String?>(null) }
    
    val context = androidx.compose.ui.platform.LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedUri = com.sjay.cartfinder.utils.ImageUtils.saveImageToInternalStorage(context, uri)
            imageUrl = savedUri?.toString() ?: uri.toString()
        }
    }

    LaunchedEffect(currentUserId) {
        viewModel.loadVendorShop(currentUserId)
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
                if (address.isEmpty()) address = stall.location.address ?: ""
                if (openingHours.isEmpty()) openingHours = stall.openingHours ?: ""
                if (imageUrl == null) imageUrl = stall.imageUrl
                
                // If we haven't picked a location yet, load the existing one
                if (locationLat == 0.0 && locationLon == 0.0 && stall.location.latitude != 0.0) {
                    locationLat = stall.location.latitude
                    locationLon = stall.location.longitude
                }
            }
        }
    }

    // Update location if returned from map
    LaunchedEffect(pickedLat, pickedLon) {
        if (pickedLat != null && pickedLon != null) {
            locationLat = pickedLat
            locationLon = pickedLon
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
                if (!imageUrl.isNullOrEmpty()) {
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
            
            val categories = listOf("Fast Food", "Drinks", "Desserts", "Bakery", "Healthy", "Other")
            var expandedCategory by remember { mutableStateOf(false) }
            
            ExposedDropdownMenuBox(
                expanded = expandedCategory,
                onExpandedChange = { expandedCategory = !expandedCategory }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expandedCategory,
                    onDismissRequest = { expandedCategory = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                expandedCategory = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = address,
                onValueChange = { address = it },
                label = { Text("Physical Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            
            val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
            var openingHoursMap by rememberSaveable { mutableStateOf(mapOf<String, String>()) }

            LaunchedEffect(openingHours) {
                if (openingHoursMap.isEmpty() && openingHours.isNotBlank()) {
                    val map = mutableMapOf<String, String>()
                    openingHours.split("\n").forEach { line ->
                        val parts = line.split(":", limit = 2)
                        if (parts.size == 2) {
                            map[parts[0].trim()] = parts[1].trim()
                        }
                    }
                    openingHoursMap = map
                }
            }

            val timeOptions = listOf(
                "Closed", "Open 24 Hours",
                "06:00 AM - 02:00 PM", "08:00 AM - 05:00 PM",
                "09:00 AM - 06:00 PM", "10:00 AM - 08:00 PM",
                "11:00 AM - 09:00 PM", "12:00 PM - 10:00 PM",
                "05:00 PM - 12:00 AM", "06:00 PM - 02:00 AM"
            )

            Text("Opening Hours", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))
            days.forEach { day ->
                var expandedTime by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expandedTime,
                    onExpandedChange = { expandedTime = !expandedTime }
                ) {
                    OutlinedTextField(
                        value = openingHoursMap[day] ?: "Closed",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(day) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTime) },
                        modifier = Modifier.menuAnchor().fillMaxWidth().padding(vertical = 4.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedTime,
                        onDismissRequest = { expandedTime = false }
                    ) {
                        timeOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    val newMap = openingHoursMap.toMutableMap()
                                    newMap[day] = option
                                    openingHoursMap = newMap
                                    expandedTime = false
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Shop Location", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (locationLat == 0.0 && locationLon == 0.0) {
                        Text("No location selected yet.", color = MaterialTheme.colorScheme.error)
                    } else {
                        Text("Lat: $locationLat")
                        Text("Lon: $locationLon")
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
                    val formattedHours = days.mapNotNull { day -> 
                        val hours = openingHoursMap[day]
                        if (!hours.isNullOrBlank()) "$day: $hours" else null
                    }.joinToString("\n")
                    
                    val newLocation = Location(address = address, latitude = locationLat, longitude = locationLon)
                    viewModel.createOrUpdateShop(currentUserId, name, description, category, phone, newLocation, imageUrl, formattedHours)
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                enabled = name.isNotBlank() && locationLat != 0.0,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
            ) {
                Text("Save Stall", color = androidx.compose.ui.graphics.Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
