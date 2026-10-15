package com.darpal.foodlabrinthnew.data.osm

import org.junit.Assert.assertEquals
import org.junit.Test

class OverpassApiParseTest {

    // Shape of an Overpass `out center tags` response: nodes carry lat/lon, ways carry a center.
    private val response = """
        {
          "version": 0.6,
          "elements": [
            { "type": "node", "id": 101, "lat": 43.6510, "lon": -79.3830,
              "tags": { "amenity": "restaurant", "name": "Pho House", "cuisine": "vietnamese" } },
            { "type": "way", "id": 202, "center": { "lat": 43.6520, "lon": -79.3840 },
              "tags": { "amenity": "cafe", "name": "Corner Cafe", "opening_hours": "Mo-Su 07:00-18:00" } },
            { "type": "node", "id": 303, "lat": 43.65, "lon": -79.38,
              "tags": { "amenity": "restaurant" } },
            { "type": "node", "id": 101, "lat": 43.6510, "lon": -79.3830,
              "tags": { "amenity": "restaurant", "name": "Pho House" } },
            { "type": "relation", "id": 404 }
          ]
        }
    """.trimIndent()

    @Test
    fun `parses nodes and ways, skips unnamed and duplicate elements`() {
        val places = OverpassApi.parse(response)

        assertEquals(listOf("n101", "w202"), places.map { it.id })
        assertEquals(listOf("Vietnamese", "Restaurant"), places[0].categories)
        assertEquals(43.6520, places[1].latitude, 1e-9)
        assertEquals("Mo-Su 07:00-18:00", places[1].openingHours)
    }

    @Test
    fun `empty or missing elements give an empty list`() {
        assertEquals(0, OverpassApi.parse("""{"elements": []}""").size)
        assertEquals(0, OverpassApi.parse("""{"remark": "runtime error"}""").size)
    }
}
