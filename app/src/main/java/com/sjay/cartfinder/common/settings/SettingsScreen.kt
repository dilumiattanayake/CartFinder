package com.sjay.cartfinder.common.settings

import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.sjay.cartfinder.common.rememberSafeImagePainter
import com.sjay.cartfinder.core.navigation.BottomNavigationBar
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.shop.ShopState
import com.sjay.cartfinder.shop.ShopViewModel
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@Composable
fun SettingsScreen(navController: NavController, role: String) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController = navController, role = role)
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))) {
            when (role.lowercase()) {
                "vendor" -> VendorSettingsScreen(navController, currentUser)
                "phi" -> PhiProfileScreen(navController, currentUser)
                else -> CustomerProfileScreen(navController, currentUser)
            }
        }
    }
}

@Composable
fun VendorSettingsScreen(navController: NavController, currentUser: FirebaseUser?) {
    val viewModel: ShopViewModel = viewModel()
    val shopState by viewModel.shopState.collectAsState()

    LaunchedEffect(currentUser?.uid) {
        currentUser?.uid?.let { viewModel.loadVendorShop(it) }
    }

    val stall = (shopState as? ShopState.Success)?.stall

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            if (stall?.imageUrl != null) {
                Image(
                    painter = rememberSafeImagePainter(stall.imageUrl),
                    contentDescription = "Cover Image",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Store, contentDescription = null, tint = Color.White, modifier = Modifier.size(64.dp))
                }
            }

            // Top bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
            }

            // Overlay elements
            Row(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Row(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.CameraAlt, contentDescription = null, tint = PrimaryOrange, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Tap photo to update", color = Color.White, fontSize = 12.sp)
                }
                
                Text(
                    text = "Stall ID: #${stall?.id?.takeLast(6)?.uppercase() ?: "UNKNOWN"}",
                    color = Color.White,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .background(Color(0xFF00C853), RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Settings", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(8.dp))
        Text(stall?.name ?: "Unknown Stall", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
        Text(currentUser?.email ?: "", fontSize = 14.sp, color = Color(0xFFFF5252))

        Spacer(modifier = Modifier.height(24.dp))

        SettingsItem(Icons.Filled.Person, "Account")
        SettingsItem(Icons.Filled.Notifications, "Notifications")
        SettingsItem(Icons.Filled.Description, "Report")
        SettingsItem(Icons.Filled.Lock, "Privacy Policy")
        SettingsItem(Icons.Filled.Info, "About")
        SettingsItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Logout",
            isDestructive = true,
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Launch.route) { popUpTo(0) }
            }
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.ShoppingCart, contentDescription = null, tint = Color.Gray)
            Spacer(modifier = Modifier.width(8.dp))
            Text("CartFinder", color = Color.Gray, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CustomerProfileScreen(navController: NavController, currentUser: FirebaseUser?) {
    ProfileBaseScreen(
        navController = navController,
        currentUser = currentUser,
        title = "My Profile",
        badgeText = "✨ Foodie Level 3 ✨",
        stat1Value = "54", stat1Label = "REVIEWS",
        stat2Value = "12", stat2Label = "BOOKMARKS",
        stat3Value = "8", stat3Label = "COMPLAINTS",
        stat3Color = Color(0xFFE53935)
    ) {
        SectionTitle(Icons.Filled.VerifiedUser, "HEALTH & SAFETY SETTINGS")
        SettingsItem(Icons.Filled.CheckCircle, "Min. Hygiene Rating Level", label = "3.5★ +", iconTint = Color(0xFF4CAF50))
        SettingsItem(Icons.Filled.Verified, "Only Show PHI-Certified Carts", hasSwitch = true, switchState = true, iconTint = Color(0xFF4CAF50))
        
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle(Icons.Filled.Menu, "MY APP ACTIVITY")
        SettingsItem(Icons.Filled.Star, "My Reviews & Ratings", iconTint = PrimaryOrange)
        SettingsItem(Icons.Filled.Bookmark, "Saved Carts & Favorites", iconTint = PrimaryOrange)
        
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle(Icons.Filled.Person, "PERSONALIZATION")
        SettingsItem(Icons.Filled.Book, "Dietary & Food Preferences", label = "Halal / Veg", iconTint = PrimaryOrange)
        SettingsItem(Icons.Filled.Notifications, "Notification Preferences")
        
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle(Icons.Filled.Settings, "ACCOUNT ACTIONS")
        SettingsItem(Icons.Filled.VpnKey, "Change Password")
        SettingsItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Log Out",
            isDestructive = true,
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Launch.route) { popUpTo(0) }
            }
        )
    }
}

