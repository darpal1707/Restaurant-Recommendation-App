package com.darpal.foodlabrinthnew.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import com.darpal.foodlabrinthnew.R

val Montserrat = FontFamily(
    Font(R.font.montserrat_regular, FontWeight.Normal),
    Font(R.font.montserrat_medium, FontWeight.Medium),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold),
    Font(R.font.montserrat_bold, FontWeight.Bold),
)

private val base = Typography()

/**
 * Tracking and leading change with size rather than using one value everywhere: large text is
 * tightened (Montserrat is wide, and letters drift apart as they grow) with snug leading, body text
 * keeps neutral tracking with comfortable leading, and small labels get a little extra space so
 * they stay legible. Hierarchy comes from weight as well as size.
 */
private fun TextStyle.tuned(weight: FontWeight, tracking: Double, leading: Double) = copy(
    fontFamily = Montserrat,
    fontWeight = weight,
    letterSpacing = tracking.em,
    lineHeight = leading.em,
)

val AppTypography = Typography(
    displayLarge = base.displayLarge.tuned(FontWeight.Bold, tracking = -0.03, leading = 1.1),
    displayMedium = base.displayMedium.tuned(FontWeight.Bold, tracking = -0.03, leading = 1.1),
    displaySmall = base.displaySmall.tuned(FontWeight.Bold, tracking = -0.025, leading = 1.15),
    headlineLarge = base.headlineLarge.tuned(FontWeight.Bold, tracking = -0.02, leading = 1.2),
    headlineMedium = base.headlineMedium.tuned(FontWeight.Bold, tracking = -0.02, leading = 1.2),
    headlineSmall = base.headlineSmall.tuned(FontWeight.SemiBold, tracking = -0.015, leading = 1.25),
    titleLarge = base.titleLarge.tuned(FontWeight.SemiBold, tracking = -0.01, leading = 1.25),
    titleMedium = base.titleMedium.tuned(FontWeight.SemiBold, tracking = -0.005, leading = 1.3),
    titleSmall = base.titleSmall.tuned(FontWeight.SemiBold, tracking = 0.0, leading = 1.35),
    bodyLarge = base.bodyLarge.tuned(FontWeight.Normal, tracking = 0.0, leading = 1.5),
    bodyMedium = base.bodyMedium.tuned(FontWeight.Normal, tracking = 0.005, leading = 1.45),
    bodySmall = base.bodySmall.tuned(FontWeight.Normal, tracking = 0.01, leading = 1.4),
    labelLarge = base.labelLarge.tuned(FontWeight.SemiBold, tracking = 0.005, leading = 1.3),
    labelMedium = base.labelMedium.tuned(FontWeight.Medium, tracking = 0.015, leading = 1.3),
    labelSmall = base.labelSmall.tuned(FontWeight.Medium, tracking = 0.025, leading = 1.3),
)
