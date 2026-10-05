package com.sjay.cartfinder.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.data.model.MenuItem
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class, androidx.compose.material.ExperimentalMaterialApi::class)
@Composable
fun ProductManagementScreen(
    navController: NavController,
    viewModel: ShopViewModel = viewModel()
) {
    val shopState by viewModel.shopState.collectAsState()
    val productsState by viewModel.productsState.collectAsState()
    
    var showAddDialog by remember { mutableStateOf(false) }

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
                title = { Text("Manage Products") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            if (stallId != null) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = PrimaryOrange
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Add Product")
                }
            }
        },
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "vendor")
        }
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
                    Text(state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is ProductsState.Success -> {
                    if (state.products.isEmpty()) {
                        Text("No products added yet. Pull to refresh.", modifier = Modifier.align(Alignment.Center))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                            items(state.products) { item ->
                                ProductItemCard(item = item, onArchive = {
                                    if (stallId != null) viewModel.archiveProduct(stallId, it.id)
                                })
                                Spacer(modifier = Modifier.height(8.dp))
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
                        viewModel.addProduct(stallId, newName, newDesc, priceDouble, "Default", stockInt)
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

@Composable
fun ProductItemCard(item: MenuItem, onArchive: (MenuItem) -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(item.name, fontWeight = FontWeight.Bold)
                Text(item.description, style = MaterialTheme.typography.bodyMedium)
                Text("Rs. ${item.price}", color = PrimaryOrange, fontWeight = FontWeight.Bold)
                Text("Stock: ${item.stockQuantity}")
                if (!item.available) {
                    Text("UNAVAILABLE", color = androidx.compose.ui.graphics.Color.Red, fontSize = 12.sp)
                }
            }
            if (item.available) {
                TextButton(onClick = { onArchive(item) }) {
                    Text("Archive", color = androidx.compose.ui.graphics.Color.Red)
                }
            }
        }
    }
}
