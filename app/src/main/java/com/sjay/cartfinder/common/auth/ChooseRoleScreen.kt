package com.sjay.cartfinder.common.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@Composable
fun ChooseRoleScreen(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isSystemInDarkTheme()) Color.Black else PrimaryOrange)
    ) {
        // Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .background(
                        if (isSystemInDarkTheme()) Color.White else Color.Transparent, 
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(if (isSystemInDarkTheme()) 8.dp else 0.dp),
                contentAlignment = Alignment.Center
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.sjay.cartfinder.R.drawable.logotext),
                    contentDescription = "CartFinder",
                    modifier = Modifier.height(48.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSystemInDarkTheme()) Color(0xFF1E1E1E) else Color(0xFFFDE6C8)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "CHOOSE YOUR ROLE",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ARE YOU HERE FOR FOOD OR PROFESSIONAL TOOLS?",
                        fontSize = 10.sp,
                        color = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RoleCard(
                        title = "I'm Looking For Street Food",
                        buttonText = "FIND CARTS",
                        icon = Icons.Outlined.Person,
                        onClick = { navController.navigate(Screen.Login.createRoute("Customer")) } // Navigate to Customer login/flow
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RoleCard(
                        title = "I'm Street Food Vendor",
                        buttonText = "VENDOR LOGIN",
                        icon = Icons.Outlined.Restaurant,
                        onClick = { navController.navigate(Screen.Login.createRoute("Vendor")) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RoleCard(
                        title = "I'm Public Health Inspector",
                        buttonText = "PHI LOGIN",
                        icon = Icons.Outlined.Shield,
                        onClick = { navController.navigate(Screen.Login.createRoute("PHI")) }
                    )
                    
                    
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun RoleCard(title: String, buttonText: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.DarkGray else Color.White),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryOrange,
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFFFF3E0), RoundedCornerShape(24.dp))
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = title, fontWeight = FontWeight.Medium, color = if (androidx.compose.foundation.isSystemInDarkTheme()) Color.White else Color.Black)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6A350)),
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = buttonText, color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
