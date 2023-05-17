package hr.algebra.MapMyPath.ui.notifications

import hr.algebra.MapMyPath.model.Clouds
import hr.algebra.MapMyPath.model.MainData
import hr.algebra.MapMyPath.model.Sys
import hr.algebra.MapMyPath.model.Weather
import hr.algebra.MapMyPath.model.WeatherData
import hr.algebra.MapMyPath.model.Wind
import org.junit.Assert.assertEquals
import org.junit.Test

class NotificationsFragmentTest {

    @Test
    fun testMakeClothesRecommendation() {
        val fragment = NotificationsFragment()
        val weatherList = listOf(
            Weather(800, "Clear", "clear sky", "01d"),
            Weather(801, "Clouds", "few clouds", "02d"),
            Weather(802, "Clouds", "scattered clouds", "03d"),
            Weather(803, "Clouds", "broken clouds", "04d"),
            Weather(804, "Clouds", "overcast clouds", "04d")
        )
        // Create a WeatherData object with a temperature of 10°C and wind speed of 5 m/s
        val mainData = MainData(temp = 10.0+273, feels_like = 8.0, pressure = 1013, humidity = 70)

        val weatherData = WeatherData(
            name ="Donji Grad",
            weather = weatherList,
            main = mainData,
            wind = Wind(20.0, 3),
            clouds= Clouds(3),
            sys= Sys("Croatia", 2252626, 216516)
            )


        val recommendation = fragment.makeClothesRecomendation(weatherData)

        // The recommendation for 10°C and 5 m/s wind speed should be "The temperature is chilly outside. Make sure to bundle up to stay warm."
        assertEquals("The temperature is chilly outside. Make sure to bundle up to stay warm.", recommendation)
    }
}
