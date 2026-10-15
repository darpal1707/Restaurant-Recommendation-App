package com.darpal.foodlabrinthnew.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantFiltersTest {

    private val bbq = Restaurant(
        id = "n1", name = "Smoke House", latitude = 0.0, longitude = 0.0, city = "Austin", state = "TX",
        categories = listOf("Barbecue", "Restaurant"), openingHours = "Mo-Su 11:00-15:00", distanceMeters = 2_500,
    )
    private val sushi = Restaurant(
        id = "n2", name = "Sushi Zen", latitude = 0.0, longitude = 0.0, city = "Toronto",
        categories = listOf("Japanese", "Sushi", "Restaurant"), openingHours = "Mo-Su 17:00-23:00", distanceMeters = 800,
    )
    private val cafe = Restaurant(
        id = "n3", name = "Bean There", latitude = 0.0, longitude = 0.0, city = "Toronto", kind = PlaceKind.CAFE,
        categories = listOf("Cafe"), distanceMeters = 300,
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
    fun `cuisine aliases match OSM categories`() {
        assertTrue(Cuisine.byName("BBQ")!!.matches(bbq))
        assertTrue(Cuisine.byName("sushi")!!.matches(sushi))
        assertTrue(Cuisine.byName("Coffee & Tea")!!.matches(cafe))
        assertFalse(Cuisine.byName("Thai")!!.matches(sushi))
    }

    @Test
    fun `nearest orders by distance`() {
        assertEquals(listOf("n3", "n2", "n1"), RestaurantFilters.nearest(all).map { it.id })
    }

    @Test
    fun `recommendations come from liked cuisines`() {
        assertEquals(listOf("n2", "n1"), RestaurantFilters.recommended(all, setOf("Sushi", "BBQ")).map { it.id })
        assertTrue(RestaurantFilters.recommended(all, emptySet()).isEmpty())
    }

    @Test
    fun `decide filters by cuisine and distance and ranks by meal`() {
        val cuisines = listOf(Cuisine.byName("BBQ")!!, Cuisine.byName("Japanese")!!)
        // Lunch: the BBQ place is open at 12:30, sushi isn't, so BBQ ranks first despite being further.
        assertEquals(listOf("n1", "n2"), RestaurantFilters.decide(all, cuisines, Meal.LUNCH, null).map { it.id })
        // Within 1 km only the sushi place remains.
        assertEquals(listOf("n2"), RestaurantFilters.decide(all, cuisines, Meal.LUNCH, Distance.WALK).map { it.id })
        // 0.5 mi is about 805 m, so the 800 m sushi place still qualifies in miles.
        assertEquals(
            listOf("n2"),
            RestaurantFilters.decide(all, cuisines, Meal.LUNCH, Distance.WALK, UnitSystem.IMPERIAL).map { it.id },
        )
        // Cafe prefers cafés.
        assertEquals("n3", RestaurantFilters.decide(all, emptyList(), Meal.CAFE, null).first().id)
    }

    @Test
    fun `distance options are round numbers in each unit and fit the search radius`() {
        assertEquals("Within 0.5 mi", Distance.WALK.description(UnitSystem.IMPERIAL))
        assertEquals("Within 0.25 mi", Distance.AROUND_THE_CORNER.description(UnitSystem.IMPERIAL))
        assertEquals("Within 500 m", Distance.AROUND_THE_CORNER.description(UnitSystem.METRIC))
        assertEquals("Within 2 km", Distance.SHORT_TRIP.description(UnitSystem.METRIC))
        assertEquals(1_609, Distance.SHORT_TRIP.maxMeters(UnitSystem.IMPERIAL))
        Distance.entries.forEach { d ->
            UnitSystem.entries.forEach { u -> assertTrue(d.maxMeters(u) <= SEARCH_RADIUS_METERS) }
        }
    }
}
