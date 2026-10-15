package com.darpal.foodlabrinthnew.data.model

/** Pure filtering / ranking rules shared by the screens. */
object RestaurantFilters {

    fun matchesQuery(restaurant: Restaurant, query: String): Boolean {
        val terms = query.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        if (terms.isEmpty()) return true
        val haystack = buildString {
            append(restaurant.name).append(' ')
            append(restaurant.city).append(' ')
            append(restaurant.state).append(' ')
            restaurant.categories.joinTo(this, " ")
        }
        return terms.all { haystack.contains(it, ignoreCase = true) }
    }

    fun matchesAnyCuisine(restaurant: Restaurant, cuisines: Collection<Cuisine>): Boolean =
        cuisines.isEmpty() || cuisines.any { it.matches(restaurant) }

    /** Restaurants without price data are kept: excluding them would hide most of the dataset. */
    fun matchesBudget(restaurant: Restaurant, budget: Budget?): Boolean =
        budget == null || restaurant.priceRange == null || restaurant.priceRange in budget.priceRanges

    fun byRating(): Comparator<Restaurant> =
        compareByDescending<Restaurant> { it.stars ?: 0.0 }.thenByDescending { it.reviewCount }

    fun trending(all: List<Restaurant>, limit: Int = 10): List<Restaurant> =
        all.sortedWith(compareByDescending<Restaurant> { it.reviewCount }.thenByDescending { it.stars ?: 0.0 })
            .take(limit)

    fun topRated(all: List<Restaurant>, limit: Int = 10, minReviews: Int = 5): List<Restaurant> =
        all.filter { it.reviewCount >= minReviews }.sortedWith(byRating()).take(limit)

    fun recommended(all: List<Restaurant>, likedCuisineNames: Set<String>, exclude: Set<String> = emptySet()): List<Restaurant> {
        val liked = likedCuisineNames.mapNotNull { Cuisine.byName(it) }
        if (liked.isEmpty()) return emptyList()
        return all.filter { it.id !in exclude && matchesAnyCuisine(it, liked) }.sortedWith(byRating())
    }

    /**
     * Questionnaire results: cuisine is a hard filter, while meal, vibe and budget only rank results,
     * because many records lack those attributes and filtering on them would usually return nothing.
     */
    fun decide(
        all: List<Restaurant>,
        cuisines: Collection<Cuisine>,
        meal: Meal?,
        ambience: Ambience?,
        budget: Budget?,
    ): List<Restaurant> = all
        .filter { matchesAnyCuisine(it, cuisines) }
        .sortedWith(
            compareByDescending<Restaurant> { matchScore(it, meal, ambience, budget) }.then(byRating()),
        )

    fun matchScore(restaurant: Restaurant, meal: Meal?, ambience: Ambience?, budget: Budget?): Int {
        var score = 0
        if (meal != null && restaurant.goodForMeal.any { it in meal.keys }) score++
        if (ambience != null && restaurant.ambience.any { it in ambience.keys }) score++
        if (budget != null && restaurant.priceRange in budget.priceRanges) score++
        return score
    }
}
