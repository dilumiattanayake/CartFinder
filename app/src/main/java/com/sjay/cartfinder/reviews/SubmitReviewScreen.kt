package com.sjay.cartfinder.reviews

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
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
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubmitReviewScreen(
    navController: NavController,
    stallId: String,
    stallName: String,
    viewModel: ReviewViewModel = viewModel()
) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    
    val submitState by viewModel.submitState.collectAsState()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: "guest"

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val totalImages = selectedImages + uris
            selectedImages = totalImages.take(3)
        }
    }

    LaunchedEffect(submitState) {
        if (submitState is SubmitReviewState.Success) {
            viewModel.resetSubmitState()
            navController.popBackStack()
        }
    }

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
                Text("Rate $stallName", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Share your experience with others", color = Color.Gray, fontSize = 14.sp)
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
                    for (i in 1..5) {
                        val icon = if (i <= rating) Icons.Filled.Star else Icons.Filled.StarBorder
                        Icon(
                            icon, 
                            contentDescription = "Rate $i", 
                            tint = PrimaryOrange, 
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { rating = i }
                        )
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
                        val ratingText = when(rating) {
                            1 -> "Poor"
                            2 -> "Fair"
                            3 -> "Good"
                            4 -> "Very Good"
                            else -> "Excellent"
                        }
                        Text("$rating - $ratingText", fontWeight = FontWeight.Bold, color = Color(0xFF8B5A2B))
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text("WRITE YOUR REVIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
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
                    placeholder = { Text("What did you like or dislike?", color = Color.Gray) },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth().height(150.dp)
                )
                
                // Image previews
                if (selectedImages.isNotEmpty()) {
                    Row(modifier = Modifier.padding(bottom = 12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        selectedImages.forEachIndexed { index, uri ->
                            Box(modifier = Modifier.size(60.dp).clip(RoundedCornerShape(8.dp))) {
                                AsyncImage(
                                    model = uri,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                IconButton(
                                    onClick = { 
                                        selectedImages = selectedImages.toMutableList().apply { removeAt(index) } 
                                    },
                                    modifier = Modifier.align(Alignment.TopEnd).size(20.dp).background(Color.Black.copy(alpha=0.5f), CircleShape)
                                ) {
                                    Icon(Icons.Filled.Close, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6)),
                        modifier = Modifier.clickable(enabled = selectedImages.size < 3) {
                            imagePickerLauncher.launch("image/*")
                        }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp), tint = if (selectedImages.size >= 3) Color.LightGray else Color.DarkGray)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Attach Photo (${selectedImages.size}/3)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = if (selectedImages.size >= 3) Color.LightGray else Color.DarkGray)
                        }
                    }
                }
            }
        }
        
        if (submitState is SubmitReviewState.Error) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = (submitState as SubmitReviewState.Error).message, 
                color = Color.Red, 
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = {
                // In a real app we'd upload images to Firebase Storage first. 
                // For now we'll just save their URI strings (which may be local).
                viewModel.submitReview(
                    orderId = "order_${System.currentTimeMillis()}", // Mocked order ID
                    customerId = currentUserId,
                    stallId = stallId,
                    rating = rating,
                    comment = reviewText,
                    imageUrls = selectedImages.map { it.toString() }
                )
            },
            enabled = submitState !is SubmitReviewState.Submitting,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (submitState is SubmitReviewState.Submitting) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.Black)
            } else {
                Icon(Icons.Filled.Check, contentDescription = null, tint = Color.Black)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit Review", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 16.sp)
            }
        }
    }
}
