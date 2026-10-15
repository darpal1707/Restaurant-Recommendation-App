package com.darpal.foodlabrinthnew.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.darpal.foodlabrinthnew.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
)

private val base = Typography()

val AppTypography = Typography(
    displayLarge = base.displayLarge.copy(fontFamily = Montserrat, fontWeight = FontWeight.Bold),
    displayMedium = base.displayMedium.copy(fontFamily = Montserrat, fontWeight = FontWeight.Bold),
    displaySmall = base.displaySmall.copy(fontFamily = Montserrat, fontWeight = FontWeight.Bold),
    headlineLarge = base.headlineLarge.copy(fontFamily = Montserrat, fontWeight = FontWeight.Bold),
    headlineMedium = base.headlineMedium.copy(fontFamily = Montserrat, fontWeight = FontWeight.Bold),
    headlineSmall = base.headlineSmall.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    titleLarge = base.titleLarge.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    titleMedium = base.titleMedium.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    titleSmall = base.titleSmall.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    bodyLarge = base.bodyLarge.copy(fontFamily = Montserrat),
    bodyMedium = base.bodyMedium.copy(fontFamily = Montserrat),
    bodySmall = base.bodySmall.copy(fontFamily = Montserrat),
    labelLarge = base.labelLarge.copy(fontFamily = Montserrat, fontWeight = FontWeight.SemiBold),
    labelMedium = base.labelMedium.copy(fontFamily = Montserrat, fontWeight = FontWeight.Medium),
    labelSmall = base.labelSmall.copy(fontFamily = Montserrat, fontWeight = FontWeight.Medium),
)
