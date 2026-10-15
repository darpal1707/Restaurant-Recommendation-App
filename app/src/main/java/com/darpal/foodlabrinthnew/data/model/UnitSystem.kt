package com.darpal.foodlabrinthnew.data.model

import java.util.Locale

/** How distances are shown. Chosen from the device region; see [forCountry]. */
enum class UnitSystem {
    METRIC,
    IMPERIAL,
    ;

    companion object {
        /** Regions that use miles for everyday distances (United States, United Kingdom, Liberia, Myanmar). */
        private val IMPERIAL_COUNTRIES = setOf("US", "GB", "LR", "MM")

        fun forCountry(countryCode: String): UnitSystem =
            if (countryCode.uppercase(Locale.ROOT) in IMPERIAL_COUNTRIES) IMPERIAL else METRIC

        fun forDefaultLocale(): UnitSystem = forCountry(Locale.getDefault().country)
    }
}

const val METERS_PER_MILE = 1609.344
private const val FEET_PER_METER = 3.28084

/** "350 m" / "1.2 km", or "300 ft" / "0.8 mi" in [UnitSystem.IMPERIAL]. */
fun formatDistance(meters: Int, units: UnitSystem = UnitSystem.METRIC): String = when (units) {
    UnitSystem.METRIC -> when {
        meters < 1000 -> "${(meters / 10) * 10} m"
        meters < 10_000 -> String.format(Locale.US, "%.1f km", meters / 1000.0)
        else -> "${meters / 1000} km"
    }
    UnitSystem.IMPERIAL -> {
        val miles = meters / METERS_PER_MILE
        when {
            // Under 0.1 mi, feet read better than "0.0 mi".
            miles < 0.1 -> "${((meters * FEET_PER_METER) / 50).toInt() * 50} ft"
            miles < 10 -> String.format(Locale.US, "%.1f mi", miles)
            else -> "${miles.toInt()} mi"
        }
    }
}
