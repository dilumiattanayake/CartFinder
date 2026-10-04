package com.sjay.cartfinder.common.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.sjay.cartfinder.ui.theme.PrimaryOrange
import com.sjay.cartfinder.R
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(navController: NavController, role: String = "Customer") {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var brn by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryOrange)
    ) {
        // Top section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(50))
            ) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }
        
        Row(modifier = Modifier.padding(start = 16.dp)) {
            Image(
                painter = painterResource(id = R.drawable.logotext),
                contentDescription = "Logo",
                modifier = Modifier.height(48.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        // White card section
        Card(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8F9FA))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "${role.uppercase()} SIGN UP",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.Black
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                CustomTextField("Full name", "baco saman", fullName) { fullName = it }
                CustomTextField("Email", "baco_saman@gmail.com", email) { email = it }
                CustomTextField("Business name", "kottu kade", businessName) { businessName = it }
                CustomTextField("Business Registration Number", "xxx-cc-xxx", brn) { brn = it }
                CustomTextField("Mobile Number", "77 xxx xxx", mobile) { mobile = it }
                CustomPasswordField("Password", password) { password = it }
                CustomPasswordField("Confirm Password", confirmPassword) { confirmPassword = it }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { /* Handle registration */ },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF6A350)),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Register", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 18.sp)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
fun CustomTextField(label: String, placeholder: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFE8DEB6),
                focusedContainerColor = Color(0xFFE8DEB6),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun CustomPasswordField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
        Text(label, fontWeight = FontWeight.Bold, color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text("*************", color = Color.Gray) },
            visualTransformation = PasswordVisualTransformation(),
            trailingIcon = {
                Icon(Icons.Outlined.VisibilityOff, contentDescription = null, tint = PrimaryOrange)
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color(0xFFE8DEB6),
                focusedContainerColor = Color(0xFFE8DEB6),
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent
            ),
            shape = RoundedCornerShape(12.dp)
        )
    }
}
