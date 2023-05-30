package hr.algebra.MapMyPath

import hr.algebra.MapMyPath.ui.home.map.MapsFragment
import junit.framework.TestCase.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class ParseResponseTestClass {

    @Test
    fun testParseResponse() {
        val mapsFragment = MapsFragment()
        val singlePlaceJson = """
            {
                "results": [
                    {
                        "name": "Test Place",
                        "geometry": {
                            "location": {
                                "lat": 10.0,
                                "lng": 20.0
                            }
                        }
                    }
                ]
            }
        """.trimIndent()
        val result = mapsFragment.parseResponse(singlePlaceJson)
        assertEquals(1, result.size)
        assertEquals("Test Place", result[0].name)
        assertEquals(10.0, result[0].location.latitude)
        assertEquals(20.0, result[0].location.longitude)
    }

    @Test
    fun testParseDirectionsResponse() {
        val mapsFragment = MapsFragment()
        val singleRouteJson = """
            {
                "routes": [
                    {
                        "overview_polyline": {
                            "points": "abc123"
                        }
                    }
                ]
            }
        """.trimIndent()
        val result = mapsFragment.parseDirectionsResponse(singleRouteJson)

        assertEquals(2, result.size)
        assertEquals(1.0, result[0].latitude)
        assertEquals(2.0, result[0].longitude)
        assertEquals(3.0, result[1].latitude)
        assertEquals(4.0, result[1].longitude)
    }
}

