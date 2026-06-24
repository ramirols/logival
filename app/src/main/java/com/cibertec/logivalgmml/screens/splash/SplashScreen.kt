package com.cibertec.logivalgmml.screens.splash

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cibertec.logivalgmml.R
import com.cibertec.logivalgmml.navigation.Routes
import com.cibertec.logivalgmml.repositories.AuthRepository
import com.cibertec.logivalgmml.ui.theme.LogiValBackground
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValText
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val authRepository = remember { AuthRepository() }
    var visible by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.splash_truck)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE
    )

    LaunchedEffect(Unit) {
        visible = true
        delay(2200)

        val prefs = context.getSharedPreferences("logival_prefs", 0)
        val onboardingDone = prefs.getBoolean("onboarding_done", false)

        val destination = when {
            authRepository.isLoggedIn() -> Routes.HOME
            !onboardingDone -> Routes.ONBOARDING
            else -> Routes.LOGIN
        }

        navController.navigate(destination) {
            popUpTo(Routes.SPLASH) { inclusive = true }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = LogiValBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            LogiValBackground,
                            LogiValBackground,
                            LogiValGreen.copy(alpha = 0.05f)
                        )
                    )
                )
        ) {
            SplashWaveBackground()

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(900)) +
                        slideInVertically(
                            animationSpec = tween(900),
                            initialOffsetY = { it / 6 }
                        )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 30.dp, vertical = 54.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.weight(0.65f))

                    Text(
                        text = "LogiVal GMML",
                        fontSize = 30.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = LogiValGreenDark,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Control inteligente de\naccesos vehiculares",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = LogiValText,
                        textAlign = TextAlign.Center,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(34.dp))

                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.size(245.dp)
                    )

                    Spacer(modifier = Modifier.height(30.dp))

                    CircularProgressIndicator(
                        color = LogiValGreen.copy(alpha = 0.40f),
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(42.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun SplashWaveBackground() {
    Canvas(
        modifier = Modifier.fillMaxSize()
    ) {
        val w = size.width
        val h = size.height

        val topFill = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            cubicTo(w * 0.75f, h * 0.05f, w * 0.45f, h * 0.02f, 0f, h * 0.12f)
            close()
        }

        drawPath(
            path = topFill,
            color = LogiValGreen.copy(alpha = 0.045f)
        )

        val bottomFill = Path().apply {
            moveTo(0f, h)
            lineTo(w, h)
            lineTo(w, h * 0.70f)
            cubicTo(w * 0.70f, h * 0.88f, w * 0.35f, h * 0.78f, 0f, h * 0.94f)
            close()
        }

        drawPath(
            path = bottomFill,
            color = LogiValGreen.copy(alpha = 0.055f)
        )

        repeat(11) { i ->
            val offset = i * 12f

            val topLine = Path().apply {
                moveTo(-80f, 35f + offset)
                cubicTo(
                    w * 0.25f,
                    -5f + offset,
                    w * 0.58f,
                    95f + offset,
                    w + 80f,
                    45f + offset
                )
            }

            drawPath(
                path = topLine,
                color = LogiValGreen.copy(alpha = 0.16f),
                style = Stroke(width = 1.1f)
            )
        }

        repeat(13) { i ->
            val offset = i * 11f

            val bottomLine = Path().apply {
                moveTo(-100f, h - 210f + offset)
                cubicTo(
                    w * 0.25f,
                    h - 120f + offset,
                    w * 0.62f,
                    h - 250f + offset,
                    w + 100f,
                    h - 150f + offset
                )
            }

            drawPath(
                path = bottomLine,
                color = LogiValGreen.copy(alpha = 0.16f),
                style = Stroke(width = 1.05f)
            )
        }
    }
}