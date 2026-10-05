package com.sjay.cartfinder.common.auth

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.sjay.cartfinder.R
import com.sjay.cartfinder.core.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    role: String = "Customer",
    viewModel: AuthViewModel = viewModel()
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var rememberMe by remember { mutableStateOf(false) }

    val authState by viewModel.authState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                Toast.makeText(context, (authState as AuthState.Success).message, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
                if (role.equals("Vendor", ignoreCase = true)) {
                    navController.navigate(Screen.VendorDashboard.route) {
                        popUpTo(0)
                    }
                } else {
                    navController.navigate(Screen.CustomerDashboard.route) {
                        popUpTo(0)
                    }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).error, Toast.LENGTH_SHORT).show()
                viewModel.resetState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
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
                .padding(top = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logotext), // Reusing placeholder
                contentDescription = "Logo",
                modifier = Modifier.size(100.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${role.uppercase()} LOGIN",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Email Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Email address", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = { Text("Example@gmail.com", color = Color.Gray) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password Field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Password", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onBackground)
                            Text("Forgot password?", color = Color.Gray, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextField(
                            value = password,
                            onValueChange = { password = it },
                            placeholder = { Text("*************", color = Color.Gray) },
                            visualTransformation = PasswordVisualTransformation(),
                            trailingIcon = {
                                Icon(Icons.Outlined.VisibilityOff, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            colors = TextFieldDefaults.colors(
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Remember me
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it },
                            colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary)
                        )
                        Text("Remember me", color = MaterialTheme.colorScheme.onBackground)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Login Button
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    } else {
                        Button(
                            onClick = { viewModel.login(email, password) },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(24.dp)
                        ) {
                            Text("Login", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimary, fontSize = 18.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                        Text(" or continue with ", color = Color.Gray, modifier = Modifier.padding(horizontal = 8.dp))
                        HorizontalDivider(modifier = Modifier.weight(1f), thickness = 1.dp, color = Color.LightGray)
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text("Don't have an account? ", color = Color.Gray)
                        Text(
                            "Signup", 
                            fontWeight = FontWeight.Bold, 
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.clickable { navController.navigate(Screen.SignUp.createRoute(role)) }
                        )
                    }
                }
            }
        }
    }
}
