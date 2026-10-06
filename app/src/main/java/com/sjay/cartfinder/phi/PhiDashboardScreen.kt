package com.sjay.cartfinder.phi

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.data.model.Certificate
import com.sjay.cartfinder.data.model.Stall
import com.sjay.cartfinder.shop.ShopState
import com.sjay.cartfinder.shop.ShopViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhiDashboardScreen(
    navController: NavController,
    shopViewModel: ShopViewModel = viewModel(),
    phiViewModel: PhiViewModel = viewModel()
) {
    val stallsState by shopViewModel.allStallsState.collectAsState()
    val pendingRequests by phiViewModel.pendingRequests.collectAsState()
    val allAlerts by phiViewModel.allAlerts.collectAsState()

    LaunchedEffect(Unit) {
        shopViewModel.loadAllStalls()
        phiViewModel.loadPendingRequests()
        phiViewModel.loadAllAlerts()
    }

    val stalls = if (stallsState is ShopState.StallsList) (stallsState as ShopState.StallsList).stalls else emptyList()
    val certifiedCount = stalls.count { /* we'd need certs, use pending as proxy */ false }
    val urgentAlerts = allAlerts.count { it.severity == "CRITICAL" || it.severity == "HIGH" }

    Scaffold(
        bottomBar = {
            com.sjay.cartfinder.core.navigation.BottomNavigationBar(navController = navController, role = "phi")
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            // ── Header Banner ───────────────────────────────────────────────
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF1A237E), Color(0xFF283593))
                            )
                        )
                        .padding(horizontal = 20.dp, vertical = 24.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    "PHI Officer Dashboard",
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    "Public Health Inspectorate",
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp
                                )
                            }
                            IconButton(
                                onClick = { navController.navigate(Screen.Notifications.route) },
                                modifier = Modifier.background(Color.White.copy(alpha = 0.15f), CircleShape)
                            ) {
                                Icon(
                                    androidx.compose.material.icons.Icons.Filled.Notifications,
                                    contentDescription = "Notifications",
                                    tint = Color.White
                                )
                            }
                        }

                        Spacer(Modifier.height(20.dp))

                        // Stats row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            PhiStatChip(
                                label = "Total Stalls",
                                value = "${stalls.size}",
                                icon = Icons.Filled.Store,
                                modifier = Modifier.weight(1f)
                            )
                            PhiStatChip(
                                label = "Pending Certs",
                                value = "${pendingRequests.size}",
                                icon = Icons.Filled.HourglassTop,
                                accent = if (pendingRequests.isNotEmpty()) Color(0xFFFFB74D) else Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            PhiStatChip(
                                label = "Active Alerts",
                                value = "${allAlerts.size}",
                                icon = Icons.Filled.Warning,
                                accent = if (urgentAlerts > 0) Color(0xFFEF5350) else Color.White,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // ── Pending Certificate Requests ─────────────────────────────────
            item {
                SectionHeader(
                    title = "PENDING CERTIFICATE REQUESTS",
                    badge = if (pendingRequests.isNotEmpty()) pendingRequests.size else null,
                    badgeColor = Color(0xFFFF8F00)
                )
            }

            if (pendingRequests.isEmpty()) {
                item {
                    EmptySectionCard(
                        icon = Icons.Filled.VerifiedUser,
                        message = "No pending certificate requests.",
                        iconTint = Color(0xFF27AE60)
                    )
                }
            } else {
                items(pendingRequests) { cert ->
                    PendingCertCard(
                        cert = cert,
                        onClick = {
                            navController.navigate("phi_stall_details/${cert.stallId}")
                        }
                    )
                }
            }

            // ── Active Alerts ────────────────────────────────────────────────
            item {
                SectionHeader(
                    title = "ACTIVE ALERTS",
                    badge = if (allAlerts.isNotEmpty()) allAlerts.size else null,
                    badgeColor = Color(0xFFE53935)
                )
            }

            if (allAlerts.isEmpty()) {
                item {
                    EmptySectionCard(
                        icon = Icons.Filled.CheckCircle,
                        message = "No active hygiene alerts.",
                        iconTint = Color(0xFF27AE60)
                    )
                }
            } else {
                items(allAlerts.take(3)) { alert ->
                    val isUrgent = alert.severity == "CRITICAL" || alert.severity == "HIGH"
                    AlertSummaryCard(
                        stallName = if (alert.stallName.isNotEmpty()) alert.stallName else "Stall ${alert.stallId.take(6)}",
                        title = alert.title,
                        severity = alert.severity,
                        isUrgent = isUrgent,
                        time = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.US)
                            .format(java.util.Date(alert.createdAt)),
                        onClick = { navController.navigate("phi_stall_details/${alert.stallId}") }
                    )
                }
                if (allAlerts.size > 3) {
                    item {
                        TextButton(
                            onClick = { navController.navigate(Screen.PhiAlerts.route) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        ) {
                            Text(
                                "View all ${allAlerts.size} alerts →",
                                color = Color(0xFFE53935),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // ── Stall Directory ──────────────────────────────────────────────
            item {
                SectionHeader(title = "STALL DIRECTORY", badge = if (stalls.isNotEmpty()) stalls.size else null, badgeColor = Color(0xFF1976D2))
            }

            when (stallsState) {
                is ShopState.Loading -> {
                    item {
                        Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = Color(0xFF1A237E))
                        }
                    }
                }
                is ShopState.StallsList -> {
                    if (stalls.isEmpty()) {
                        item {
                            EmptySectionCard(icon = Icons.Filled.Store, message = "No stalls registered yet.", iconTint = Color.Gray)
                        }
                    } else {
                        items(stalls) { stall ->
                            StallDirectoryCard(stall = stall) {
                                navController.navigate("phi_stall_details/${stall.id}")
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun PhiStatChip(
    label: String,
    value: String,
    icon: ImageVector,
    accent: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(22.dp))
        Spacer(Modifier.height(4.dp))
        Text(value, color = accent, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
    }
}

@Composable
fun SectionHeader(title: String, badge: Int? = null, badgeColor: Color = Color.Gray) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(title, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, color = Color.Gray)
        if (badge != null && badge > 0) {
            Box(
                modifier = Modifier
                    .background(badgeColor, RoundedCornerShape(20.dp))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text("$badge", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun EmptySectionCard(icon: ImageVector, message: String, iconTint: Color) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(24.dp))
            Spacer(Modifier.width(12.dp))
            Text(message, color = Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun PendingCertCard(cert: Certificate, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFF8F00), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    if (cert.stallName.isNotEmpty()) cert.stallName else "Stall ${cert.stallId.take(8)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text("Requested PHI Certificate", color = Color(0xFFBF6F00), fontSize = 12.sp)
                Text(
                    "ID: ${cert.stallId.take(10)}...",
                    color = Color.Gray,
                    fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "PENDING",
                    color = Color(0xFFFF8F00),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color(0xFFFFE082), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                )
                Spacer(Modifier.height(4.dp))
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color(0xFFFF8F00))
            }
        }
    }
}

@Composable
fun AlertSummaryCard(
    stallName: String,
    title: String,
    severity: String,
    isUrgent: Boolean,
    time: String,
    onClick: () -> Unit
) {
    val bgColor = if (isUrgent) Color(0xFFFFF3F3) else Color(0xFFFFFBF0)
    val accentColor = if (isUrgent) Color(0xFFE53935) else Color(0xFFFF8F00)
    val icon = if (isUrgent) Icons.Filled.Warning else Icons.Filled.Info

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = bgColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(accentColor, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stallName, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(title, color = accentColor, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(time, color = Color.Gray, fontSize = 11.sp)
            }
            Box(
                modifier = Modifier
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(severity, color = accentColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun StallDirectoryCard(stall: Stall, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(Color(0xFFE3F2FD), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.StoreMallDirectory, contentDescription = null, tint = Color(0xFF1976D2), modifier = Modifier.size(24.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(stall.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(stall.category, color = Color.Gray, fontSize = 12.sp)
                stall.location.address?.let {
                    Text(it, color = Color.Gray, fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (stall.isOpen) Color(0xFF27AE60) else Color(0xFFBDBDBD), CircleShape)
                )
                Spacer(Modifier.width(4.dp))
                Text(if (stall.isOpen) "Open" else "Closed", color = Color.Gray, fontSize = 11.sp)
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(20.dp))
            }
        }
    }
}
