package dev.haihuynh.scribbledash.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.haihuynh.scribbledash.R

val BagelFatOne = FontFamily(
    Font(R.font.bagel_fat_one_regular, FontWeight.Normal)
)

val OutFit = FontFamily(
    Font(
        resId = R.font.outfit_regular,
        weight = FontWeight.Normal
    ),
    Font(
        resId = R.font.outfit_medium,
        weight = FontWeight.Medium
    )
)

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = BagelFatOne,
        fontSize = 40.sp,
        color = Color(0xFF514437)
    ),
    headlineMedium = TextStyle(
        fontFamily = BagelFatOne,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        color = Color(0xFF514437),
    ),
    headlineSmall = TextStyle(
        fontFamily = BagelFatOne,
        fontSize = 18.sp,
        lineHeight = 26.sp,
        color = Color(0xFF514437),
    ),
    titleLarge = TextStyle(
        fontFamily = OutFit,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color(0xFF514437)
    ),
    titleMedium = TextStyle(
        fontFamily = OutFit,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        color = Color(0xFF514437)
    )
    /* Other default text styles to override
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
    */
)