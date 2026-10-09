package com.sjay.cartfinder.phi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.BottomNavigationBar
import com.sjay.cartfinder.data.model.PhiAlert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhiAlertsScreen(
    navController: NavController,
    phiViewModel: PhiViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val allAlerts by phiViewModel.allAlerts.collectAsState()
    var selectedFilter by remember { mutableStateOf("All") }

    LaunchedEffect(Unit) {
        phiViewModel.loadAllAlerts()
    }

    val filteredAlerts = when (selectedFilter) {
        "Urgent" -> allAlerts.filter { it.severity == "CRITICAL" || it.severity == "HIGH" }
        "Advisory" -> allAlerts.filter { it.severity == "MEDIUM" || it.severity == "LOW" }
        else -> allAlerts
    }

    val urgentCount = allAlerts.count { it.severity == "CRITICAL" || it.severity == "HIGH" }
    val advisoryCount = allAlerts.count { it.severity == "MEDIUM" || it.severity == "LOW" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Hygiene Alerts", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(
                            "$urgentCount urgent · $advisoryCount advisory",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { phiViewModel.loadAllAlerts() }) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Refresh")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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
                .background(MaterialTheme.colorScheme.background)
        ) {
            // ── Filter Tabs ────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    Triple("All", allAlerts.size, Color(0xFF1976D2)),
                    Triple("Urgent", urgentCount, Color(0xFFE53935)),
                    Triple("Advisory", advisoryCount, Color(0xFFFF8F00))
                ).forEach { (label, count, color) ->
                    val selected = selectedFilter == label
                    FilterChip(
                        selected = selected,
                        onClick = { selectedFilter = label },
                        label = {
                            Text(
                                "$label ($count)",
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = color,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            HorizontalDivider()

            if (filteredAlerts.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF27AE60),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "No ${if (selectedFilter == "All") "" else "$selectedFilter "}alerts",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )
                        Text("All stalls are compliant.", color = Color.Gray, fontSize = 13.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredAlerts) { alert ->
                        AlertDetailCard(
                            alert = alert,
                            onViewStall = {
                                navController.navigate("phi_stall_details/${alert.stallId}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AlertDetailCard(
    alert: PhiAlert,
    onViewStall: () -> Unit
) {
    val isUrgent = alert.severity == "CRITICAL" || alert.severity == "HIGH"
    val isResolved = alert.status == "RESOLVED"

    val accentColor = when {
        isResolved -> Color(0xFF27AE60)
        isUrgent -> Color(0xFFE53935)
        alert.severity == "MEDIUM" -> Color(0xFFFF8F00)
        else -> Color(0xFF1976D2)
    }
    val bgColor = when {
        isResolved -> Color(0xFFE8F5E9)
        isUrgent -> Color(0xFFFFF3F3)
        alert.severity == "MEDIUM" -> Color(0xFFFFFBF0)
        else -> MaterialTheme.colorScheme.surface
    }
    val severityLabel = when (alert.severity) {
        "CRITICAL" -> "🔴 CRITICAL"
        "HIGH" -> "🟠 HIGH"
        "MEDIUM" -> "🟡 MEDIUM"
        else -> "🟢 LOW"
    }
    val icon = when {
        isResolved -> Icons.Filled.CheckCircle
        isUrgent -> Icons.Filled.Warning
        else -> Icons.Filled.Info
    }
    val dateStr = java.text.SimpleDateFormat("dd MMM yyyy, HH:mm", java.util.Locale.US)
        .format(java.util.Date(alert.createdAt))

    val displayName = if (alert.stallName.isNotEmpty()) alert.stallName else "Stall ${alert.stallId.take(8)}"

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // ── Header ───────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(10.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(22.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            displayName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(dateStr, color = Color.Gray, fontSize = 11.sp)
                    }
                }
                Box(
                    modifier = Modifier
                        .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(severityLabel, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(12.dp))
            HorizontalDivider(color = accentColor.copy(alpha = 0.15f))
            Spacer(Modifier.height(12.dp))

            // ── Alert Content ─────────────────────────────────────────
            Text(alert.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = accentColor)
            Spacer(Modifier.height(4.dp))
            Text(alert.description, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f), fontSize = 13.sp)

            Spacer(Modifier.height(14.dp))

            // ── Action ────────────────────────────────────────────────
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onViewStall,
                    colors = ButtonDefaults.buttonColors(containerColor = accentColor),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Filled.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(if (isResolved) "View Stall" else "Inspect Stall", fontSize = 13.sp)
                }
                if (isResolved) {
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(0.6f)
                    ) {
                        Text("Archive", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

// Keep backward-compatible AlertCard for any existing call sites
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
    AlertDetailCard(
        alert = com.sjay.cartfinder.data.model.PhiAlert(
            stallName = stallName,
            title = issue,
            description = desc,
            severity = if (isAdvisory) "LOW" else "HIGH",
            status = if (isResolved) "RESOLVED" else "OPEN",
            createdAt = System.currentTimeMillis()
        ),
        onViewStall = onAction1
    )
}