@Composable
fun PhiProfileScreen(navController: NavController, currentUser: FirebaseUser?) {
    ProfileBaseScreen(
        navController = navController,
        currentUser = currentUser,
        title = "PHI Officer Profile",
        badgeText = "Senior Public Health Inspector • MOH Sector 08",
        statusText = "Active Field Duty • Kaduwela & SLIIT Perimeter",
        stat1Value = "14", stat1Label = "MONITORED STALLS",
        stat2Value = "03", stat2Label = "URGENT ALERTS", stat2Color = Color(0xFFE53935),
        stat3Value = "98%", stat3Label = "COMPLIANCE", stat3Color = Color(0xFF4CAF50)
    ) {
        SectionTitle(Icons.Filled.Warning, "HYGIENE ALERTS & FLAGS")
        SettingsItem(Icons.Filled.QrCodeScanner, "Quick Stall Scan", label = "Scan Badge", labelColor = Color.White, labelBackground = PrimaryOrange, iconTint = PrimaryOrange)
        SettingsItem(Icons.Filled.Update, "Show Pending Re-inspections", hasSwitch = true, switchState = true, iconTint = Color(0xFF4CAF50))
        
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle(Icons.Filled.Build, "TOOLS & ACTIVITY")
        SettingsItem(Icons.Filled.FactCheck, "PHI Field Protocol & Kit", iconTint = Color(0xFF2196F3))
        SettingsItem(Icons.Filled.Folder, "MOH Sector 08 Audit Dossier", iconTint = PrimaryOrange)
        
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle(Icons.Filled.Person, "PERSONALIZATION")
        SettingsItem(Icons.Filled.Receipt, "Spot Fine & Penalty Receipts", iconTint = PrimaryOrange)
        SettingsItem(Icons.Filled.Notifications, "Notification Preferences")
        
        Spacer(modifier = Modifier.height(16.dp))
        SectionTitle(Icons.Filled.Settings, "ACCOUNT ACTIONS")
        SettingsItem(Icons.Filled.VpnKey, "Change Password")
        SettingsItem(
            icon = Icons.AutoMirrored.Filled.ExitToApp,
            title = "Log Out",
            isDestructive = true,
            onClick = {
                FirebaseAuth.getInstance().signOut()
                navController.navigate(Screen.Launch.route) { popUpTo(0) }
            }
        )
    }
}

@Composable
fun ProfileBaseScreen(
    navController: NavController,
    currentUser: FirebaseUser?,
    title: String,
    badgeText: String,
    statusText: String? = null,
    stat1Value: String, stat1Label: String, stat1Color: Color = Color.Black,
    stat2Value: String, stat2Label: String, stat2Color: Color = Color.Black,
    stat3Value: String, stat3Label: String, stat3Color: Color = Color.Black,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Orange Background
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        color = PrimaryOrange,
                        shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                    )
            )

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
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                IconButton(
                    onClick = { },
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), CircleShape)
                        .size(40.dp)
                ) {
                    Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                }
            }

            // Profile Info
            Column(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 70.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
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
                    // Edit Icon
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = (-4).dp, y = (-4).dp)
                            .background(PrimaryOrange, CircleShape)
                            .padding(4.dp)
                    ) {
                        Icon(Icons.Filled.Edit, contentDescription = "Edit", tint = Color.White, modifier = Modifier.size(14.dp))
                    }
                    // Status dot
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset(x = 8.dp, y = 8.dp)
                            .size(14.dp)
                            .background(Color(0xFF4CAF50), CircleShape)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = badgeText,
                    color = PrimaryOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                Text(currentUser?.displayName ?: "User Name", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)

                if (statusText != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 4.dp)
                    ) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), CircleShape))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(statusText, color = Color.White, fontSize = 12.sp)
                    }
                }
            }

            // Stats Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 20.dp),
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
                    StatItem(stat1Value, stat1Label, stat1Color)
                    Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                    StatItem(stat2Value, stat2Label, stat2Color)
                    Divider(modifier = Modifier.height(40.dp).width(1.dp), color = Color.LightGray)
                    StatItem(stat3Value, stat3Label, stat3Color)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            content()
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun StatItem(value: String, label: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = color)
        Text(label, fontSize = 10.sp, color = Color.Gray, fontWeight = FontWeight.SemiBold)
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
