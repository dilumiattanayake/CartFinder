package com.sjay.cartfinder.common.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.sjay.cartfinder.ui.theme.PrimaryOrange

@Composable
fun StaticPageScreen(navController: NavController, title: String, contentText: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF9F9F9))
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryOrange)
                .padding(16.dp)
                .padding(top = 16.dp),
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
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = contentText,
                fontSize = 16.sp,
                color = Color.DarkGray,
                lineHeight = 24.sp
            )
        }
    }
}

@Composable
fun AboutUsScreen(navController: NavController) {
    StaticPageScreen(
        navController = navController,
        title = "About Us",
        contentText = "Welcome to CartFinder!\n\n" +
                "Our mission is to bring the best street food right to your fingertips while ensuring hygiene, safety, and quality.\n\n" +
                "Whether you're a food lover craving an evening snack, a vendor looking to reach more customers, or a PHI officer monitoring health compliance, CartFinder connects the entire street food ecosystem in one seamless platform.\n\n" +
                "Thank you for being part of our journey!"
    )
}

@Composable
fun PrivacyPolicyScreen(navController: NavController) {
    StaticPageScreen(
        navController = navController,
        title = "Privacy Policy",
        contentText = "Privacy Policy\n\n" +
                "Last Updated: Today\n\n" +
                "1. Data Collection\n" +
                "We collect personal information such as your name, email address, and location data to provide our services and facilitate food orders.\n\n" +
                "2. Use of Data\n" +
                "Your location is used solely to show you nearby food carts. We do not sell your personal data to third parties.\n\n" +
                "3. Security\n" +
                "We employ industry-standard measures to protect your data, but no method of transmission over the internet is 100% secure.\n\n" +
                "4. Contact Us\n" +
                "If you have questions about this policy, please contact support@cartfinder.com."
    )
}

@Composable
fun TermsOfServiceScreen(navController: NavController) {
    StaticPageScreen(
        navController = navController,
        title = "Terms of Service",
        contentText = "Terms of Service\n\n" +
                "1. Acceptance of Terms\n" +
                "By creating an account and using CartFinder, you agree to comply with these terms.\n\n" +
                "2. Vendor Responsibilities\n" +
                "Vendors must provide accurate food descriptions, maintain required health and hygiene standards, and fulfill accepted orders.\n\n" +
                "3. Customer Responsibilities\n" +
                "Customers must provide accurate delivery locations and commit to paying for completed orders.\n\n" +
                "4. Termination\n" +
                "We reserve the right to suspend or terminate accounts that violate our terms or receive poor compliance ratings."
    )
}

@Composable
fun HelpSupportScreen(navController: NavController) {
    StaticPageScreen(
        navController = navController,
        title = "Help & Support",
        contentText = "Need Help?\n\n" +
                "If you are experiencing issues with the app, placing an order, or have feedback, we are here to help.\n\n" +
                "FAQs:\n" +
                "Q: How do I track my order?\n" +
                "A: Go to the 'Orders' tab to view real-time status.\n\n" +
                "Q: How do I report a hygiene issue?\n" +
                "A: Customers can file complaints from the stall's menu page. PHI officers will automatically be alerted.\n\n" +
                "Contact Us:\n" +
                "Email: support@cartfinder.com\n" +
                "Phone: +94 112 345 678"
    )
}
