package com.darpal.foodlabrinthnew.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RestaurantTest {

    @Test
    fun `parses a Yelp style record with mixed value types`() {
        val map = mapOf(
            "business_id" to "abc-123",
            "name" to "Pasta Place",
            "address" to "1 Main St",
            "city" to "Toronto",
            "state" to "ON",
            "postal_code" to "M5V",
            "latitude" to "43.65",
            "longitude" to -79.38,
            "stars" to 4L,
            "review_count" to "120",
            "categories" to "Italian, Pizza ,Restaurants",
            "is_open" to 1L,
            "attributes" to mapOf(
                "RestaurantsPriceRange2" to "2",
                "GoodForMeal" to "{'dessert': False, 'lunch': True, 'dinner': True}",
                "Ambience" to mapOf("romantic" to true, "casual" to "False"),
            ),
        )

        val r = Restaurant.fromMap("key", map)!!

        assertEquals("abc-123", r.id)
        assertEquals(43.65, r.latitude!!, 0.0001)
        assertEquals(-79.38, r.longitude!!, 0.0001)
        assertEquals(4.0, r.stars!!, 0.0)
        assertEquals(120, r.reviewCount)
        assertEquals(listOf("Italian", "Pizza", "Restaurants"), r.categories)
        assertEquals(true, r.isOpen)
        assertEquals(2, r.priceRange)
        assertEquals("$$", r.priceLabel)
        assertEquals(setOf("lunch", "dinner"), r.goodForMeal)
        assertEquals(setOf("romantic"), r.ambience)
        assertEquals("1 Main St, Toronto, ON M5V", r.fullAddress)
    }

    @Test
    fun `falls back to the node key and rejects records without a name`() {
        assertEquals("node-key", Restaurant.fromMap("node-key", mapOf("name" to "X"))!!.id)
        assertNull(Restaurant.fromMap("node-key", mapOf("city" to "Nowhere")))
        assertNull(Restaurant.fromMap("node-key", mapOf("name" to "null")))
    }

    @Test
    fun `invalid coordinates are dropped`() {
        val r = Restaurant.fromMap("k", mapOf("name" to "X", "latitude" to "abc", "longitude" to "500"))!!
        assertNull(r.latitude)
        assertNull(r.longitude)
        assertTrue(!r.hasLocation)
    }

    @Test
    fun `hours from a map are ordered by weekday and formatted`() {
        val hours = Restaurant.parseHours(
            mapOf("Sunday" to "0:0-0:0", "Monday" to "9:0-17:30", "Wednesday" to "11:0-22:0"),
        )
        assertEquals(
            listOf(
                OpeningHours("Monday", "09:00 – 17:30"),
                OpeningHours("Wednesday", "11:00 – 22:00"),
                OpeningHours("Sunday", "Open 24 hours"),
            ),
            hours,
        )
    }

    @Test
    fun `hours stored as a map toString are still parsed`() {
        val hours = Restaurant.parseHours("{Tuesday=8:0-16:0, Monday=8:0-16:0}")
        assertEquals(listOf("Monday", "Tuesday"), hours.map { it.day })
        assertEquals("08:00 – 16:00", hours.first().time)
    }

    @Test
    fun `missing hours give an empty list`() {
        assertTrue(Restaurant.parseHours(null).isEmpty())
        assertTrue(Restaurant.parseHours("null").isEmpty())
    }

    @Test
    fun `review stars are parsed from strings and clamped`() {
        val review = Review.fromMap("r1", mapOf("business_id" to "b", "text" to "Great", "stars" to "7.0"))!!
        assertEquals(5f, review.stars)
        assertEquals(0f, Review.fromMap("r2", mapOf("business_id" to "b", "stars" to "null"))!!.stars)
    }
}
