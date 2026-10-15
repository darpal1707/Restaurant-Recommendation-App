package com.darpal.foodlabrinthnew.data.model

/** Maps an OpenStreetMap element (type, id, coordinates, tags) to a [Restaurant]. */
object OsmParser {

    /**
     * @param type "node", "way" or "relation"
     * @param latitude node latitude, or the `center` latitude Overpass returns for ways/relations
     * @return null when the element has no name, no usable coordinates or isn't a place to eat
     */
    fun toRestaurant(type: String, id: Long, latitude: Double?, longitude: Double?, tags: Map<String, String>): Restaurant? {
        val name = tags["name"]?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val kind = PlaceKind.fromOsm(tags["amenity"]) ?: return null
        if (latitude == null || longitude == null || latitude !in -90.0..90.0 || longitude !in -180.0..180.0) return null
        val prefix = type.firstOrNull()?.takeIf { it in "nwr" } ?: return null

        val street = listOfNotNull(tags["addr:housenumber"], tags["addr:street"]).joinToString(" ")
        return Restaurant(
            id = "$prefix$id",
            name = name,
            latitude = latitude,
            longitude = longitude,
            kind = kind,
            categories = categories(tags, kind),
            address = street.ifBlank { tags["addr:full"].orEmpty() },
            city = tags["addr:city"].orEmpty(),
            state = (tags["addr:state"] ?: tags["addr:province"]).orEmpty(),
            postalCode = tags["addr:postcode"].orEmpty(),
            openingHours = tags["opening_hours"]?.trim()?.takeIf { it.isNotEmpty() },
            phone = (tags["phone"] ?: tags["contact:phone"])?.split(';')?.first()?.trim()?.takeIf { it.isNotEmpty() },
            website = (tags["website"] ?: tags["contact:website"])?.trim()?.takeIf { it.isNotEmpty() }?.let {
                if (it.startsWith("http://") || it.startsWith("https://")) it else "https://$it"
            },
            features = features(tags),
        )
    }

    internal fun categories(tags: Map<String, String>, kind: PlaceKind): List<String> {
        val cuisines = tags["cuisine"].orEmpty()
            .split(';', ',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .map(::prettify)
        val diets = listOf("vegan" to "Vegan", "vegetarian" to "Vegetarian", "halal" to "Halal")
            .filter { (key, _) -> tags["diet:$key"] in setOf("yes", "only") }
            .map { it.second }
        return (cuisines + diets + kind.label).distinctBy { it.lowercase() }
    }

    internal fun features(tags: Map<String, String>): List<String> = buildList {
        if (tags["outdoor_seating"] == "yes") add("Outdoor seating")
        if (tags["takeaway"] in setOf("yes", "only")) add("Takeaway")
        if (tags["delivery"] == "yes") add("Delivery")
        if (tags["wheelchair"] == "yes") add("Wheelchair accessible")
        if (tags["internet_access"] in setOf("wlan", "yes")) add("Wi-Fi")
    }

    /** "coffee_shop" -> "Coffee Shop", "tex-mex" -> "Tex-Mex". */
    internal fun prettify(value: String): String =
        value.replace('_', ' ')
            .split(' ')
            .joinToString(" ") { word ->
                word.split('-').joinToString("-") { part -> part.replaceFirstChar { it.uppercaseChar() } }
            }
}
