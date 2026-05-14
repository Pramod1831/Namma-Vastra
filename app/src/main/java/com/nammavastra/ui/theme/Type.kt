package com.nammavastra.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val PlayfairFamily = FontFamily.Serif
val LatoFamily = FontFamily.SansSerif
val RobotoMonoFamily = FontFamily.Monospace

val NammaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = PlayfairFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 40.sp,
        lineHeight = 46.sp
    ),
    displayMedium = TextStyle(
        fontFamily = PlayfairFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 31.sp,
        lineHeight = 38.sp
    ),
    titleLarge = TextStyle(
        fontFamily = PlayfairFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 25.sp,
        lineHeight = 30.sp
    ),
    titleMedium = TextStyle(
        fontFamily = LatoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 19.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = LatoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 17.sp,
        lineHeight = 26.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = LatoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 23.sp
    ),
    labelLarge = TextStyle(
        fontFamily = LatoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        letterSpacing = 0.4.sp
    ),
    labelMedium = TextStyle(
        fontFamily = RobotoMonoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        letterSpacing = 0.8.sp
    )
)
