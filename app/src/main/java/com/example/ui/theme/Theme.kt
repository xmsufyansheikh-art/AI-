package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

enum class AppThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}

private val DarkColorScheme = darkColorScheme(
    primary = ElectricSkyBlue,
    onPrimary = MidnightNavy,
    primaryContainer = DarkBlueContainer,
    onPrimaryContainer = ElectricSkyBlue,
    secondary = RoyalAzure,
    onSecondary = MidnightNavy,
    secondaryContainer = DeepSapphire,
    onSecondaryContainer = TextWhite,
    tertiary = GulaboRose,
    onTertiary = TextWhite,
    background = MidnightNavy,
    onBackground = TextWhite,
    surface = DarkBlueSurface,
    onSurface = TextWhite,
    surfaceVariant = DarkBlueElevated,
    onSurfaceVariant = TextMuted,
    outline = DarkBlueBorder
)

private val LightColorScheme = lightColorScheme(
    primary = LightBluePrimary,
    onPrimary = TextWhite,
    primaryContainer = LightBlueElevated,
    onPrimaryContainer = LightBluePrimary,
    secondary = LightBlueSecondary,
    onSecondary = TextWhite,
    secondaryContainer = Color(0xFFDBEAFE),
    onSecondaryContainer = LightBlueSecondary,
    tertiary = GulaboRose,
    onTertiary = TextWhite,
    background = LightBlueBackground,
    onBackground = TextDark,
    surface = LightBlueSurface,
    onSurface = TextDark,
    surfaceVariant = LightBlueElevated,
    onSurfaceVariant = TextDarkMuted,
    outline = LightBlueBorder
)

@Composable
fun GulaboAITheme(
    themeMode: AppThemeMode = AppThemeMode.DARK, // Default to deep dark blue per prompt!
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        AppThemeMode.SYSTEM -> isSystemInDarkTheme()
        AppThemeMode.DARK -> true
        AppThemeMode.LIGHT -> false
    }

    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.background.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
