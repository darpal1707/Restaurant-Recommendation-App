package com.darpal.foodlabrinthnew.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = Crimson40,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    primaryContainer = Crimson90,
    onPrimaryContainer = Crimson10,
    secondary = Rose40,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    secondaryContainer = Rose90,
    onSecondaryContainer = Rose10,
    tertiary = Teal40,
    onTertiary = androidx.compose.ui.graphics.Color.White,
    tertiaryContainer = Teal90,
    onTertiaryContainer = Teal10,
    background = Neutral99,
    onBackground = Neutral10,
    surface = Neutral99,
    onSurface = Neutral10,
    surfaceVariant = NeutralVariant90,
    onSurfaceVariant = NeutralVariant30,
    outline = NeutralVariant50,
    outlineVariant = NeutralVariant80,
)

private val DarkColors = darkColorScheme(
    primary = Crimson80,
    onPrimary = Crimson10,
    primaryContainer = Crimson30,
    onPrimaryContainer = Crimson90,
    secondary = Rose80,
    onSecondary = Rose10,
    secondaryContainer = Rose30,
    onSecondaryContainer = Rose90,
    tertiary = Teal80,
    onTertiary = Teal10,
    tertiaryContainer = Teal30,
    onTertiaryContainer = Teal90,
    background = Neutral6,
    onBackground = Neutral90,
    surface = Neutral6,
    onSurface = Neutral90,
    surfaceVariant = NeutralVariant30,
    onSurfaceVariant = NeutralVariant80,
    outline = NeutralVariant60,
    outlineVariant = NeutralVariant30,
)

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp),
)

@Composable
fun FoodLabrinthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Off by default so the brand colours are kept; flip on to follow the wallpaper (Android 12+).
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColors
        else -> LightColors
    }
    CompositionLocalProvider(LocalReducedMotion provides rememberReducedMotion()) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            shapes = AppShapes,
            content = content,
        )
    }
}
