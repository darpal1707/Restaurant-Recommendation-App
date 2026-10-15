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

enum class Distance(val label: String, val description: String, val maxMeters: Int) {
    WALK("Walking distance", "Within 1 km", 1_000),
    SHORT_TRIP("Short trip", "Within 3 km", 3_000),
    FURTHER("A bit further", "Within 10 km", 10_000),
}
