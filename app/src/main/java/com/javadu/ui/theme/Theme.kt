package com.javadu.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = JavaGreen,
    onPrimary = Color.Black,
    primaryContainer = JavaGreenDark,
    onPrimaryContainer = Color.White,
    secondary = JavaGreenLight,
    onSecondary = Color.Black,
    secondaryContainer = JavaGreenDark,
    onSecondaryContainer = Color.White,
    tertiary = WarningYellow,
    onTertiary = Color.Black,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    error = ErrorRed,
    onError = Color.White,
    outline = TextSecondaryDark
)

@Composable
fun JavaDuoAppTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
