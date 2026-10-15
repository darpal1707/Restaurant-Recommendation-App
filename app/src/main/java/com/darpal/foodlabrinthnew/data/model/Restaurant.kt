package com.darpal.foodlabrinthnew.data.model

/**
 * A restaurant from the `business` node of the Realtime Database (Yelp-style records).
 *
 * The database stores loosely typed values (numbers may arrive as Long, Double or String),
 * so [fromMap] parses defensively instead of relying on Firebase's reflection-based mapping.
 */
data class Restaurant(
    val id: String,
    val name: String,
    val address: String = "",
    val city: String = "",
    val state: String = "",
    val postalCode: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    val stars: Double? = null,
    val reviewCount: Int = 0,
    val categories: List<String> = emptyList(),
    val hours: List<OpeningHours> = emptyList(),
    val isOpen: Boolean? = null,
    /** Yelp `RestaurantsPriceRange2`, 1 (cheap) to 4 (expensive). */
    val priceRange: Int? = null,
    /** Keys of Yelp `GoodForMeal` that are true, e.g. "lunch", "dinner". */
    val goodForMeal: Set<String> = emptySet(),
    /** Keys of Yelp `Ambience` that are true, e.g. "romantic", "casual". */
    val ambience: Set<String> = emptySet(),
) {
    val hasLocation: Boolean get() = latitude != null && longitude != null

    val cityState: String get() = listOf(city, state).filter { it.isNotBlank() }.joinToString(", ")

    val fullAddress: String
        get() = listOf(address, city, listOf(state, postalCode).filter { it.isNotBlank() }.joinToString(" "))
            .filter { it.isNotBlank() }
            .joinToString(", ")

    val priceLabel: String? get() = priceRange?.takeIf { it in 1..4 }?.let { "$".repeat(it) }

    companion object {
        private val WEEK = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        private val TRUE_KEY = Regex("""['"]?(\w+)['"]?\s*[:=]\s*True""", RegexOption.IGNORE_CASE)

        fun fromMap(key: String?, map: Map<*, *>): Restaurant? {
            val name = map.string("name") ?: return null
            val id = map.string("business_id") ?: key ?: return null
            val attributes = map["attributes"] as? Map<*, *>
            return Restaurant(
                id = id,
                name = name,
                address = map.string("address").orEmpty(),
                city = map.string("city").orEmpty(),
                state = map.string("state").orEmpty(),
                postalCode = map.string("postal_code").orEmpty(),
                latitude = map.double("latitude")?.takeIf { it in -90.0..90.0 },
                longitude = map.double("longitude")?.takeIf { it in -180.0..180.0 },
                stars = map.double("stars"),
                reviewCount = map.double("review_count")?.toInt() ?: 0,
                categories = parseCategories(map["categories"]),
                hours = parseHours(map["hours"]),
                isOpen = map.string("is_open")?.let { it == "1" || it.equals("true", ignoreCase = true) },
                priceRange = attributes?.string("RestaurantsPriceRange2")?.toDoubleOrNull()?.toInt(),
                goodForMeal = parseTrueKeys(attributes?.get("GoodForMeal")),
                ambience = parseTrueKeys(attributes?.get("Ambience")),
            )
        }

        internal fun parseCategories(value: Any?): List<String> = when (value) {
            is String -> value.split(',')
            is List<*> -> value.map { it.toString() }
            is Map<*, *> -> value.values.map { it.toString() }
            else -> emptyList()
        }.map { it.trim() }.filter { it.isNotEmpty() && it != "null" }

        internal fun parseHours(value: Any?): List<OpeningHours> {
            val pairs: List<Pair<String, String>> = when (value) {
                is Map<*, *> -> value.entries.map { it.key.toString() to it.value.toString() }
                // Older imports stored the map's toString(), e.g. "{Monday=9:0-17:0, Tuesday=...}".
                is String -> value.trim().removePrefix("{").removeSuffix("}")
                    .split(',')
                    .mapNotNull { part ->
                        val idx = part.indexOfFirst { it == '=' || it == ':' }
                        if (idx <= 0) null else part.substring(0, idx).trim().trim('\'', '"') to
                            part.substring(idx + 1).trim().trim('\'', '"')
                    }
                else -> emptyList()
            }
            return pairs
                .filter { (day, time) -> day.isNotBlank() && time.isNotBlank() && time != "null" }
                .map { (day, time) -> OpeningHours(day, formatTimeRange(time)) }
                .sortedBy { WEEK.indexOf(it.day).let { i -> if (i < 0) Int.MAX_VALUE else i } }
        }

        /** Yelp writes "9:0-17:30"; show it as "09:00 – 17:30". Anything else is shown as is. */
        internal fun formatTimeRange(raw: String): String {
            val parts = raw.split('-')
            if (parts.size != 2) return raw
            val formatted = parts.map { part ->
                val hm = part.trim().split(':')
                val h = hm.getOrNull(0)?.toIntOrNull() ?: return raw
                val m = hm.getOrNull(1)?.toIntOrNull() ?: 0
                "%02d:%02d".format(h, m)
            }
            return if (formatted[0] == "00:00" && formatted[1] == "00:00") "Open 24 hours"
            else "${formatted[0]} – ${formatted[1]}"
        }

        /** Accepts either a nested map or a Python-dict string such as "{'lunch': True, 'dinner': False}". */
        internal fun parseTrueKeys(value: Any?): Set<String> = when (value) {
            is Map<*, *> -> value.filterValues { it == true || it.toString().equals("true", ignoreCase = true) }
                .keys.map { it.toString().lowercase() }.toSet()
            is String -> TRUE_KEY.findAll(value).map { it.groupValues[1].lowercase() }.toSet()
            else -> emptySet()
        }

        private fun Map<*, *>.string(key: String): String? =
            this[key]?.toString()?.trim()?.takeIf { it.isNotEmpty() && it != "null" }

        private fun Map<*, *>.double(key: String): Double? = string(key)?.toDoubleOrNull()
    }
}

data class OpeningHours(val day: String, val time: String)
