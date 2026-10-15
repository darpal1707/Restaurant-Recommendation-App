package com.darpal.foodlabrinthnew.data.model

/** Where to look for restaurants: the device location ([point] == null) or a preset city. */
data class Area(val id: String, val label: String, val point: GeoPoint?) {
    val isNearby: Boolean get() = point == null

    companion object {
        val NEARBY = Area("nearby", "Near me", null)

        val CITIES: List<Area> = listOf(
            Area("toronto", "Toronto", GeoPoint(43.6532, -79.3832)),
            Area("new_york", "New York", GeoPoint(40.7128, -74.0060)),
            Area("san_francisco", "San Francisco", GeoPoint(37.7749, -122.4194)),
            Area("london", "London", GeoPoint(51.5074, -0.1278)),
            Area("paris", "Paris", GeoPoint(48.8566, 2.3522)),
            Area("mumbai", "Mumbai", GeoPoint(19.0760, 72.8777)),
            Area("sydney", "Sydney", GeoPoint(-33.8688, 151.2093)),
        )

        fun byId(id: String?): Area = CITIES.firstOrNull { it.id == id } ?: NEARBY
    }
}
