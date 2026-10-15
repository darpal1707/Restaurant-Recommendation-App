package com.darpal.foodlabrinthnew.data.model

/**
 * A cuisine the user can browse or like. [terms] are matched (case-insensitively, as substrings)
 * against a restaurant's categories, which come from the OSM `cuisine` and `diet:*` tags.
 */
data class Cuisine(val name: String, val terms: List<String> = listOf(name)) {
    fun matches(restaurant: Restaurant): Boolean =
        restaurant.categories.any { category -> terms.any { category.contains(it, ignoreCase = true) } }

    companion object {
        val ALL: List<Cuisine> = listOf(
            Cuisine("American", listOf("American", "Burger", "Steak")),
            Cuisine("Asian", listOf("Asian")),
            Cuisine("BBQ", listOf("BBQ", "Barbecue", "Barbeque")),
            Cuisine("Chinese"),
            Cuisine("Coffee & Tea", listOf("Coffee", "Tea", "Cafe")),
            Cuisine("Deli", listOf("Deli", "Sandwich", "Bagel")),
            Cuisine("Desserts", listOf("Dessert", "Ice Cream", "Cake", "Donut", "Bakery")),
            Cuisine("European", listOf("European", "French", "German", "British", "Portuguese")),
            Cuisine("Fast Food", listOf("Fast Food", "Burger", "Chicken")),
            Cuisine("Greek"),
            Cuisine("Halal"),
            Cuisine("Indian"),
            Cuisine("Italian", listOf("Italian", "Pizza", "Pasta")),
            Cuisine("Jamaican", listOf("Jamaican", "Caribbean")),
            Cuisine("Japanese", listOf("Japanese", "Ramen")),
            Cuisine("Korean"),
            Cuisine("Mediterranean", listOf("Mediterranean", "Lebanese", "Turkish", "Kebab")),
            Cuisine("Mexican", listOf("Mexican", "Tex-Mex", "Taco", "Burrito")),
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
