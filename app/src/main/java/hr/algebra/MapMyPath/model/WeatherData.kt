package hr.algebra.MapMyPath.model

data class WeatherData(
    val name: String,
    val weather: List<Weather>,
    val main: MainData,
    val wind: Wind,
    val clouds: Clouds,
    val sys: Sys
)
