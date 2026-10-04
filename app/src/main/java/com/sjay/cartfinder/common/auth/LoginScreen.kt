package com.sjay.cartfinder.common.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.core.navigation.Screen
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryOrange)
    ) {
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
            Text("Welcome", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
            Text("To", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
            Spacer(modifier = Modifier.height(8.dp))
            Text("CartFinder", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "VENDOR LOGIN",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF4A4A4A)
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text("Email", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("baco_saman@gmail.com", color = Color.Gray) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFE8DEB6),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                        Text("Password", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("*************", color = Color.Gray) },
                            visualTransformation = PasswordVisualTransformation(),
                            trailingIcon = {
                                Icon(Icons.Outlined.VisibilityOff, contentDescription = null, tint = PrimaryOrange)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color(0xFFE8DEB6),
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF9E86FF))
                        )
                        Text("Remember me", fontSize = 14.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { navController.navigate(Screen.ReviewList.route) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("LOGIN", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 18.sp)
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text("Forgot password ?", fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 48.dp)) {
                Divider(modifier = Modifier.weight(1f), color = Color.Black)
                Text(" Or ", modifier = Modifier.padding(horizontal = 8.dp), color = Color.Black)
                Divider(modifier = Modifier.weight(1f), color = Color.Black)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Social icons placeholders
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Box(modifier = Modifier.size(40.dp).background(Color.Red, RoundedCornerShape(20.dp))) // Google
                Box(modifier = Modifier.size(40.dp).background(Color.Blue, RoundedCornerShape(20.dp))) // FB
                Box(modifier = Modifier.size(40.dp).background(Color.White, RoundedCornerShape(20.dp))) // Github
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(modifier = Modifier.padding(bottom = 32.dp)) {
                Text("Don't have an account ? ", color = Color.White)
                Text(
                    "Signup", 
                    fontWeight = FontWeight.Bold, 
                    color = Color.Black,
                    modifier = Modifier.clickable { navController.navigate(Screen.SignUp.route) }
                )
            }
        }
    }
}
