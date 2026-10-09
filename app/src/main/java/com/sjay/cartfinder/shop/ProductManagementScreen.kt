package com.sjay.cartfinder.shop

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
// import coil.compose.rememberAsyncImagePainter
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductManagementScreen(
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val shopState by viewModel.shopState.collectAsState()
    val productsState by viewModel.productsState.collectAsState()
    val currentUserId = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val context = androidx.compose.ui.platform.LocalContext.current
    
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            viewModel.loadVendorShop(currentUserId)
        }
    }

    LaunchedEffect(shopState) {
        if (shopState is ShopState.Success) {
            val stall = (shopState as ShopState.Success).stall
            if (stall != null) {
                viewModel.loadProducts(stall.id)
            }
        } else if (shopState is ShopState.Error) {
            android.widget.Toast.makeText(context, (shopState as ShopState.Error).message, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(productsState) {
        if (productsState is ProductsState.Error) {
            android.widget.Toast.makeText(context, (productsState as ProductsState.Error).message, android.widget.Toast.LENGTH_LONG).show()
        }
    }

    val stallId = (shopState as? ShopState.Success)?.stall?.id

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Text("EDIT FOOD ITEMS", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Changes are saved per item", color = Color.Gray, fontSize = 12.sp)
                    }
                },
                navigationIcon = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .background(PrimaryOrange, CircleShape)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(40.dp)
                            .background(Color(0xFFF3F4F6), CircleShape)
                            .clickable { navController.popBackStack() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.Gray)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.Black else Color.White)
            )
        },
        floatingActionButton = {
            if (stallId != null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = PrimaryOrange
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Product", tint = Color.White)
                }
            }
        },
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "vendor")
        },
        containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.Black else Color.White
    ) { padding ->
        androidx.compose.material3.pulltorefresh.PullToRefreshBox(
            isRefreshing = productsState is ProductsState.Loading,
            onRefresh = { if (stallId != null) viewModel.loadProducts(stallId) },
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (val state = productsState) {
                is ProductsState.Loading -> {
                    // Handled by PullToRefreshBox indicator
                }
                is ProductsState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(state.message, color = MaterialTheme.colorScheme.error)
                    }
                }
                is ProductsState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text("Menu details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Update the items shown on your Menu.", color = Color.Gray, fontSize = 14.sp)
                            }
                            Text("${state.products.size} ITEMS", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        val categories = listOf("All") + state.products.map { it.categoryId.ifEmpty { "Other" } }.distinct().sorted()
                        var selectedFilter by remember { mutableStateOf("All") }
                        
                        androidx.compose.foundation.lazy.LazyRow(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(categories) { _, cat ->
                                FilterChip(
                                    selected = selectedFilter == cat,
                                    onClick = { selectedFilter = cat },
                                    label = { Text(cat) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryOrange.copy(alpha = 0.2f),
                                        selectedLabelColor = PrimaryOrange
                                    )
                                )
                            }
                        }

                        val filteredProducts = if (selectedFilter == "All") state.products else state.products.filter { it.categoryId.ifEmpty { "Other" } == selectedFilter }

                        if (filteredProducts.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No products added yet. Click the + button to add.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                                val groupedItems = filteredProducts.groupBy { it.categoryId.ifEmpty { "Other" } }
                                groupedItems.forEach { (category, items) ->
                                    item {
                                        Text(
                                            text = category.uppercase(),
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 18.sp,
                                            color = PrimaryOrange,
                                            modifier = Modifier.padding(vertical = 12.dp)
                                        )
                                    }
                                    itemsIndexed(items) { index, item ->
                                        EditableProductCard(
                                            item = item, 
                                            index = index + 1,
                                            onSave = { updatedItem ->
                                                if (stallId != null) viewModel.updateProduct(context, stallId, updatedItem)
                                            },
                                            onDelete = { itemToDelete ->
                                                if (stallId != null) viewModel.deleteProduct(stallId, itemToDelete.id)
                                            }
                                        )
                                        Spacer(modifier = Modifier.height(24.dp))
                                    }
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        if (showAddDialog && stallId != null) {
            var newName by remember { mutableStateOf("") }
            var newDesc by remember { mutableStateOf("") }
            var newPrice by remember { mutableStateOf("") }
            var newStock by remember { mutableStateOf("10") }
            var newPrepTime by remember { mutableStateOf("8") }
            var newSpiceLevel by remember { mutableIntStateOf(0) }
            var imageUri by remember { mutableStateOf<Uri?>(null) }
            var selectedCategory by remember { mutableStateOf("Main Course") }
            
            val context = androidx.compose.ui.platform.LocalContext.current
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri: Uri? ->
                if (uri != null) {
                    val savedUri = com.sjay.cartfinder.utils.ImageUtils.saveImageToInternalStorage(context, uri)
                    imageUri = savedUri ?: uri
                }
            }

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Product") },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .clickable { launcher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            if (imageUri != null) {
                                Image(
                                    painter = com.sjay.cartfinder.common.rememberSafeImagePainter(imageUrl = imageUri?.toString()),
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.DarkGray)
                                    Text("Tap to add Image", color = Color.DarkGray, fontSize = 12.sp)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newDesc, onValueChange = { newDesc = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = newPrice, onValueChange = { newPrice = it }, label = { Text("Price") }, modifier = Modifier.fillMaxWidth())
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(value = newStock, onValueChange = { newStock = it }, label = { Text("Stock Quantity") }, modifier = Modifier.weight(1f))
                            OutlinedTextField(value = newPrepTime, onValueChange = { newPrepTime = it }, label = { Text("Prep Time (min)") }, modifier = Modifier.weight(1f))
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        val categories = listOf("Main Course", "Fast Food", "Drinks", "Desserts", "Bakery", "Healthy", "Other")
                        var expandedCategory by remember { mutableStateOf(false) }
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedCategory,
                            onExpandedChange = { expandedCategory = !expandedCategory }
                        ) {
                            OutlinedTextField(
                                value = selectedCategory,
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
                                            selectedCategory = cat
                                            expandedCategory = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        // Spice Level Selector
                        SpiceLevelSelector(
                            spiceLevel = newSpiceLevel,
                            onSpiceLevelChange = { newSpiceLevel = it }
                        )
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val priceDouble = newPrice.toDoubleOrNull() ?: 0.0
                        val stockInt = newStock.toIntOrNull() ?: 0
                        val prepTimeInt = newPrepTime.toIntOrNull() ?: 8
                        // Use a dummy image URL for now if an image is selected, or handle actual upload in ViewModel
                        val finalImageUrl = if (imageUri != null) imageUri.toString() else null
                    viewModel.addProduct(context, stallId, newName, newDesc, priceDouble, selectedCategory, stockInt, finalImageUrl, prepTimeInt, newSpiceLevel)
                        showAddDialog = false
                    }) {
                        Text("Add")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableProductCard(item: MenuItem, index: Int, onSave: (MenuItem) -> Unit, onDelete: (MenuItem) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    
    var name by remember { mutableStateOf(item.name) }
    var category by remember { mutableStateOf(item.categoryId.ifEmpty { "Main Course" }) }
    var prepTime by remember { mutableStateOf(item.preparationTime.toString()) }
    var price by remember { mutableStateOf(item.price.toInt().toString()) }
    var description by remember { mutableStateOf(item.description) }
    var available by remember { mutableStateOf(item.stockQuantity > 0 || item.available) }
    var imageUrl by remember { mutableStateOf(item.imageUrl) }
    var spiceLevel by remember { mutableIntStateOf(item.spiceLevel) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val savedUri = com.sjay.cartfinder.utils.ImageUtils.saveImageToInternalStorage(context, uri)
            imageUrl = savedUri?.toString() ?: uri.toString()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { isExpanded = !isExpanded },
        colors = CardDefaults.cardColors(containerColor = if (isExpanded) { if (androidx.compose.foundation.isSystemInDarkTheme()) Color.DarkGray else Color.White } else { if (androidx.compose.foundation.isSystemInDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFF9FAFB) }),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isExpanded) 4.dp else 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        if (!isExpanded) {
            Row(
                modifier = Modifier.padding(12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(64.dp).clip(RoundedCornerShape(8.dp)).background(Color.LightGray),
                    contentAlignment = Alignment.Center
                ) {
                    if (!item.imageUrl.isNullOrEmpty()) {
                        Image(
                            painter = com.sjay.cartfinder.common.rememberSafeImagePainter(item.imageUrl),
                            contentDescription = item.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Rs. ${item.price}", color = PrimaryOrange, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.Gray)
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Image Box
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (!imageUrl.isNullOrEmpty()) {
                    Image(
                        painter = com.sjay.cartfinder.common.rememberSafeImagePainter(imageUrl),
                        contentDescription = "Product Image",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(32.dp))
                }
                
                // Change Overlay
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp)
                        .background(if (androidx.compose.foundation.isSystemInDarkTheme()) Color.DarkGray else Color.White, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Black)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Change", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            // Name and Category
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("ITEM ${String.format("%02d", index)}", color = PrimaryOrange, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.Edit, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Tap to edit", color = Color.Gray, fontSize = 10.sp)
                    }
                }
                
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ITEM NAME", fontSize = 10.sp, color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.LightGray
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
                )
                
                val categories = listOf("Main Course", "Fast Food", "Drinks", "Desserts", "Bakery", "Healthy", "Other")
                var expandedCategory by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expandedCategory,
                    onExpandedChange = { expandedCategory = !expandedCategory }
                ) {
                    TextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("CATEGORY", fontSize = 10.sp, color = Color.Gray) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCategory) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.LightGray,
                            unfocusedIndicatorColor = Color.LightGray
                        ),
                        modifier = Modifier.menuAnchor(androidx.compose.material3.ExposedDropdownMenuAnchorType.PrimaryNotEditable, enabled = true).fillMaxWidth(),
                        textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
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
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField(
                value = prepTime,
                onValueChange = { prepTime = it },
                label = { Text("PREPARATION TIME", fontSize = 10.sp, color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray
                ),
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.width(16.dp))
            TextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("PRICE", fontSize = 10.sp, color = Color.Gray) },
                leadingIcon = { Text("Rs.", fontWeight = FontWeight.Bold) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.LightGray,
                    unfocusedIndicatorColor = Color.LightGray
                ),
                modifier = Modifier.weight(1f),
                textStyle = LocalTextStyle.current.copy(fontWeight = FontWeight.Bold)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Spice Level Selector
        SpiceLevelSelector(
            spiceLevel = spiceLevel,
            onSpiceLevelChange = { spiceLevel = it }
        )
        
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 12.dp))
        
        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("DESCRIPTION / CONTENTS", fontSize = 10.sp, color = Color.Gray) },
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            modifier = Modifier.fillMaxWidth(),
            textStyle = LocalTextStyle.current.copy(color = Color.DarkGray, fontSize = 14.sp)
        )
        
        HorizontalDivider(color = Color.LightGray, modifier = Modifier.padding(vertical = 12.dp))
        
        // Availability and Save
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = available, onCheckedChange = { available = it })
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("AVAILABILITY", color = Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(if (available) "Available" else "Unavailable", color = if (available) Color(0xFF27AE60) else Color.Red, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
            Row {
                IconButton(onClick = { onDelete(item) }, modifier = Modifier.background(Color(0xFFFEE2E2), RoundedCornerShape(8.dp)).padding(horizontal = 4.dp)) {
                    Icon(Icons.Filled.Delete, contentDescription = "Delete", tint = Color.Red)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val updatedItem = item.copy(
                            name = name,
                            description = description,
                            price = price.toDoubleOrNull() ?: item.price,
                            categoryId = category,
                            imageUrl = imageUrl,
                            stockQuantity = if (available) 10 else 0,
                            available = available,
                            preparationTime = prepTime.toIntOrNull() ?: item.preparationTime,
                            spiceLevel = spiceLevel
                        )
                        onSave(updatedItem)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Save", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
        }
    }
}
}

@Composable
fun SpiceLevelSelector(
    spiceLevel: Int,
    onSpiceLevelChange: (Int) -> Unit
) {
    val levels = listOf(
        Triple(0, "None", Color(0xFF9E9E9E)),
        Triple(1, "Mild", Color(0xFF4CAF50)),
        Triple(2, "Medium", Color(0xFFFF9800)),
        Triple(3, "Hot", Color(0xFFE53935))
    )

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Filled.LocalFireDepartment,
                    contentDescription = null,
                    tint = if (spiceLevel > 0) Color(0xFFE53935) else Color.LightGray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text("Spice Level", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            }
            Text(
                levels.find { it.first == spiceLevel }?.second ?: "None",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = levels.find { it.first == spiceLevel }?.third ?: Color.Gray
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { (level, label, color) ->
                val isSelected = spiceLevel == level
                FilterChip(
                    selected = isSelected,
                    onClick = { onSpiceLevelChange(level) },
                    label = {
                        Text(
                            label,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = if (level > 0) ({
                        Icon(
                            Icons.Filled.LocalFireDepartment,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isSelected) Color.White else color
                        )
                    }) else null,
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = color,
                        selectedLabelColor = Color.White,
                        selectedLeadingIconColor = Color.White
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
