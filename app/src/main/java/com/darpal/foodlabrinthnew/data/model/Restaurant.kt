package com.darpal.foodlabrinthnew.data.model

/** A place to eat, built from an OpenStreetMap element (see [OsmParser]). */
data class Restaurant(
    /** OSM type initial + id, e.g. "n123456" for node 123456. */
    val id: String,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val kind: PlaceKind = PlaceKind.RESTAURANT,
    /** Cuisines and diets, e.g. ["Italian", "Pizza", "Vegan"]. */
    val categories: List<String> = emptyList(),
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    /** Raw OSM `opening_hours` value, e.g. "Mo-Fr 11:00-22:00; Sa 12:00-23:00". */
    val openingHours: String? = null,
    val phone: String? = null,
    val website: String? = null,
    /** Amenities such as "Outdoor seating" or "Takeaway". */
    val features: List<String> = emptyList(),
    /** Distance from the current search centre; filled in by the repository. */
    val distanceMeters: Int? = null,
) {
    val location: GeoPoint get() = GeoPoint(latitude, longitude)

    val cityState: String get() = listOf(city, state).filter { it.isNotBlank() }.joinToString(", ")

    val fullAddress: String
        get() = listOf(address, city, listOf(state, postalCode).filter { it.isNotBlank() }.joinToString(" "))
            .filter { it.isNotBlank() }
            .joinToString(", ")

    val hours: List<OpeningHours> get() = OpeningHours.parse(openingHours)

    /** Cuisines only, without the generic kind label, for compact subtitles. */
    val cuisineLabel: String
        get() = categories.filterNot { it.equals(kind.label, ignoreCase = true) }.take(3).joinToString(" · ")
            .ifBlank { kind.label }

    val distanceLabel: String? get() = distanceMeters?.let(::formatDistance)

    /** Link to this element on openstreetmap.org. */
    val osmUrl: String?
        get() {
            val type = when (id.firstOrNull()) {
                'n' -> "node"
                'w' -> "way"
                'r' -> "relation"
                else -> return null
            }
            return "https://www.openstreetmap.org/$type/${id.drop(1)}"
        }

    fun withDistanceFrom(center: GeoPoint): Restaurant =
        copy(distanceMeters = GeoPoint.distanceMeters(center, location))
}

enum class PlaceKind(val osmValue: String, val label: String) {
    RESTAURANT("restaurant", "Restaurant"),
    CAFE("cafe", "Cafe"),
    FAST_FOOD("fast_food", "Fast food"),
    ;

    companion object {
        fun fromOsm(value: String?): PlaceKind? = entries.firstOrNull { it.osmValue == value }
    }
}

data class GeoPoint(val latitude: Double, val longitude: Double) {
    companion object {
        private const val EARTH_RADIUS_M = 6_371_000.0

        /** Great-circle (haversine) distance in metres. */
        fun distanceMeters(a: GeoPoint, b: GeoPoint): Int {
            val dLat = Math.toRadians(b.latitude - a.latitude)
            val dLon = Math.toRadians(b.longitude - a.longitude)
            val h = Math.sin(dLat / 2).let { it * it } +
                Math.cos(Math.toRadians(a.latitude)) * Math.cos(Math.toRadians(b.latitude)) *
                Math.sin(dLon / 2).let { it * it }
            return (2 * EARTH_RADIUS_M * Math.asin(Math.sqrt(h))).toInt()
        }
    }
}

fun formatDistance(meters: Int): String = when {
    meters < 1000 -> "${(meters / 10) * 10} m"
    meters < 10_000 -> String.format(java.util.Locale.US, "%.1f km", meters / 1000.0)
    else -> "${meters / 1000} km"
}
