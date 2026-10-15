package com.darpal.foodlabrinthnew.data.osm

import com.darpal.foodlabrinthnew.data.model.GeoPoint
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
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

    @Test
    fun `error remarks with no elements are detected`() {
        val timeout = """{"elements": [], "remark": "runtime error: Query timed out in \"query\" at line 1 after 26 seconds."}"""
        assertTrue(OverpassApi.errorRemark(timeout)!!.contains("timed out"))
        assertNull(OverpassApi.errorRemark(response))
        assertNull(OverpassApi.errorRemark("""{"elements": []}"""))
        // An HTML error page instead of JSON is also a failure.
        assertEquals("Invalid response", OverpassApi.errorRemark("<html>504 Gateway Time-out</html>"))
    }

    @Test
    fun `query uses a dot decimal separator and the requested radius`() {
        val query = OverpassApi.buildQuery(GeoPoint(43.6532, -79.3832), 3000)
        assertTrue(query, query.contains("(around:3000,43.653200,-79.383200)"))
    }
}
