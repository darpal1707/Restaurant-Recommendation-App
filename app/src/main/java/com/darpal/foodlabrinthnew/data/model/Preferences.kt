package com.darpal.foodlabrinthnew.data.model

/**
 * Meal answer for the "Help me choose" questionnaire. Ranked by whether OSM `opening_hours`
 * covers [minuteOfDay]; [CAFE] instead prefers cafés and coffee places.
 */
enum class Meal(val label: String, val minuteOfDay: Int?) {
    BREAKFAST("Breakfast", 8 * 60 + 30),
    LUNCH("Lunch", 12 * 60 + 30),
    DINNER("Dinner", 19 * 60 + 30),
    CAFE("Cafe", null),
    LATE_NIGHT("Late night", 23 * 60 + 30),
}

/**
 * Distance answer for the questionnaire. Each option uses round numbers in the user's units
 * (e.g. 0.5 mi rather than 0.8 km) and all of them fit inside [SEARCH_RADIUS_METERS].
 */
enum class Distance(val label: String, private val metricMeters: Int, private val imperialMiles: Double) {
    AROUND_THE_CORNER("Around the corner", 500, 0.25),
    WALK("Walking distance", 1_000, 0.5),
    SHORT_TRIP("Short trip", 2_000, 1.0),
    ;

    fun maxMeters(units: UnitSystem): Int = when (units) {
        UnitSystem.METRIC -> metricMeters
        UnitSystem.IMPERIAL -> (imperialMiles * METERS_PER_MILE).toInt()
    }

    fun description(units: UnitSystem): String = when (units) {
        UnitSystem.METRIC -> if (metricMeters < 1000) "Within $metricMeters m" else "Within ${metricMeters / 1000} km"
        UnitSystem.IMPERIAL -> "Within ${imperialMiles.toString().removeSuffix(".0")} mi"
    }
}

/** How far around the chosen point restaurants are fetched from OpenStreetMap. */
const val SEARCH_RADIUS_METERS = 3_000
