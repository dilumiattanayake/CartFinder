package com.sjay.cartfinder.phi

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StallPhiDetailsScreen(
    navController: NavController,
    stallId: String,
    phiViewModel: PhiViewModel = viewModel()
) {
    val phiState by phiViewModel.phiState.collectAsState()
    val stall by phiViewModel.stallState.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    
    var showInspectionDialog by remember { mutableStateOf(false) }
    var inspectionScore by remember { mutableStateOf("") }
    var inspectionResult by remember { mutableStateOf("PASS") }
    var inspectionNotes by remember { mutableStateOf("") }
    
    LaunchedEffect(stallId) {
        phiViewModel.loadStall(stallId)
        phiViewModel.loadCertificate(stallId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stall?.name?.let { "$it - PHI Details" } ?: "PHI Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (val state = phiState) {
                is PhiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
                is PhiState.CertificateData -> {
                    Text("Certificate Status", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (state.certificate != null) {
                        Text("Status: ${state.certificate.status}")
                        if (state.certificate.status == "PENDING_REQUEST") {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                Button(onClick = { phiViewModel.issueCertificate(stallId, "A") }) { Text("Approve (Grade A)") }
                                Button(onClick = { phiViewModel.issueCertificate(stallId, "B") }) { Text("Approve (Grade B)") }
                            }
                        } else {
                        Text("Grade: ${state.certificate.grade}")
                            Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Button(onClick = { navController.navigate(Screen.PhiCertificate.createRoute(stallId, stall?.name ?: "Stall")) }) {
                                    Text("View Certificate")
                                }
                                OutlinedButton(onClick = { phiViewModel.deleteCertificate(stallId) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                                    Text("Revoke/Delete")
                                }
                            }
                        }
                    } else {
                        Text("No Certificate Record")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = { navController.navigate(Screen.PhiSpotAudit.createRoute(stallId)) }, modifier = Modifier.fillMaxWidth()) {
                        Text("Perform Spot Audit")
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(onClick = { phiViewModel.loadInspections(stallId) }, modifier = Modifier.fillMaxWidth()) {
                        Text("View Inspections")
                    }
                }
                is PhiState.InspectionData -> {
                    Text("Inspections", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    if (state.inspections.isEmpty()) {
                        Text("No inspections found.")
                    } else {
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(state.inspections) { insp ->
                                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        val formatter = SimpleDateFormat("dd MMM yyyy", java.util.Locale.US)
                                        Text("Date: ${formatter.format(Date(insp.inspectionDate))}")
                                        Text("Score: ${insp.score}")
                                        Text("Result: ${insp.result}")
                                        Text("Notes: ${insp.notes}")
                                        
                                        Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                                            TextButton(onClick = {
                                                // Pre-fill dialog for update
                                                inspectionScore = insp.score.toString()
                                                inspectionResult = insp.result
                                                inspectionNotes = insp.notes
                                                // We'd need an ID to update. We'll just delete and create a new one to simulate update.
                                                phiViewModel.deleteInspection(stallId, insp.id)
                                                showInspectionDialog = true
                                            }) {
                                                Text("Edit")
                                            }
                                            TextButton(onClick = {
                                                phiViewModel.deleteInspection(stallId, insp.id)
                                            }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                                                Text("Delete")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { phiViewModel.loadCertificate(stallId) }) { Text("Back to Certificate") }
                }
                is PhiState.Error -> {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
                is PhiState.Success -> {
                    Text("Action Successful!", color = androidx.compose.ui.graphics.Color.Green)
                }
                else -> {}
            }
        }
    }
    
    if (showInspectionDialog) {
        AlertDialog(
            onDismissRequest = { showInspectionDialog = false },
            title = { Text("New Inspection") },
            text = {
                Column {
                    OutlinedTextField(
                        value = inspectionScore,
                        onValueChange = { inspectionScore = it },
                        label = { Text("Score (0-100)") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inspectionResult,
                        onValueChange = { inspectionResult = it },
                        label = { Text("Result (e.g., PASS, FAIL)") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inspectionNotes,
                        onValueChange = { inspectionNotes = it },
                        label = { Text("Notes") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    phiViewModel.addInspection(
                        stallId = stallId,
                        inspectorId = currentUserId,
                        score = inspectionScore.toIntOrNull() ?: 0,
                        resultText = inspectionResult,
                        notes = inspectionNotes
                    )
                    showInspectionDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showInspectionDialog = false }) { Text("Cancel") }
            }
        )
    }
}
