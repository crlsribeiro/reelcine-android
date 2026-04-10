package com.carlosribeiro.reelcine.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val ReelCineDarkColorScheme = darkColorScheme(
    primary = Violet,
    onPrimary = TextPrimary,
    primaryContainer = VioletDark,
    onPrimaryContainer = TextPrimary,
    secondary = Gold,
    onSecondary = BackgroundDark,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    error = Error,
    onError = TextPrimary,
    outline = DividerColor
)

@Composable
fun ReelCineTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ReelCineDarkColorScheme,
        typography = ReelCineTypography,
        content = content
    )
}
