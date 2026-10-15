package com.darpal.foodlabrinthnew.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantFiltersTest {

    private val bbq = Restaurant(
        id = "1", name = "Smoke House", city = "Austin", state = "TX",
        categories = listOf("Barbeque", "Restaurants"), stars = 4.5, reviewCount = 300, priceRange = 2,
        goodForMeal = setOf("dinner"), ambience = setOf("casual"),
    )
    private val sushi = Restaurant(
        id = "2", name = "Sushi Zen", city = "Toronto", state = "ON",
        categories = listOf("Japanese", "Sushi Bars"), stars = 4.8, reviewCount = 40, priceRange = 4,
        goodForMeal = setOf("dinner"), ambience = setOf("upscale"),
    )
    private val cafe = Restaurant(
        id = "3", name = "Bean There", city = "Toronto", state = "ON",
        categories = listOf("Coffee & Tea", "Breakfast & Brunch"), stars = 3.5, reviewCount = 900,
    )
    private val all = listOf(bbq, sushi, cafe)

    @Test
    fun `query matching is case-insensitive and needs every word`() {
        assertTrue(RestaurantFilters.matchesQuery(sushi, "sushi toronto"))
        assertTrue(RestaurantFilters.matchesQuery(bbq, "tx"))
        assertFalse(RestaurantFilters.matchesQuery(bbq, "sushi toronto"))
        assertTrue(RestaurantFilters.matchesQuery(bbq, "   "))
    }

    @Test
    fun `cuisine aliases match Yelp categories`() {
        assertTrue(Cuisine.byName("BBQ")!!.matches(bbq))
        assertTrue(Cuisine.byName("sushi")!!.matches(sushi))
        assertTrue(Cuisine.byName("Coffee & Tea")!!.matches(cafe))
        assertFalse(Cuisine.byName("Thai")!!.matches(sushi))
    }

    @Test
    fun `budget keeps restaurants without price data`() {
        assertTrue(RestaurantFilters.matchesBudget(bbq, Budget.MEDIUM))
        assertFalse(RestaurantFilters.matchesBudget(sushi, Budget.LOW))
        assertTrue(RestaurantFilters.matchesBudget(cafe, Budget.HIGH))
    }

    @Test
    fun `trending orders by review count`() {
        assertEquals(listOf("3", "1", "2"), RestaurantFilters.trending(all).map { it.id })
    }

    @Test
    fun `recommendations come from liked cuisines and skip excluded ids`() {
        val recs = RestaurantFilters.recommended(all, setOf("Sushi", "BBQ"), exclude = setOf("1"))
        assertEquals(listOf("2"), recs.map { it.id })
        assertTrue(RestaurantFilters.recommended(all, emptySet()).isEmpty())
    }

    @Test
    fun `decide filters by cuisine and ranks by meal, vibe and budget`() {
        val picks = RestaurantFilters.decide(
            all,
            cuisines = listOf(Cuisine.byName("BBQ")!!, Cuisine.byName("Japanese")!!),
            meal = Meal.DINNER,
            ambience = Ambience.LUXURY,
            budget = Budget.HIGH,
        )
        // Sushi matches meal + vibe + budget (3) and outranks BBQ (meal only), despite fewer reviews.
        assertEquals(listOf("2", "1"), picks.map { it.id })
    }
}
