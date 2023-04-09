package hr.algebra.MapMyPath.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
            textView.text = it
        }



        try{

            getWeatherFropOpenWeatherApi();
        } catch (er : Exception){

            Log.e(this.toString(), er.toString())
        }


        return root
    }

    private fun getWeatherFropOpenWeatherApi() {
        GlobalScope.launch(Dispatchers.IO) {
            val apiKey = "cda5d4bb347f5cd215128a802a0419b6"
            val city = "Zagreb"
            val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"

            val response = URL(url).readText()
            val gson = Gson()
            val weatherData = gson.fromJson(response, WeatherData::class.java)

            println("City: ${weatherData.name}")
            println("Weather: ${weatherData.weather[0].main}, ${weatherData.weather[0].description}")
            println("Temperature: ${weatherData.main.temp} K")
            println("Feels Like: ${weatherData.main.feels_like} K")
            println("Pressure: ${weatherData.main.pressure} hPa")
            println("Humidity: ${weatherData.main.humidity}%")
            println("Wind Speed: ${weatherData.wind.speed} m/s")
            println("Cloudiness: ${weatherData.clouds.all}%")
            println("Country: ${weatherData.sys.country}")
            println("Sunrise: ${weatherData.sys.sunrise}")
            println("Sunset: ${weatherData.sys.sunset}")

            launch(Dispatchers.Main) {
                Toast.makeText(requireContext(), weatherData.main.humidity.toString(), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}