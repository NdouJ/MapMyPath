package hr.algebra.MapMyPath.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import hr.algebra.MapMyPath.databinding.FragmentNotificationsBinding
import hr.algebra.MapMyPath.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class NotificationsFragment : Fragment() {

    private lateinit var cityTextView: TextView
    private lateinit var mainTemp: TextView
    private lateinit var feels_temp: TextView
    private lateinit var presure: TextView
    private lateinit var humidity: TextView
    private lateinit var clouds: TextView
    private lateinit var sunrise: TextView
    private lateinit var sunset: TextView
    private lateinit var wind_speed: TextView
    private lateinit var progressBar: ProgressBar


    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = ""
        }

        cityTextView = binding.idCity
        mainTemp=binding.idMainTemp
        feels_temp=binding.idFeelsTemp
        presure=binding.idPresure
        humidity=binding.idHumidity
        clouds=binding.idClouds
        sunrise=binding.idSunrise
        sunset=binding.idSunset
        wind_speed = binding.windSpeed
        progressBar = binding.progressBar


        try{

            getWeatherFropOpenWeatherApi();
        } catch (er : Exception){

            Log.e(this.toString(), "Weatherapi connection problem "+er.toString())
        }


        return root
    }

    private fun getWeatherFropOpenWeatherApi() {
        GlobalScope.launch(Dispatchers.IO) {
            progressBar.visibility = View.VISIBLE
            val apiKey = "cda5d4bb347f5cd215128a802a0419b6"
            val city = "Zagreb"
            val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"

            val response = URL(url).readText()
            val gson = Gson()
            val weatherData = gson.fromJson(response, WeatherData::class.java)


            launch(Dispatchers.Main) {
                progressBar.visibility = View.INVISIBLE
            }


            val cityfromApi = weatherData.name
            cityTextView.text = "City: " + cityfromApi
            mainTemp.text="Temperature"+weatherData.main.temp + "K"
            feels_temp.text="Feels temp: "+weatherData.main.feels_like +" K"
            presure.text="Presure"+weatherData.main.pressure
            humidity.text="Humidity: ${weatherData.main.humidity}%"
            wind_speed.text="Wind Speed: ${weatherData.wind.speed} m/s"
            clouds.text="Cloudiness: ${weatherData.clouds.all}%"
            sunrise.text="Sunrise: ${weatherData.sys.sunrise}"
            sunset.text="Sunset: ${weatherData.sys.sunset}"

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}