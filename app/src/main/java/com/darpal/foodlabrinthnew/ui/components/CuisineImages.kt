package com.darpal.foodlabrinthnew.ui.components

import androidx.annotation.DrawableRes
import com.darpal.foodlabrinthnew.R
import com.darpal.foodlabrinthnew.data.model.Cuisine
import com.darpal.foodlabrinthnew.data.model.Restaurant

/** Bundled artwork for cuisines; restaurants without photos use the image of their cuisine. */
object CuisineImages {
    private val byName: Map<String, Int> = mapOf(
        "American" to R.drawable.american,
        "Asian" to R.drawable.asian,
        "BBQ" to R.drawable.bbq,
        "Chinese" to R.drawable.chinese,
        "Coffee & Tea" to R.drawable.coffee_tea,
        "Deli" to R.drawable.deli,
        "Desserts" to R.drawable.desserts_two,
        "European" to R.drawable.european,
        "Fast Food" to R.drawable.fast_food,
        "Greek" to R.drawable.greek,
        "Halal" to R.drawable.halal,
        "Indian" to R.drawable.indian,
        "Italian" to R.drawable.italian,
        "Jamaican" to R.drawable.jamaican,
        "Japanese" to R.drawable.japanese,
        "Korean" to R.drawable.korean,
        "Mediterranean" to R.drawable.mediterranean,
        "Mexican" to R.drawable.mexican,
        "Salad" to R.drawable.salads,
        "Spanish" to R.drawable.spanish,
        "Sushi" to R.drawable.sushi,
        "Thai" to R.drawable.thai,
        "Vegan" to R.drawable.vegan,
        "Vegetarian" to R.drawable.vegetarian,
    )
    private val fallbacks = byName.values.toList()

    @DrawableRes
    fun forCuisine(cuisine: Cuisine): Int = byName[cuisine.name] ?: R.drawable.american

    @DrawableRes
    fun forRestaurant(restaurant: Restaurant): Int =
        Cuisine.matching(restaurant).firstOrNull()?.let(::forCuisine)
            ?: fallbacks[Math.floorMod(restaurant.id.hashCode(), fallbacks.size)]
}
