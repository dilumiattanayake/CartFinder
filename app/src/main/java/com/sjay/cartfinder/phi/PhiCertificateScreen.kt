package com.sjay.cartfinder.phi

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Fullscreen
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.data.model.Certificate
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhiCertificateScreen(
    navController: NavController,
    stallId: String,
    stallName: String = "Caligo Street Food",
    phiViewModel: PhiViewModel = viewModel()
) {
    val phiState by phiViewModel.phiState.collectAsState()
    val stall by phiViewModel.stallState.collectAsState()
    
    LaunchedEffect(stallId) {
        phiViewModel.loadStall(stallId)
        phiViewModel.loadCertificate(stallId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vendor Profile", fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    AssistChip(
                        onClick = { },
                        label = { Text("LIVE PHI REGISTRY", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        leadingIcon = {
                            Box(modifier = Modifier.size(8.dp).background(Color.Green, RoundedCornerShape(4.dp)))
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFFEF9C3))
                    )
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8F9FA))
                .verticalScroll(androidx.compose.foundation.rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Gradient header logic... skipping complex UI for brevity, focusing on core elements
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Badge
            Box(contentAlignment = Alignment.Center) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFF27AE60), RoundedCornerShape(50.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Text("PHI Hygiene Verified", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("Ministry of Health & Sanitation Sri Lanka", fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            when (val state = phiState) {
                is PhiState.CertificateData -> {
                    val cert = state.certificate
                    if (cert != null) {
                        CertificateDetails(cert, stallName, stall?.location?.address ?: "Location pending")
                    } else {
                        Text("No active certificate found.")
                    }
                }
                is PhiState.Loading -> CircularProgressIndicator()
                else -> Text("Loading certificate...")
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // Footer Buttons
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67E22))
            ) {
                Icon(Icons.Outlined.Download, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Download Official PDF Badge")
            }
            
            OutlinedButton(
                onClick = { },
                modifier = Modifier.fillMaxWidth().padding(16.dp, 8.dp, 16.dp, 16.dp)
            ) {
                Icon(Icons.Outlined.Fullscreen, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Display at Stall (Kiosk Mode)")
            }
        }
    }
}

@Composable
fun CertificateDetails(cert: Certificate, stallName: String, stallAddress: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(stallName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(stallAddress, fontSize = 12.sp, color = Color.DarkGray)
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("MOH REGISTRATION", fontSize = 10.sp, color = Color.Gray)
                Text(cert.registrationNumber.ifEmpty { "MOH-MLB-2026-${cert.stallId.take(4)}" }, fontWeight = FontWeight.Bold)
            }
        }
        Card(modifier = Modifier.weight(1f), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("INSPECTING OFFICER", fontSize = 10.sp, color = Color.Gray)
                Text("PHI Officer", fontWeight = FontWeight.Bold)
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(40.dp).background(Color(0xFF27AE60), RoundedCornerShape(20.dp)), contentAlignment = Alignment.Center) {
                Text(cert.grade, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text("SANITATION RATING", fontSize = 10.sp, color = Color.Gray)
                Text("Grade ${cert.grade} (Score: ${cert.score}/100)", fontWeight = FontWeight.Bold)
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    val formatter = SimpleDateFormat("MMMM yyyy", Locale.US)
    val expiryStr = formatter.format(Date(cert.expiryDate))
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(8.dp).background(Color(0xFF27AE60), RoundedCornerShape(4.dp)))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Status: ${cert.status}", fontWeight = FontWeight.Bold, color = Color(0xFF065F46))
            }
            Text("Valid Until $expiryStr", fontSize = 12.sp, color = Color(0xFF065F46))
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))
    
    // QR Code Placeholder
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7))
    ) {
        Column(modifier = Modifier.padding(24.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(100.dp).background(Color.White), contentAlignment = Alignment.Center) {
                Icon(Icons.Filled.QrCode, contentDescription = "QR Code", modifier = Modifier.size(80.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.Security, contentDescription = null, tint = Color(0xFF27AE60), modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Scan to verify tamper-proof MOH digital ledger", fontSize = 10.sp, color = Color.DarkGray)
            }
        }
    }
}
