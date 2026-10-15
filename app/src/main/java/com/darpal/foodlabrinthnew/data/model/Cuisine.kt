package com.darpal.foodlabrinthnew.data.model

/**
 * A cuisine the user can browse or like. [terms] are matched (case-insensitively, as substrings)
 * against a restaurant's Yelp categories, e.g. "BBQ" also matches the Yelp category "Barbeque".
 */
data class Cuisine(val name: String, val terms: List<String> = listOf(name)) {
    fun matches(restaurant: Restaurant): Boolean =
        restaurant.categories.any { category -> terms.any { category.contains(it, ignoreCase = true) } }

    companion object {
        val ALL: List<Cuisine> = listOf(
            Cuisine("American", listOf("American")),
            Cuisine("Asian", listOf("Asian")),
            Cuisine("BBQ", listOf("BBQ", "Barbeque", "Barbecue")),
            Cuisine("Chinese"),
            Cuisine("Coffee & Tea", listOf("Coffee", "Tea")),
            Cuisine("Deli", listOf("Deli")),
            Cuisine("Desserts", listOf("Dessert", "Ice Cream", "Bakeries")),
            Cuisine("European", listOf("European", "French", "German", "British")),
            Cuisine("Fast Food"),
            Cuisine("Greek"),
            Cuisine("Halal"),
            Cuisine("Indian"),
            Cuisine("Italian", listOf("Italian", "Pizza")),
            Cuisine("Jamaican", listOf("Jamaican", "Caribbean")),
            Cuisine("Japanese", listOf("Japanese", "Ramen")),
            Cuisine("Korean"),
            Cuisine("Mediterranean"),
            Cuisine("Mexican", listOf("Mexican", "Tex-Mex", "Tacos")),
            Cuisine("Salad"),
            Cuisine("Spanish", listOf("Spanish", "Tapas")),
            Cuisine("Sushi"),
            Cuisine("Thai"),
            Cuisine("Vegan"),
            Cuisine("Vegetarian"),
        )

        fun byName(name: String): Cuisine? = ALL.firstOrNull { it.name.equals(name, ignoreCase = true) }

        fun matching(restaurant: Restaurant): List<Cuisine> = ALL.filter { it.matches(restaurant) }
    }
}
