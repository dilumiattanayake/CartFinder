package com.sjay.cartfinder.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Reply
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
import com.google.firebase.firestore.FirebaseFirestore
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.Review
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewListScreen(
    navController: NavController,
    stallId: String,
    stallName: String,
    viewModel: ReviewViewModel = viewModel(),
    role: String = "Customer"
) {
    val reviewsState by viewModel.reviewsState.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var selectedReviewForEdit by remember { mutableStateOf<Review?>(null) }
    var selectedReviewForReply by remember { mutableStateOf<Review?>(null) }
    var selectedReviewForReport by remember { mutableStateOf<Review?>(null) }
    var selectedReviewForDelete by remember { mutableStateOf<Review?>(null) }

    LaunchedEffect(stallId) {
        viewModel.getReviewsForStall(stallId)
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$stallName Reviews", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = role)
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF9FAFB))
                .padding(padding)
        ) {
            item {
                val hasReviewed = (reviewsState as? ReviewState.Success)?.reviews?.any { it.customerId == currentUserId } == true
                if ((role == "Customer" || role == "customer") && !hasReviewed) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(48.dp).background(Color(0xFFFDE6C8), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, tint = Color.Black)
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text("Visited recently?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Text("Share your experience with others", color = Color.Gray, fontSize = 14.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            Button(
                                onClick = { navController.navigate(Screen.SubmitReview.createRoute(stallId, stallName)) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, tint = Color.Black)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Write a Review", fontWeight = FontWeight.Bold, color = Color.Black)
                            }
                        }
                    }
                }
            }
            
            when (val state = reviewsState) {
                is ReviewState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = PrimaryOrange)
                        }
                    }
                }
                is ReviewState.Error -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            Text(text = "Error: ${state.message}", color = Color.Red)
                        }
                    }
                }
                is ReviewState.Success -> {
                    if (state.reviews.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(text = "No reviews yet.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(state.reviews) { review ->
                            ReviewItemCard(
                                review = review,
                                currentUserId = currentUserId,
                                userRole = role,
                                onEditClick = { selectedReviewForEdit = it },
                                onDeleteClick = { selectedReviewForDelete = it },
                                onReportClick = { selectedReviewForReport = it },
                                onReplyClick = { selectedReviewForReply = it },
                                onLikeClick = { viewModel.toggleLike(it.id, currentUserId, stallId) },
                                onDislikeClick = { viewModel.toggleDislike(it.id, currentUserId, stallId) }
                            )
                        }
                    }
                }
                else -> {}
            }
        }
    }

    // Dialogs
    selectedReviewForEdit?.let { review ->
        var editRating by remember { mutableStateOf(review.rating) }
        var editComment by remember { mutableStateOf(review.comment) }
        
        AlertDialog(
            onDismissRequest = { selectedReviewForEdit = null },
            title = { Text("Edit Review") },
            text = {
                Column {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Filled.Star,
                                contentDescription = null,
                                tint = if (index < editRating) PrimaryOrange else Color.LightGray,
                                modifier = Modifier
                                    .size(32.dp)
                                    .clickable { editRating = index + 1 }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = editComment,
                        onValueChange = { editComment = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Comment") }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.editReview(review, editRating, editComment, stallId)
                    selectedReviewForEdit = null
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { selectedReviewForEdit = null }) { Text("Cancel") }
            }
        )
    }

    selectedReviewForReply?.let { review ->
        var replyText by remember { mutableStateOf(review.vendorReply ?: "") }
        AlertDialog(
            onDismissRequest = { selectedReviewForReply = null },
            title = { Text("Vendor Reply") },
            text = {
                OutlinedTextField(
                    value = replyText,
                    onValueChange = { replyText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Your Reply") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.replyToReview(review.id, replyText, stallId)
                    selectedReviewForReply = null
                }) { Text("Post Reply") }
            },
            dismissButton = {
                TextButton(onClick = { selectedReviewForReply = null }) { Text("Cancel") }
            }
        )
    }

    selectedReviewForDelete?.let { review ->
        AlertDialog(
            onDismissRequest = { selectedReviewForDelete = null },
            title = { Text("Delete Review") },
            text = { Text("Are you sure you want to delete this review?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteReview(review.id, stallId)
                    selectedReviewForDelete = null
                }) { Text("Delete", color = Color.Red) }
            },
            dismissButton = {
                TextButton(onClick = { selectedReviewForDelete = null }) { Text("Cancel") }
            }
        )
    }

    selectedReviewForReport?.let { review ->
        AlertDialog(
            onDismissRequest = { selectedReviewForReport = null },
            title = { Text("Report Review") },
            text = { Text("Are you sure you want to report this review for moderation?") },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.reportReview(review.id, stallId)
                    selectedReviewForReport = null
                }) { Text("Report") }
            },
            dismissButton = {
                TextButton(onClick = { selectedReviewForReport = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun ReviewItemCard(
    review: Review, 
    currentUserId: String,
    userRole: String,
    onEditClick: (Review) -> Unit,
    onDeleteClick: (Review) -> Unit,
    onReportClick: (Review) -> Unit,
    onReplyClick: (Review) -> Unit,
    onLikeClick: (Review) -> Unit,
    onDislikeClick: (Review) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val isOwner = review.customerId == currentUserId
    val isVendor = userRole == "Vendor" || userRole == "vendor"
    
    val isLiked = review.likedBy.contains(currentUserId)
    val isDisliked = review.dislikedBy.contains(currentUserId)

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(40.dp).background(Color.Gray, CircleShape))
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(review.customerId.take(8) + "...", fontWeight = FontWeight.Bold) 
                    }
                    Text("Verified Eater", color = Color.Gray, fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                
                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.MoreVert, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        if (isOwner) {
                            DropdownMenuItem(
                                text = { Text("Edit") },
                                onClick = { expanded = false; onEditClick(review) },
                                leadingIcon = { Icon(Icons.Outlined.Edit, contentDescription = null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete") },
                                onClick = { expanded = false; onDeleteClick(review) },
                                leadingIcon = { Icon(Icons.Filled.Delete, contentDescription = null, tint = Color.Red) }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text("Report") },
                                onClick = { expanded = false; onReportClick(review) },
                                leadingIcon = { Icon(Icons.Filled.Report, contentDescription = null) }
                            )
                            if (isVendor) {
                                DropdownMenuItem(
                                    text = { Text("Vendor Reply") },
                                    onClick = { expanded = false; onReplyClick(review) },
                                    leadingIcon = { Icon(Icons.Filled.Reply, contentDescription = null) }
                                )
                            }
                        }
                    }
                }
            }
            
            Row(modifier = Modifier.padding(vertical = 8.dp)) {
                repeat(5) { index ->
                    val icon = Icons.Filled.Star
                    val tint = if (index < review.rating) PrimaryOrange else Color.LightGray
                    Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(16.dp))
                }
            }
            
            Text(review.comment)
            
            if (review.imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(review.imageUrls) { imageUrl ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                        ) {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = "Review Image",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { onLikeClick(review) }, modifier = Modifier.size(24.dp)) {
                    Icon(if (isLiked) Icons.Filled.ThumbUp else Icons.Outlined.ThumbUp, contentDescription = "Like", tint = if (isLiked) PrimaryOrange else Color.Gray)
                }
                Text(" ${review.likedBy.size}", color = Color.Gray, fontSize = 12.sp)
                Spacer(modifier = Modifier.width(16.dp))
                IconButton(onClick = { onDislikeClick(review) }, modifier = Modifier.size(24.dp)) {
                    Icon(if (isDisliked) Icons.Filled.ThumbDown else Icons.Outlined.ThumbDown, contentDescription = "Dislike", tint = if (isDisliked) PrimaryOrange else Color.Gray)
                }
                Text(" ${review.dislikedBy.size}", color = Color.Gray, fontSize = 12.sp)
            }
            
            if (!review.vendorReply.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FF))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Vendor Reply:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryOrange)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(review.vendorReply, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
