package hr.algebra.MapMyPath.model

data class MainData(
    val temp: Double,
    val feels_like: Double,
    val pressure: Int,
    val humidity: Int){


    fun getCelsius(number : Double):Double{

        return number-273.15
    }
}
