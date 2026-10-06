package com.sjay.cartfinder.common.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sjay.cartfinder.core.navigation.BottomNavigationBar
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@Composable
fun SettingsScreen(navController: NavController, role: String) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val viewModel: SettingsViewModel = viewModel()
    
    val customerStats by viewModel.customerStats.collectAsState()
    val vendorStats by viewModel.vendorStats.collectAsState()
    val phiStats by viewModel.phiStats.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        val uid = currentUser?.uid ?: return@LaunchedEffect
        when (role.lowercase()) {
            "vendor" -> viewModel.loadVendorStats(uid)
            "phi" -> viewModel.loadPhiStats()
            else -> viewModel.loadCustomerStats(uid)
        }
    }

    val stats = when (role.lowercase()) {
        "vendor" -> listOf(
            "PRODUCTS" to "${vendorStats.products}",
            "ORDERS" to "${vendorStats.orders}",
            "REVIEWS" to "${vendorStats.reviews}"
        )
        "phi" -> listOf(
            "INSPECTIONS" to "${phiStats.inspections}",
            "CERTIFIED" to "${phiStats.certified}"
        )
        else -> listOf(
            "ORDERS" to "${customerStats.orders}",
            "REVIEWS" to "${customerStats.reviews}",
            "CARTS" to "${customerStats.carts}"
        )
    }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, role = role)
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))) {
            UnifiedProfileScreen(navController, currentUser, role, stats)
        }
    }
}

@Composable
fun UnifiedProfileScreen(
    navController: NavController,
    currentUser: FirebaseUser?,
    role: String,
    stats: List<Pair<String, String>>
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top section
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = PrimaryOrange,
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
                .padding(bottom = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                    Text("Profile", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    IconButton(
                        onClick = { },
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Profile Image
                Box {
                    Icon(
                        Icons.Filled.AccountCircle,
                        contentDescription = "Profile Pic",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                        tint = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name and Role
                Text(
                    text = currentUser?.displayName ?: "User Name", 
                    color = Color.White, 
                    fontSize = 24.sp, 
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = role.uppercase(),
                    color = PrimaryOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Stats Row
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                stats.forEachIndexed { index, stat ->
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                        Text(stat.second, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(stat.first, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                    }
                    if (index < stats.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier
                                .height(40.dp)
                                .width(1.dp), 
                            color = Color.LightGray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Links
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            SectionTitle(Icons.Filled.Settings, "ACCOUNT")
            SettingsItem(Icons.Filled.Person, "Edit Profile", onClick = { navController.navigate(Screen.EditProfile.route) })
            SettingsItem(Icons.Filled.VpnKey, "Change Password", onClick = { navController.navigate(Screen.ChangePassword.route) })
            
            Spacer(modifier = Modifier.height(16.dp))
            SectionTitle(Icons.Filled.List, "PAGES & LINKS")
            SettingsItem(Icons.Filled.Info, "About Us", onClick = { navController.navigate(Screen.AboutUs.route) })
            SettingsItem(Icons.Filled.Lock, "Privacy Policy", onClick = { navController.navigate(Screen.PrivacyPolicy.route) })
            SettingsItem(Icons.Filled.Description, "Terms of Service", onClick = { navController.navigate(Screen.TermsOfService.route) })
            SettingsItem(Icons.Filled.HeadsetMic, "Help & Support", onClick = { navController.navigate(Screen.HelpSupport.route) })
            
            Spacer(modifier = Modifier.height(24.dp))
            val context = androidx.compose.ui.platform.LocalContext.current
            SettingsItem(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                title = "Log Out",
                isDestructive = true,
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    val gso = com.google.android.gms.auth.api.signin.GoogleSignInOptions.Builder(
                        com.google.android.gms.auth.api.signin.GoogleSignInOptions.DEFAULT_SIGN_IN
                    ).build()
                    com.google.android.gms.auth.api.signin.GoogleSignIn.getClient(context, gso).signOut()
                    navController.navigate(Screen.Launch.route) { popUpTo(0) }
                }
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun SectionTitle(icon: ImageVector, title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    label: String? = null,
    labelColor: Color = Color(0xFF4CAF50),
    labelBackground: Color = Color(0xFFE8F5E9),
    iconTint: Color = Color.Gray,
    hasSwitch: Boolean = false,
    switchState: Boolean = false,
    isDestructive: Boolean = false,
    onClick: () -> Unit = {}
) {
    val titleColor = if (isDestructive) Color(0xFFE53935) else Color.Black
    val effectiveIconTint = if (isDestructive) Color(0xFFE53935).copy(alpha = 0.2f) else iconTint.copy(alpha = 0.1f)
    val actualIconTint = if (isDestructive) Color(0xFFE53935) else iconTint

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(effectiveIconTint, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = actualIconTint, modifier = Modifier.size(24.dp))
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Text(
                text = title,
                color = titleColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            if (label != null) {
                Text(
                    text = label,
                    color = labelColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(labelBackground, RoundedCornerShape(16.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }

            if (hasSwitch) {
                var checked by remember { mutableStateOf(switchState) }
                Switch(
                    checked = checked,
                    onCheckedChange = { checked = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFF4CAF50))
                )
            } else if (!isDestructive) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = Color.LightGray)
            }
        }
    }
}
