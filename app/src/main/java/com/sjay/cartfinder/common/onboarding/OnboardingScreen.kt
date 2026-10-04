package com.sjay.cartfinder.common.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
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
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(navController: NavController) {
    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.ink,
            title = "Find the Best Street Food",
            description = "Discover amazing food carts around you easily and quickly."
        ),
        OnboardingPageData(
            imageRes = R.drawable.ink_shape,
            title = "Help Others Eat Safe",
            description = "Leave reviews and flag issues — your feedback keeps the ratings honest."
        ),
        OnboardingPageData(
            imageRes = R.drawable.ink,
            title = "Track Your Favorites",
            description = "Save your favorite carts so you never lose track of a great meal."
        ),
        OnboardingPageData(
            imageRes = R.drawable.ink_shape,
            title = "Join the Community",
            description = "Connect with other street food lovers and share your experiences."
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color(0xFFFFF3E0)),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = pages[page].imageRes),
                        contentDescription = "Onboarding Illustration",
                        modifier = Modifier.size(250.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = pages[page].title,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pages[page].description,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Pagination dots
        Row(
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(pages.size) { iteration ->
                val color = if (pagerState.currentPage == iteration) Color(0xFF4CAF50) else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .background(color, RoundedCornerShape(4.dp))
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (pagerState.currentPage == pages.size - 1) {
                    navController.navigate(Screen.ChooseRole.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(50.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == pages.size - 1) "GET STARTED" else "NEXT",
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
