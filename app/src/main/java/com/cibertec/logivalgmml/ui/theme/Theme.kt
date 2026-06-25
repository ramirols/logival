package com.cibertec.logivalgmml.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = LogiValGreen,
    onPrimary = LogiValWhite,

    secondary = LogiValGreenDark,
    onSecondary = LogiValWhite,

    tertiary = LogiValYellow,
    onTertiary = LogiValText,

    background = LogiValBackground,
    onBackground = LogiValText,

    surface = LogiValSurface,
    onSurface = LogiValText,

    surfaceVariant = LogiValGreenLight,
    onSurfaceVariant = LogiValTextSoft,

    outline = LogiValBorder,

    error = LogiValRed,
    onError = LogiValWhite
)

private val DarkColorScheme = darkColorScheme(
    primary = LogiValGreenLight,
    onPrimary = LogiValGreenDark,

    secondary = LogiValGreen,
    onSecondary = LogiValWhite,

    tertiary = LogiValYellow,
    onTertiary = LogiValText,

    background = Color(0xFF0F1713),
    onBackground = Color(0xFFF4F7F5),

    surface = Color(0xFF16211B),
    onSurface = Color(0xFFF4F7F5),

    surfaceVariant = Color(0xFF1E2D25),
    onSurfaceVariant = Color(0xFFC9D8D0),

    outline = Color(0xFF2C4035),

    error = LogiValRed,
    onError = LogiValWhite
)

@Composable
fun LogiValGMMLTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        DarkColorScheme
    } else {
        LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}