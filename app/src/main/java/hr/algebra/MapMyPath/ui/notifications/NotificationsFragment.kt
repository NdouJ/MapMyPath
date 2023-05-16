package hr.algebra.MapMyPath.ui.notifications

 import android.app.AlertDialog
import android.app.Dialog
 import android.os.Bundle
 import android.speech.tts.TextToSpeech
 import android.util.Log
import android.view.*
import android.widget.*
 import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
 import com.google.gson.Gson
import hr.algebra.MapMyPath.databinding.FragmentNotificationsBinding
import hr.algebra.MapMyPath.model.WeatherData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
 import java.util.*


class NotificationsFragment : Fragment(), TextToSpeech.OnInitListener {

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
    private lateinit var weatherData: WeatherData
    private  var tts :TextToSpeech? = null
    private var weatherRecBackup: String? =null;

     var flag = 0


    private var _NotificationFragmentBinding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _NotificationFragmentBinding!!

    override fun onCreateView(

        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        setHasOptionsMenu(true)
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _NotificationFragmentBinding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = ""
        }
        _NotificationFragmentBinding?.imageView2!!.setOnClickListener{
            speekOut(weatherRecBackup!!)
        }
        speekOut("Hello welcome")
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

tts = TextToSpeech(requireContext(), this)
        
        try{

            getWeatherFropOpenWeatherApi();

        } catch (er : Exception){

            Log.e(this.toString(), "Weatherapi connection problem "+er.toString())
        }

while(true){
    if(flag==1){
        makeClothesRecomendation(weatherData)

        break
    }
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
             weatherData = gson.fromJson(response, WeatherData::class.java)


            launch(Dispatchers.Main) {

                progressBar.visibility = View.INVISIBLE
            }


            val cityfromApi = weatherData.name
            cityTextView.text = "City: " + cityfromApi
            mainTemp.text="Temperature ${String.format("%.2f", weatherData.main.temp-273.15)}  C "
            feels_temp.text="Feels temp: ${String.format("%.2f", weatherData.main.feels_like-273.15)}  C "
            presure.text="Presure: "+weatherData.main.pressure +" hPa"
            humidity.text="Humidity: ${weatherData.main.humidity}%"
            wind_speed.text="Wind Speed: ${weatherData.wind.speed} m/s"
            clouds.text="Cloudiness: ${weatherData.clouds.all} %"
            sunrise.text="Sunrise: ${weatherData.sys.sunrise}"
            sunset.text="Sunset: ${weatherData.sys.sunset}"


            flag=1

        }


    }



    private fun makeClothesRecomendation(weatherData: WeatherData?) {



        var temperatureRecommendation = ""
        when {
            weatherData!!.main.temp-273.15 < 0 -> temperatureRecommendation = "The temperature is below freezing. It's important to stay warm and cozy."
            weatherData!!.main.temp-273.15 >= 0 && weatherData.main.temp-273.15 <= 10 -> temperatureRecommendation = "The temperature is chilly outside. Make sure to bundle up to stay warm."
            weatherData!!.main.temp -273.15> 10 && weatherData.main.temp-273.15 <= 17 -> temperatureRecommendation = "The temperature is cooler outside, so it's a good idea to bring a light jacket or sweater."
            weatherData!!.main.temp-273.15 > 17 && weatherData.main.temp-273.15 <= 26 -> temperatureRecommendation = "The temperature is comfortable outside, enjoy the day!"
            else -> temperatureRecommendation = "The temperature is hot outside, so make sure to wear light clothing to stay cool."
        }

        var windRecommendation = ""
        when {
            weatherData!!.wind.speed <= 5 -> windRecommendation = "There's a light breeze outside, perfect weather for outdoor activities!"
            weatherData!!.wind.speed  > 5 && weatherData.wind.speed  <= 15 -> windRecommendation = "It's a bit windy outside, but it's still manageable."
            else -> windRecommendation = "It's very windy outside, so make sure to hold onto your hats and secure any loose items."
        }
        val rainLikelihood = weatherData.main.humidity + weatherData!!.clouds.all
        var rainRecommendation = ""

        when {
            rainLikelihood < 30 -> rainRecommendation = "It doesn't look like it's going to rain, so you can make the most of your outdoor activities!"
            rainLikelihood < 60 -> rainRecommendation = "It might rain, so make sure to bring an umbrella just in case."
            else -> rainRecommendation = "It's likely to rain, so it's a good idea to bring a raincoat or choose indoor activities."
        }

        val weatherRec= temperatureRecommendation+windRecommendation+rainRecommendation
        weatherRecBackup=weatherRec


//Toast.makeText(requireContext(), weatherRec, Toast.LENGTH_LONG).show()
        val dialog = CustomDialogFragment(weatherRec)
        dialog.show(requireFragmentManager(), "Weather-info")

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _NotificationFragmentBinding = null
    }

    class CustomDialogFragment(private val message: String) : DialogFragment() {

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle("Weather notification")
                .setMessage(message)
                .setPositiveButton("OK") { dialog, id ->
                    // Do something when the positive button is clicked
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // Do something when the negative button is clicked
                }
            val dialog = builder.create()


            val window = dialog.window
            val layoutParams = window?.attributes
            layoutParams?.gravity = Gravity.CENTER
            window?.attributes = layoutParams

            return dialog
        }

    }

    override fun onInit(status: Int)  {
            if (status == TextToSpeech.SUCCESS) {
                val result = tts!!.setLanguage(Locale.US)
                Log.d("TTS", "TextToSpeech initialized successfully")
            } else {
                Log.e("TTS", "TextToSpeech initialization failed")
            }


    }
    private fun speekOut ( text : String){
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }


    override fun onDestroy() {
        super.onDestroy()
        if (tts != null){
            tts?.stop()
            tts?.shutdown()
        }
        _NotificationFragmentBinding=null
    }
}




