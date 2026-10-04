package com.sjay.cartfinder.common.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
            .background(PrimaryOrange)
    ) {
        // Back Button
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(16.dp)
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "CartFinder",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .padding(bottom = 32.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFDE6C8))
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
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "ARE YOU HERE FOR FOOD OR PROFESSIONAL TOOLS?",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RoleCard(
                        title = "I'm Looking For Street Food",
                        buttonText = "FIND CARTS",
                        icon = Icons.Outlined.Person,
                        onClick = { navController.navigate(Screen.Login.route) } // Navigate to Customer login/flow
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RoleCard(
                        title = "I'm Street Food Vendor",
                        buttonText = "VENDOR LOGIN",
                        icon = Icons.Outlined.Restaurant,
                        onClick = { navController.navigate(Screen.Login.route) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    RoleCard(
                        title = "I'm Public Health Inspector",
                        buttonText = "PHI LOGIN",
                        icon = Icons.Outlined.Shield,
                        onClick = { navController.navigate(Screen.Login.route) }
                    )
                    
                    Spacer(modifier = Modifier.weight(1f))
                    
                    Text(
                        text = "Continue as Guest",
                        fontWeight = FontWeight.Bold,
                        textDecoration = TextDecoration.Underline,
                        modifier = Modifier.clickable { /* Handle Guest */ }
                    )
                }
            }
        }
    }
}

@Composable
fun RoleCard(title: String, buttonText: String, icon: ImageVector, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
                Text(text = title, fontWeight = FontWeight.Medium)
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
