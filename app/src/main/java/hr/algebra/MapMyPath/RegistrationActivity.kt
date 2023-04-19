package hr.algebra.MapMyPath

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import hr.algebra.MapMyPath.databinding.ActivityRegistrationBinding
import android.Manifest
import android.speech.tts.TextToSpeech
import android.util.Log
import java.util.*


class RegistrationActivity : AppCompatActivity(), TextToSpeech.OnInitListener {

    private lateinit var registrationBinding: ActivityRegistrationBinding
    private  var tts :TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        registrationBinding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(registrationBinding?.root)
        tts = TextToSpeech(this, this)
        setSupportActionBar(registrationBinding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        registrationBinding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

    }
    override fun onInit(status: Int)  {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "This language is not supported")
            } else {
                speekOut("Welcome to the registration")
            }
        } else {
            Log.e("TTS", "Initialization failed")
        }
    }
    private fun speekOut ( text : String){
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }
}
