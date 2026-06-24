package com.cibertec.logivalgmml.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = LogiValGreenLight,
    onPrimary = LogiValText,
    secondary = LogiValGreen,
    onSecondary = Color.White,
    background = Color(0xFF101510),
    onBackground = Color.White,
    surface = Color(0xFF182018),
    onSurface = Color.White,
    error = LogiValRed
)

private val LightColorScheme = lightColorScheme(
    primary = LogiValGreen,
    onPrimary = Color.White,
    secondary = LogiValGreenDark,
    onSecondary = Color.White,
    tertiary = LogiValYellow,
    background = LogiValBackground,
    onBackground = LogiValText,
    surface = Color.White,
    onSurface = LogiValText,
    error = LogiValRed
)

@Composable
fun LogiValGMMLTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}