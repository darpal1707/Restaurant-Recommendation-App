package com.darpal.foodlabrinthnew.data.model

/** Pure filtering / ranking rules shared by the screens. */
object RestaurantFilters {

    fun matchesQuery(restaurant: Restaurant, query: String): Boolean {
        val terms = query.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (terms.isEmpty()) return true
        val haystack = buildString {
            append(restaurant.name).append(' ')
            append(restaurant.address).append(' ')
            append(restaurant.city).append(' ')
            append(restaurant.state).append(' ')
            restaurant.categories.joinTo(this, " ")
        }
        return terms.all { haystack.contains(it, ignoreCase = true) }
    }

    fun matchesAnyCuisine(restaurant: Restaurant, cuisines: Collection<Cuisine>): Boolean =
        cuisines.isEmpty() || cuisines.any { it.matches(restaurant) }

    /** Places without a known distance are kept. */
    fun withinDistance(restaurant: Restaurant, distance: Distance?): Boolean =
        distance == null || (restaurant.distanceMeters ?: 0) <= distance.maxMeters

    fun byDistance(): Comparator<Restaurant> =
        compareBy<Restaurant, Int?>(nullsLast()) { it.distanceMeters }.thenBy { it.name }

    fun nearest(all: List<Restaurant>, limit: Int = 10): List<Restaurant> = all.sortedWith(byDistance()).take(limit)

    fun recommended(all: List<Restaurant>, likedCuisineNames: Set<String>): List<Restaurant> {
        val liked = likedCuisineNames.mapNotNull { Cuisine.byName(it) }
        if (liked.isEmpty()) return emptyList()
        return all.filter { matchesAnyCuisine(it, liked) }.sortedWith(byDistance())
    }

    /**
     * Questionnaire results: cuisine and distance filter; the meal only ranks results, because many
     * OSM entries have no `opening_hours` and filtering on it would hide them.
     */
    fun decide(all: List<Restaurant>, cuisines: Collection<Cuisine>, meal: Meal?, distance: Distance?): List<Restaurant> =
        all.filter { matchesAnyCuisine(it, cuisines) && withinDistance(it, distance) }
            .sortedWith(compareByDescending<Restaurant> { mealScore(it, meal) }.then(byDistance()))

    fun mealScore(restaurant: Restaurant, meal: Meal?): Int = when {
        meal == null -> 0
        meal == Meal.CAFE -> if (restaurant.kind == PlaceKind.CAFE || restaurant.categories.any { it.contains("Coffee", true) }) 1 else 0
        OpeningHours.isOpenAt(restaurant.openingHours, meal.minuteOfDay!!) == true -> 1
        else -> 0
    }
}
