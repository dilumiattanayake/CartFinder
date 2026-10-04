package com.sjay.cartfinder.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitReviewScreen(navController: NavController) {
    var reviewText by remember { mutableStateOf("") }
    var c1 by remember { mutableStateOf(true) }
    var c2 by remember { mutableStateOf(true) }
    var c3 by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Rate Kottu Kraze", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Verified QR check-in recorded at Kaduwela Road", color = Color.Gray, fontSize = 12.sp)
                }
            }
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.background(Color(0xFFF5F5F5), RoundedCornerShape(50))
            ) {
                Icon(Icons.Filled.Close, contentDescription = "Close")
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Stars
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FF))
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    repeat(5) {
                        Icon(Icons.Filled.Star, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(36.dp))
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFE0B2))
                ) {
                    Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(PrimaryOrange, RoundedCornerShape(50)))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("4.8 - Outstanding Hygiene", fontWeight = FontWeight.Bold, color = Color(0xFF8B5A2B))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Hygiene Quick-Check
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
            Text("Hygiene Quick-Check", fontWeight = FontWeight.Bold)
            Text("Tap to verify", fontSize = 10.sp, color = Color.Gray)
        }
        
        Spacer(modifier = Modifier.height(12.dp))
        
        HygieneCheckItem("Staff wore clean gloves", c1) { c1 = it }
        Spacer(modifier = Modifier.height(8.dp))
        HygieneCheckItem("Stainless steel counter clean", c2) { c2 = it }
        Spacer(modifier = Modifier.height(8.dp))
        HygieneCheckItem("Eco packaging & covered bins", c3) { c3 = it }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("FIELD NOTES & OBSERVATIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
        Spacer(modifier = Modifier.height(8.dp))
        
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FF)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                TextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Add a comment about food safety or cleanliness (Optional)...", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().height(100.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp), tint = Color.DarkGray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attach Photo", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                        }
                    }
                    Text("${reviewText.length}/140", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Submit Verification", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Check, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(12.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("Syncs anonymously to Colombo Municipal PHI public logs", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
        }
    }
}

@Composable
fun HygieneCheckItem(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    colors = CheckboxDefaults.colors(checkedColor = PrimaryOrange, checkmarkColor = Color.Black)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(label, color = Color.Black)
            }
            Icon(Icons.Filled.Check, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
        }
    }
}
