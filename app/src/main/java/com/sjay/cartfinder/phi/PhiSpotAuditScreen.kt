package com.sjay.cartfinder.phi

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.google.firebase.auth.FirebaseAuth
import com.sjay.cartfinder.core.navigation.BottomNavigationBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhiSpotAuditScreen(
    navController: NavController,
    stallId: String = "",
    phiViewModel: PhiViewModel = viewModel(),
    shopViewModel: com.sjay.cartfinder.shop.ShopViewModel = viewModel()
) {
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var personalHygiene by remember { mutableStateOf("PASS") }
    var foodTemp by remember { mutableStateOf("ADVISORY") }
    var waterOil by remember { mutableStateOf("PASS") }
    var wasteBin by remember { mutableStateOf("FAIL") }
    var remarks by remember { mutableStateOf("") }
    
    val score = calculateScore(personalHygiene, foodTemp, waterOil, wasteBin)
    val grade = if (score >= 90) "A" else if (score >= 75) "B" else if (score >= 50) "C" else "Rejected"

    var selectedStallId by remember { mutableStateOf(stallId) }
    val stall by phiViewModel.stallState.collectAsState()
    val allStallsState by shopViewModel.allStallsState.collectAsState()
    
    var showStallDropdown by remember { mutableStateOf(false) }

    LaunchedEffect(selectedStallId) {
        if (selectedStallId.isNotEmpty()) {
            phiViewModel.loadStall(selectedStallId)
        }
    }
    
    LaunchedEffect(Unit) {
        shopViewModel.loadAllStalls()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("SPOT AUDITS", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFDECD4)
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
                .verticalScroll(rememberScrollState())
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.LocationOn, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("FIELD SQUAD 08", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text("GPS LOCK: ACTIVE", color = Color.Green, fontSize = 10.sp)
                AssistChip(
                    onClick = { },
                    label = { Text("OFFLINE SYNC ON", fontSize = 10.sp, color = Color(0xFFE67E22)) },
                    colors = AssistChipDefaults.assistChipColors(containerColor = Color(0xFFFDF2E9))
                )
            }



            // Start Inspection
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = Color(0xFF27AE60))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Spot Inspection", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
                Badge(containerColor = Color(0xFFE5E7EB)) { Text("REF #PHI-${selectedStallId.take(4)}", color = Color.DarkGray) }
            }

            // Vendor Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Store, contentDescription = null, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ACTIVE VENDOR MATCH", color = Color(0xFFE67E22), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Text(stall?.name ?: "No Stall Selected", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(stall?.location?.address ?: "Tap swap icon to select stall", color = Color.Gray, fontSize = 12.sp)
                    }
                    Box {
                        IconButton(onClick = { showStallDropdown = true }) {
                            Icon(Icons.Filled.SwapHoriz, contentDescription = "Change Stall")
                        }
                        DropdownMenu(
                            expanded = showStallDropdown,
                            onDismissRequest = { showStallDropdown = false }
                        ) {
                            when (val state = allStallsState) {
                                is com.sjay.cartfinder.shop.ShopState.StallsList -> {
                                    state.stalls.forEach { s ->
                                        DropdownMenuItem(
                                            text = { Text(s.name) },
                                            onClick = {
                                                selectedStallId = s.id
                                                showStallDropdown = false
                                            }
                                        )
                                    }
                                }
                                is com.sjay.cartfinder.shop.ShopState.Loading -> {
                                    DropdownMenuItem(text = { Text("Loading...") }, onClick = {})
                                }
                                else -> {
                                    DropdownMenuItem(text = { Text("No Stalls Found") }, onClick = {})
                                }
                            }
                        }
                    }
                }
            }

            // Criteria
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("MOH STANDARD CRITERIA", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text("Compliance: $score%", color = Color(0xFF27AE60), fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            CriteriaCard("Personal Hygiene & Clean Attire", "Hairnet, aprons, clean hands & nails", personalHygiene) { personalHygiene = it }
            CriteriaCard("Food Temp & Sneeze Guards", "Covered displays, safe hot/cold zones", foodTemp, hasAdvisory = true) { foodTemp = it }
            CriteriaCard("Filtered Water & Cooking Oil", "Fresh frying oil & sealed drinking water", waterOil) { waterOil = it }
            CriteriaCard("Waste Bin Lid & Stall Perimeter", "Foot-pedal bin, zero drain blockage", wasteBin) { wasteBin = it }



            // Remarks
            Text("Inspector Remarks & Corrective Directives", fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
            OutlinedTextField(
                value = remarks,
                onValueChange = { remarks = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = { Text("Enter remarks...") },
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color(0xFFF3F4F6),
                    focusedContainerColor = Color.White
                )
            )

            // Rating
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (grade == "Rejected") Color(0xFFFDE8E8) else Color(0xFFFEF9C3))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(if (grade == "Rejected") Color.Red else Color(0xFF27AE60), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(if (grade == "Rejected") "F" else grade, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Provisional MOH Rating", fontWeight = FontWeight.Bold)
                        Text(if (grade == "Rejected") "Failed Compliance Standard" else "Compliant for Green QR Sticker Issue", fontSize = 12.sp, color = if (grade == "Rejected") Color.Red else Color.Black)
                    }
                    Text("SEC ID: PHI-084", fontSize = 10.sp)
                }
            }

            // Buttons
            Button(
                onClick = { 
                    if (selectedStallId.isNotEmpty()) {
                        val resultText = if (score >= 75) "PASS" else "FAIL"
                        val notesJson = """{"personalHygiene":"$personalHygiene", "foodTemp":"$foodTemp", "waterOil":"$waterOil", "wasteBin":"$wasteBin", "remarks":"$remarks"}"""
                        phiViewModel.addInspection(selectedStallId, currentUserId, score, resultText, notesJson)
                        // Also automatically update or issue certificate based on this grade
                        phiViewModel.issueCertificate(selectedStallId, grade, score)
                        navController.popBackStack()
                    }
                },
                enabled = selectedStallId.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF27AE60))
            ) {
                Icon(Icons.Filled.Verified, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Submit & Issue Digital Grade / Notice")
            }

            OutlinedButton(
                onClick = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Save Field Draft to Device")
            }
        }
    }
}

fun calculateScore(p: String, f: String, w: String, wb: String): Int {
    var score = 0
    if (p == "PASS") score += 25
    if (f == "PASS") score += 25 else if (f == "ADVISORY") score += 15
    if (w == "PASS") score += 25
    if (wb == "PASS") score += 25
    return score
}

@Composable
fun CriteriaCard(title: String, desc: String, selected: String, hasAdvisory: Boolean = false, onSelect: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F4F6))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold)
                    Text(desc, color = Color.Gray, fontSize = 12.sp)
                }
                Text("25 PTS", color = Color(0xFF27AE60), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { onSelect("PASS") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selected == "PASS") Color(0xFF27AE60) else Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("PASS", color = if (selected == "PASS") Color.White else Color.Black)
                }
                if (hasAdvisory) {
                    Button(
                        onClick = { onSelect("ADVISORY") },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = if (selected == "ADVISORY") Color(0xFFF59E0B) else Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("ADVISORY", color = if (selected == "ADVISORY") Color.White else Color.Black, fontSize = 10.sp)
                    }
                }
                Button(
                    onClick = { onSelect("FAIL") },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = if (selected == "FAIL") Color(0xFFE74C3C) else Color.White),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("FAIL", color = if (selected == "FAIL") Color.White else Color.Black)
                }
            }
        }
    }
}
