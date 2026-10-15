package com.darpal.foodlabrinthnew.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OsmParserTest {

    private val tags = mapOf(
        "amenity" to "restaurant",
        "name" to "Pasta Place",
        "cuisine" to "italian;pizza;coffee_shop",
        "diet:vegan" to "yes",
        "diet:halal" to "no",
        "addr:housenumber" to "12",
        "addr:street" to "King Street West",
        "addr:city" to "Toronto",
        "addr:province" to "ON",
        "addr:postcode" to "M5V 1A1",
        "opening_hours" to "Mo-Fr 11:00-22:00",
        "phone" to "+1 416 555 0100;+1 416 555 0101",
        "website" to "pastaplace.example",
        "outdoor_seating" to "yes",
        "takeaway" to "only",
    )

    @Test
    fun `maps an OSM node with common tags`() {
        val r = OsmParser.toRestaurant("node", 42L, 43.65, -79.38, tags)!!

        assertEquals("n42", r.id)
        assertEquals("https://www.openstreetmap.org/node/42", r.osmUrl)
        assertEquals(PlaceKind.RESTAURANT, r.kind)
        assertEquals(listOf("Italian", "Pizza", "Coffee Shop", "Vegan", "Restaurant"), r.categories)
        assertEquals("Italian · Pizza · Coffee Shop", r.cuisineLabel)
        assertEquals("12 King Street West, Toronto, ON M5V 1A1", r.fullAddress)
        assertEquals("+1 416 555 0100", r.phone)
        assertEquals("https://pastaplace.example", r.website)
        assertEquals(listOf("Outdoor seating", "Takeaway"), r.features)
    }

    @Test
    fun `ways use the prefix w and their centre coordinates`() {
        val r = OsmParser.toRestaurant("way", 7L, 1.0, 2.0, mapOf("amenity" to "cafe", "name" to "Bean"))!!
        assertEquals("w7", r.id)
        assertEquals(PlaceKind.CAFE, r.kind)
        assertEquals(listOf("Cafe"), r.categories)
        assertEquals("Cafe", r.cuisineLabel)
    }

    @Test
    fun `rejects elements without name, coordinates or a food amenity`() {
        assertNull(OsmParser.toRestaurant("node", 1L, 1.0, 1.0, mapOf("amenity" to "restaurant")))
        assertNull(OsmParser.toRestaurant("node", 1L, null, 1.0, mapOf("amenity" to "restaurant", "name" to "X")))
        assertNull(OsmParser.toRestaurant("node", 1L, 1.0, 1.0, mapOf("amenity" to "bank", "name" to "X")))
        assertNull(OsmParser.toRestaurant("node", 1L, 95.0, 1.0, mapOf("amenity" to "restaurant", "name" to "X")))
    }

    @Test
    fun `prettify handles underscores and hyphens`() {
        assertEquals("Tex-Mex", OsmParser.prettify("tex-mex"))
        assertEquals("Ice Cream", OsmParser.prettify("ice_cream"))
    }

    @Test
    fun `distance formatting and haversine`() {
        val toronto = GeoPoint(43.6532, -79.3832)
        val newYork = GeoPoint(40.7128, -74.0060)
        val km = GeoPoint.distanceMeters(toronto, newYork) / 1000
        // Published great-circle distance is roughly 550 km.
        assert(km in 540..560) { "was $km km" }
        assertEquals("350 m", formatDistance(354))
        assertEquals("1.2 km", formatDistance(1_234))
        assertEquals("15 km", formatDistance(15_400))
    }
}
