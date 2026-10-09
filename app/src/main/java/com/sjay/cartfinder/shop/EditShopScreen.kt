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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

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
        } else if (shopState is ShopState.Error) {
            android.widget.Toast.makeText(context, (shopState as ShopState.Error).message, android.widget.Toast.LENGTH_LONG).show()
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
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                        painter = com.sjay.cartfinder.common.rememberSafeImagePainter(imageUrl),
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
                    modifier = Modifier.menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth()
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
            // openingHoursMap: day -> "HH:MM - HH:MM" or "Closed"
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

            // Which day is currently showing the time-edit sheet
            var editingDay by remember { mutableStateOf<String?>(null) }

            Text("Opening Hours", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
            Spacer(modifier = Modifier.height(8.dp))

            days.forEach { day ->
                val currentValue = openingHoursMap[day] ?: "Closed"
                val isClosed = currentValue == "Closed"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isClosed) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(day, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                            if (!isClosed) {
                                Text(
                                    currentValue,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = PrimaryOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            } else {
                                Text("Closed", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                        }
                        // Edit time button (only visible when open)
                        if (!isClosed) {
                            TextButton(
                                onClick = { editingDay = day },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Edit Time", color = PrimaryOrange, style = MaterialTheme.typography.labelMedium)
                            }
                        }
                        // Open / Closed toggle
                        Switch(
                            checked = !isClosed,
                            onCheckedChange = { isOpen ->
                                val newMap = openingHoursMap.toMutableMap()
                                if (isOpen) {
                                    newMap[day] = "08:00 AM - 06:00 PM"
                                } else {
                                    newMap[day] = "Closed"
                                }
                                openingHoursMap = newMap
                            },
                            colors = SwitchDefaults.colors(checkedTrackColor = PrimaryOrange),
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // Time-range picker dialog
            if (editingDay != null) {
                val day = editingDay!!
                val currentValue = openingHoursMap[day] ?: "08:00 AM - 06:00 PM"

                // Parse existing value
                val parts = currentValue.split(" - ")
                var openTime by remember(day) { mutableStateOf(if (parts.size == 2) parts[0] else "08:00 AM") }
                var closeTime by remember(day) { mutableStateOf(if (parts.size == 2) parts[1] else "06:00 PM") }

                AlertDialog(
                    onDismissRequest = { editingDay = null },
                    title = {
                        Text("$day Hours", fontWeight = FontWeight.Bold)
                    },
                    text = {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text("Set custom opening and closing times", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            Spacer(Modifier.height(16.dp))
                            TimeRangePicker(
                                openTime = openTime,
                                closeTime = closeTime,
                                onOpenTimeChange = { openTime = it },
                                onCloseTimeChange = { closeTime = it }
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                val newMap = openingHoursMap.toMutableMap()
                                newMap[day] = "$openTime - $closeTime"
                                openingHoursMap = newMap
                                editingDay = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange)
                        ) {
                            Text("Save", color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { editingDay = null }) { Text("Cancel") }
                    }
                )
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
                    viewModel.createOrUpdateShop(context, currentUserId, name, description, category, phone, newLocation, imageUrl, formattedHours)
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

/**
 * Custom time-range picker: two rows (Open / Close) each with Hour, Minute, AM/PM selectors.
 * Times are formatted as "HH:MM AM" strings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeRangePicker(
    openTime: String,
    closeTime: String,
    onOpenTimeChange: (String) -> Unit,
    onCloseTimeChange: (String) -> Unit
) {
    @Composable
    fun TimeSelector(label: String, time: String, onTimeChange: (String) -> Unit) {
        // Parse "08:00 AM"
        val parts = time.split(":", " ")
        var hour by remember(time) { mutableStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 8) }
        var minute by remember(time) { mutableStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }
        var isPm by remember(time) { mutableStateOf(parts.getOrNull(2) == "PM") }

        fun formatTime() = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')} ${if (isPm) "PM" else "AM"}"

        Column(modifier = Modifier.fillMaxWidth()) {
            Text(label, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Gray)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Hour picker
                OutlinedTextField(
                    value = hour.toString().padStart(2, '0'),
                    onValueChange = { v ->
                        val h = v.toIntOrNull()?.coerceIn(1, 12) ?: hour
                        hour = h
                        onTimeChange(formatTime())
                    },
                    label = { Text("HH", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                Text(":", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                // Minute picker
                OutlinedTextField(
                    value = minute.toString().padStart(2, '0'),
                    onValueChange = { v ->
                        val m = v.toIntOrNull()?.coerceIn(0, 59) ?: minute
                        minute = m
                        onTimeChange(formatTime())
                    },
                    label = { Text("MM", fontSize = 10.sp) },
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
                // AM / PM toggle
                Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    FilterChip(
                        selected = !isPm,
                        onClick = { isPm = false; onTimeChange(formatTime()) },
                        label = { Text("AM", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                    FilterChip(
                        selected = isPm,
                        onClick = { isPm = true; onTimeChange(formatTime()) },
                        label = { Text("PM", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryOrange,
                            selectedLabelColor = androidx.compose.ui.graphics.Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        TimeSelector(label = "Opens at", time = openTime, onTimeChange = onOpenTimeChange)
        HorizontalDivider()
        TimeSelector(label = "Closes at", time = closeTime, onTimeChange = onCloseTimeChange)
    }
}
