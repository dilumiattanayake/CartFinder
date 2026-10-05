package com.sjay.cartfinder.checkout

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.delay

@Composable
fun PayHereSandboxScreen(
    navController: NavController,
    orderId: String,
    totalAmount: Double,
    onPaymentSuccess: () -> Unit
) {
    var isProcessing by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("PayHere Sandbox", fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Total Amount: Rs. $totalAmount", fontSize = 20.sp)
            Text("Order ID: $orderId", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Spacer(modifier = Modifier.height(48.dp))

            if (isProcessing) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
                Text("Processing Test Payment...")
                
                LaunchedEffect(Unit) {
                    delay(2000)
                    // In a real PayHere integration, the SDK returns success here.
                    // We update the order status to PENDING (meaning paid and waiting for vendor)
                    val repo = com.sjay.cartfinder.data.repository.OrderRepository()
                    repo.updateOrderStatus(orderId, "PENDING")
                    onPaymentSuccess()
                }
            } else {
                Button(
                    onClick = { isProcessing = true },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Pay with Card (Sandbox)")
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                OutlinedButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}
