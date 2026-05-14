package com.nammavastra.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val AppColors = lightColorScheme(
    background = AppBackground,
    surface = AppSurface,
    surfaceVariant = AppSurfaceAlt,
    primary = ZariGold,
    secondary = SilkMaroon,
    onBackground = DeepCharcoal,
    onSurface = DeepCharcoal,
    onSurfaceVariant = MutedText,
    onPrimary = PaperWhite,
    onSecondary = PaperWhite,
    outline = SoftBorder,
    primaryContainer = AppSurfaceAlt,
    onPrimaryContainer = DeepCharcoal
)

@Composable
fun NammaVastraTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = AppColors,
        typography = NammaTypography,
        content = content
    )
}
