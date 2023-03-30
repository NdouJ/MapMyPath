package hr.algebra.MapMyPath

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSkip: Button = findViewById(R.id.btn_skip)

        val buttonSubmit : Button = findViewById(R.id.btn_submit)
        buttonSkip.setOnClickListener {

                startActivity(Intent(this, NavigationActivity::class.java))
            }


        buttonSubmit.setOnClickListener {

            //TODO: firebase ili neka autentifikacija s dodatnim funkcionalnostima
            startActivity(Intent(this, NavigationActivity::class.java))

        }


    }
}