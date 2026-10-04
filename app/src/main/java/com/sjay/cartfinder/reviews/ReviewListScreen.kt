package com.sjay.cartfinder.reviews

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewListScreen(navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    Box(modifier = Modifier.padding(16.dp).size(32.dp).background(Color(0xFFFDE6C8), CircleShape), contentAlignment = Alignment.Center) {
                        Icon(Icons.Filled.Search, contentDescription = null, tint = PrimaryOrange) // Placeholder icon for burger
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Map") },
                    label = { Text("Map") }
                )
                NavigationBarItem(
                    selected = true,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Star, contentDescription = "Reviews", tint = PrimaryOrange) },
                    label = { Text("Reviews", color = PrimaryOrange) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Notifications") },
                    label = { Text("Notifications") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { },
                    icon = { Icon(Icons.Filled.Search, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(padding)
        ) {
            item {
                Column(modifier = Modifier.background(Color.White).padding(16.dp)) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search stalls, dishes, or reviewers...") },
                        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = true,
                                onClick = { },
                                label = { Text("All Carts", color = Color.White) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF8B5A2B)),
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { },
                                label = { Text("Kottu Kraze") },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                        item {
                            FilterChip(
                                selected = false,
                                onClick = { },
                                label = { Text("Hopper Hub") },
                                shape = RoundedCornerShape(16.dp)
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("4.0+ only", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PHI Verified", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
            
            item {
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
                                Text("Visited a street stall?", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                Text("Rate taste & stall cleanliness", color = Color.Gray, fontSize = 14.sp)
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().background(Color(0xFFF5F7FF), RoundedCornerShape(8.dp)).padding(12.dp)) {
                            repeat(4) {
                                Icon(Icons.Filled.Star, contentDescription = null, tint = PrimaryOrange)
                            }
                            Icon(Icons.Filled.Star, contentDescription = null, tint = Color.LightGray)
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Great (4.0)", fontWeight = FontWeight.Bold, color = Color(0xFF8B5A2B), fontSize = 12.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Button(
                            onClick = { navController.navigate(Screen.SubmitReview.route) },
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
            
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Kottu Kraze", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text(" (Malabe Gate)", color = Color.Gray, fontSize = 14.sp)
                            Spacer(modifier = Modifier.weight(1f))
                            Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color(0xFF4CAF50))) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PHI Grade A", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Star, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                            Text(" 4.8", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text(" • 142 reviews", color = Color.Gray, fontSize = 14.sp)
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(40.dp).background(Color.Gray, CircleShape)) // Profile pic
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Nuwan Senanayake", fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color(0xFF4CAF50), modifier = Modifier.size(14.dp))
                                }
                                Text("Verified Eater • Yesterday", color = Color.Gray, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Row {
                                repeat(5) {
                                    Icon(Icons.Filled.Star, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(
                            "The chicken kottu here is unbeatable! Griddle was spotless, chef was wearing gloves and aprons. Totally safe and delicious."
                        )
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        // Image Placeholder
                        Box(modifier = Modifier.fillMaxWidth().height(180.dp).background(Color.LightGray, RoundedCornerShape(12.dp)))
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Card(
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F7FF))
                            ) {
                                Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.ThumbUp, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Helpful (24)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(16.dp))
                            
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Reply", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Icon(Icons.Outlined.Share, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
