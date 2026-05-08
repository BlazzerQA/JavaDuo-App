package com.javadu.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

private val LightColorScheme = lightColorScheme(
    primary = JavaGreenDark,
    onPrimary = Color.White,
    primaryContainer = JavaGreenLight,
    onPrimaryContainer = Color.Black,
    secondary = JavaGreen,
    onSecondary = Color.Black,
    secondaryContainer = JavaGreenLight,
    onSecondaryContainer = Color.Black,
    tertiary = WarningYellow,
    onTertiary = Color.Black,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    error = ErrorRed,
    onError = Color.White,
    outline = TextSecondaryLight
)

@Composable
fun JavaDuoAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
