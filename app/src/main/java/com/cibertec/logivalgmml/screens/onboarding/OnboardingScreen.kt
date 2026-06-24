package com.cibertec.logivalgmml.screens.onboarding

import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.cibertec.logivalgmml.R
import com.cibertec.logivalgmml.ui.theme.LogiValGreen
import com.cibertec.logivalgmml.ui.theme.LogiValGreenDark
import com.cibertec.logivalgmml.ui.theme.LogiValText

private val StitchBlue = Color(0xFF147BEF)
private val StitchDarkBlue = Color(0xFF172B45)
private val DotInactive = Color(0xFFD2D6DC)

data class OnboardingPage(
    val title: String,
    val description: String,
    @RawRes val lottieRes: Int,
    val titleColor: Color,
    val buttonColor: Color
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit
) {
    val context = LocalContext.current

    val pages = listOf(
        OnboardingPage(
            title = "Gestiona ingresos\nvehiculares",
            description = "Registra solicitudes de acceso al\nmercado de forma rápida y ordenada.",
            lottieRes = R.raw.onboarding_truck,
            titleColor = LogiValGreenDark,
            buttonColor = LogiValGreen
        ),
        OnboardingPage(
            title = "Valida con código QR",
            description = "El personal de control puede registrar\nentrada y salida desde la app.",
            lottieRes = R.raw.onboarding_qr,
            titleColor = StitchDarkBlue,
            buttonColor = StitchBlue
        ),
        OnboardingPage(
            title = "Controla incidencias y\npermanencia",
            description = "Monitorea vehículos dentro del\nmercado, tiempos e incidencias.",
            lottieRes = R.raw.onboarding_dashboard,
            titleColor = StitchDarkBlue,
            buttonColor = StitchBlue
        )
    )

    var currentPage by remember { mutableIntStateOf(0) }

    fun finishOnboarding() {
        context.getSharedPreferences("logival_prefs", 0)
            .edit()
            .putBoolean("onboarding_done", true)
            .apply()

        onFinish()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(horizontal = 18.dp, vertical = 22.dp)
        ) {
            if (currentPage == 0) {
                TextButton(
                    onClick = { finishOnboarding() },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "Omitir",
                        color = LogiValGreenDark,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(58.dp))

                AnimatedContent(
                    targetState = pages[currentPage],
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(220))
                    },
                    label = "onboarding_content"
                ) { page ->
                    OnboardingPageContent(
                        page = page,
                        currentPage = currentPage
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                OnboardingDots(
                    total = pages.size,
                    selectedIndex = currentPage,
                    activeColor = pages[currentPage].buttonColor
                )

                Spacer(modifier = Modifier.height(28.dp))

                Button(
                    onClick = {
                        if (currentPage < pages.lastIndex) {
                            currentPage++
                        } else {
                            finishOnboarding()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(9.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = pages[currentPage].buttonColor,
                        contentColor = Color.White
                    ),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 0.dp
                    )
                ) {
                    Text(
                        text = if (currentPage < pages.lastIndex) "Siguiente" else "Comenzar",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (currentPage == 1) {
                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(
                        onClick = { finishOnboarding() }
                    ) {
                        Text(
                            text = "Omitir",
                            color = StitchBlue,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    currentPage: Int
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(page.lottieRes)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = Int.MAX_VALUE
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(
                when (currentPage) {
                    0 -> 255.dp
                    1 -> 235.dp
                    else -> 260.dp
                }
            )
        )

        Spacer(
            modifier = Modifier.height(
                when (currentPage) {
                    0 -> 28.dp
                    1 -> 30.dp
                    else -> 26.dp
                }
            )
        )

        Text(
            text = page.title,
            color = page.titleColor,
            textAlign = TextAlign.Center,
            fontSize = 27.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = page.description,
            color = LogiValText.copy(alpha = 0.82f),
            textAlign = TextAlign.Center,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
    }
}

@Composable
private fun OnboardingDots(
    total: Int,
    selectedIndex: Int,
    activeColor: Color
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(
                        if (index == selectedIndex) activeColor else DotInactive
                    )
            )
        }
    }
}