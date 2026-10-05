package com.sjay.cartfinder.phi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhiAlertsScreen(navController: NavController, phiViewModel: PhiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    val phiState by phiViewModel.phiState.collectAsState()

    LaunchedEffect(Unit) {
        phiViewModel.loadAllAlerts()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ALERTS", fontWeight = FontWeight.Black, fontSize = 20.sp) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFDECD4))
            )
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, role = "phi")
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
        ) {
            when (val state = phiState) {
                is PhiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PhiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Error: ${state.message}", color = MaterialTheme.colorScheme.error)
                    }
                }
                is PhiState.AlertData -> {
                    val alerts = state.alerts
                    val urgentCount = alerts.count { it.severity == "CRITICAL" || it.severity == "HIGH" }
                    val advisoryCount = alerts.count { it.severity == "MEDIUM" || it.severity == "LOW" }

                    // Header stats
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).background(Color.Red, RoundedCornerShape(4.dp)))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("ACTIVE HYGIENE ALERTS", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Text("$urgentCount Urgent • $advisoryCount Advisories", fontSize = 12.sp, color = Color.Gray)
                    }

                    // Filter Tabs
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(selected = true, onClick = {}, label = { Text("All (${alerts.size})", color = Color.White) }, colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFE67E22)))
                        FilterChip(selected = false, onClick = {}, label = { Text("Urgent $urgentCount") }, leadingIcon = { Box(modifier = Modifier.size(6.dp).background(Color.Red, RoundedCornerShape(3.dp))) })
                        FilterChip(selected = false, onClick = {}, label = { Text("Advisories $advisoryCount") }, leadingIcon = { Box(modifier = Modifier.size(6.dp).background(Color.Green, RoundedCornerShape(3.dp))) })
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (alerts.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No active alerts.")
                        }
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(alerts.size) { index ->
                                val alert = alerts[index]
                                val isAdvisory = alert.severity == "MEDIUM" || alert.severity == "LOW"
                                val isResolved = alert.status == "RESOLVED"
                                val formatter = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.US)
                                val dateStr = formatter.format(java.util.Date(alert.createdAt))

                                AlertCard(
                                    stallName = "Stall: ${alert.stallId}", // We don't have stall name in alert directly yet
                                    address = "Check Stall Details",
                                    type = if (isAdvisory) "Advisory" else "Urgent",
                                    typeColor = if (isResolved) Color(0xFFD1FAE5) else if (isAdvisory) Color(0xFFD1FAE5) else Color(0xFFFDE8E8),
                                    typeTextColor = if (isResolved) Color(0xFF059669) else if (isAdvisory) Color(0xFF059669) else Color(0xFFE74C3C),
                                    issue = alert.title,
                                    desc = alert.description,
                                    time = dateStr,
                                    action1 = if (isResolved) "View Details" else "Review",
                                    action1Color = if (isResolved) Color(0xFFF3F4F6) else Color(0xFFE67E22),
                                    action1TextColor = if (isResolved) Color.Black else Color.White,
                                    action2 = if (!isResolved) "Contact" else null,
                                    isAdvisory = isAdvisory,
                                    isResolved = isResolved,
                                    onAction1 = { navController.navigate(com.sjay.cartfinder.core.navigation.Screen.StallPhiDetails.createRoute(alert.stallId)) }
                                )
                            }
                        }
                    }
                }
                else -> {
                    // Loading or Idle
                }
            }
        }
    }
}

@Composable
fun AlertCard(
    stallName: String,
    address: String,
    type: String,
    typeColor: Color,
    typeTextColor: Color,
    issue: String,
    desc: String,
    time: String,
    action1: String,
    action1Color: Color,
    action1TextColor: Color = Color.White,
    action2: String?,
    isAdvisory: Boolean = false,
    isResolved: Boolean = false,
    onAction1: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(stallName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        if (isResolved) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(address, color = Color.Gray, fontSize = 12.sp)
                }
                Box(modifier = Modifier.background(typeColor, RoundedCornerShape(12.dp)).padding(horizontal = 8.dp, vertical = 4.dp)) {
                    Text(type, color = typeTextColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Issue
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = if (isResolved) Icons.Filled.CheckCircle else if (isAdvisory) Icons.Filled.Info else Icons.Filled.Warning
                    val iconColor = if (isResolved) Color(0xFF27AE60) else if (isAdvisory) Color(0xFF059669) else Color(0xFFE74C3C)
                    Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(issue, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = iconColor)
                }
                Text(time, color = Color.Gray, fontSize = 12.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(desc, color = Color.DarkGray, fontSize = 12.sp)
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Actions
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onAction1,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = action1Color),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(action1, color = action1TextColor, fontSize = 12.sp)
                }
                if (action2 != null) {
                    Button(
                        onClick = { },
                        modifier = Modifier.weight(0.5f),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF3F4F6)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(action2, color = Color.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
