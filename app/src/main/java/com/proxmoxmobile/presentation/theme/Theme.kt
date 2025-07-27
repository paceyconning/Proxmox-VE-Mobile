package com.proxmoxmobile.presentation.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// OLED Dark Theme Colors
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF64B5F6), // Light Blue
    onPrimary = Color(0xFF000000),
    primaryContainer = Color(0xFF1976D2),
    onPrimaryContainer = Color(0xFFFFFFFF),
    secondary = Color(0xFF81C784), // Light Green
    onSecondary = Color(0xFF000000),
    secondaryContainer = Color(0xFF388E3C),
    onSecondaryContainer = Color(0xFFFFFFFF),
    tertiary = Color(0xFFFFB74D), // Light Orange
    onTertiary = Color(0xFF000000),
    tertiaryContainer = Color(0xFFF57C00),
    onTertiaryContainer = Color(0xFFFFFFFF),
    error = Color(0xFFFF8A80), // Light Red
    onError = Color(0xFF000000),
    errorContainer = Color(0xFFD32F2F),
    onErrorContainer = Color(0xFFFFFFFF),
    background = Color(0xFF000000), // Pure Black for OLED
    onBackground = Color(0xFFE0E0E0),
    surface = Color(0xFF121212), // Dark Gray
    onSurface = Color(0xFFE0E0E0),
    surfaceVariant = Color(0xFF1E1E1E), // Slightly lighter dark gray
    onSurfaceVariant = Color(0xFFBDBDBD),
    outline = Color(0xFF424242),
    outlineVariant = Color(0xFF616161),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFFE0E0E0),
    inverseOnSurface = Color(0xFF121212),
    inversePrimary = Color(0xFF1976D2)
)

// Light Theme Colors (for completeness)
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF1976D2),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFBBDEFB),
    onPrimaryContainer = Color(0xFF000000),
    secondary = Color(0xFF388E3C),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFC8E6C9),
    onSecondaryContainer = Color(0xFF000000),
    tertiary = Color(0xFFF57C00),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFE0B2),
    onTertiaryContainer = Color(0xFF000000),
    error = Color(0xFFD32F2F),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFCDD2),
    onErrorContainer = Color(0xFF000000),
    background = Color(0xFFFAFAFA),
    onBackground = Color(0xFF000000),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF000000),
    surfaceVariant = Color(0xFFF5F5F5),
    onSurfaceVariant = Color(0xFF424242),
    outline = Color(0xFFBDBDBD),
    outlineVariant = Color(0xFFE0E0E0),
    scrim = Color(0xFF000000),
    inverseSurface = Color(0xFF121212),
    inverseOnSurface = Color(0xFFE0E0E0),
    inversePrimary = Color(0xFF64B5F6)
)

@Composable
fun ProxmoxTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            // Make the system bars transparent
            WindowCompat.setDecorFitsSystemWindows(window, false)
            // Set the status bar color to transparent
            window.statusBarColor = Color.Transparent.toArgb()
            // Set the navigation bar color to transparent
            window.navigationBarColor = Color.Transparent.toArgb()
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
} 