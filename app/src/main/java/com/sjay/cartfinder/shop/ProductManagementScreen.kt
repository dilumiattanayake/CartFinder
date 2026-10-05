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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
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
import coil.compose.rememberAsyncImagePainter
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
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
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
        containerColor = Color.White
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

                        if (state.products.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No products added yet. Click the + button to add.", color = Color.Gray)
                            }
                        } else {
                            LazyColumn(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp)) {
                                itemsIndexed(state.products) { index, item ->
                                    EditableProductCard(
                                        item = item, 
                                        index = index + 1,
                                        onSave = { updatedItem ->
                                            if (stallId != null) viewModel.updateProduct(stallId, updatedItem)
                                        }
                                    )
                                    Spacer(modifier = Modifier.height(24.dp))
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

            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Add New Product") },
                text = {
                    Column {
                        OutlinedTextField(value = newName, onValueChange = { newName = it }, label = { Text("Name") })
                        OutlinedTextField(value = newDesc, onValueChange = { newDesc = it }, label = { Text("Description") })
                        OutlinedTextField(value = newPrice, onValueChange = { newPrice = it }, label = { Text("Price") })
                        OutlinedTextField(value = newStock, onValueChange = { newStock = it }, label = { Text("Stock Quantity") })
                    }
                },
                confirmButton = {
                    TextButton(onClick = {
                        val priceDouble = newPrice.toDoubleOrNull() ?: 0.0
                        val stockInt = newStock.toIntOrNull() ?: 0
                        viewModel.addProduct(stallId, newName, newDesc, priceDouble, "Main Course", stockInt)
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
fun EditableProductCard(item: MenuItem, index: Int, onSave: (MenuItem) -> Unit) {
    var name by remember { mutableStateOf(item.name) }
    var category by remember { mutableStateOf(item.categoryId.ifEmpty { "Main Course" }) }
    var prepTime by remember { mutableStateOf("8 mins") }
    var price by remember { mutableStateOf(item.price.toInt().toString()) }
    var description by remember { mutableStateOf(item.description) }
    var available by remember { mutableStateOf(item.stockQuantity > 0 || item.available) }
    var imageUrl by remember { mutableStateOf(item.imageUrl) }
    var spiceLevel by remember { mutableStateOf(1) } // 1 to 3

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            imageUrl = uri.toString()
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
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
                if (imageUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(imageUrl),
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
                        .background(Color.White, RoundedCornerShape(12.dp))
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
                
                TextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("CATEGORY", fontSize = 10.sp, color = Color.Gray) },
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
        
        // Spice level
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("Spice level", color = Color.Gray, fontSize = 12.sp)
            Row {
                for (i in 1..3) {
                    Box(
                        modifier = Modifier
                            .padding(end = 4.dp)
                            .size(24.dp)
                            .background(Color.White, CircleShape)
                            .clickable { spiceLevel = i },
                        contentAlignment = Alignment.Center
                    ) {
                        // Drawing border
                        Box(modifier = Modifier.fillMaxSize().padding(1.dp).background(Color.Transparent, CircleShape))
                        Icon(
                            Icons.Filled.LocalFireDepartment, 
                            contentDescription = null, 
                            tint = if (i <= spiceLevel) PrimaryOrange else Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
        
        Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 12.dp))
        
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
        
        Divider(color = Color.LightGray, modifier = Modifier.padding(vertical = 12.dp))
        
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
            
            Button(
                onClick = {
                    val updatedItem = item.copy(
                        name = name,
                        description = description,
                        price = price.toDoubleOrNull() ?: item.price,
                        categoryId = category,
                        imageUrl = imageUrl,
                        stockQuantity = if (available) 10 else 0,
                        available = available
                    )
                    onSave(updatedItem)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save item", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}
