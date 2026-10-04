package com.sjay.cartfinder.common.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.R
import com.sjay.cartfinder.core.navigation.Screen

@Composable
fun OnboardingScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Image Placeholder (In a real app, use the actual illustration)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color(0xFFFFF3E0)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logotext), // Placeholder
                contentDescription = "Onboarding Illustration",
                modifier = Modifier.size(200.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Help others eat safe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF333333)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Leave reviews and flag issues — your\nfeedback keeps the ratings honest.",
            fontSize = 16.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { navController.navigate(Screen.ChooseRole.route) },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(50.dp)
        ) {
            Text(text = "GET STARTED", fontWeight = FontWeight.Bold, color = Color.White)
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        // Pagination dots
        Row(horizontalArrangement = Arrangement.Center) {
            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(8.dp).background(Color.LightGray, RoundedCornerShape(4.dp)))
            Spacer(modifier = Modifier.width(8.dp))
            Box(modifier = Modifier.size(8.dp).background(Color(0xFF4CAF50), RoundedCornerShape(4.dp)))
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
