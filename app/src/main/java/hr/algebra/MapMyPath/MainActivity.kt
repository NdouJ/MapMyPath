package hr.algebra.MapMyPath

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imageView = findViewById<ImageView>(R.id.myImageView)
      //  imageView.setImageResource()

        Picasso.get().load("https://upload.wikimedia.org/wikipedia/commons/2/27/Wkipedia_blank_world_map.jpg").into(imageView)

    }
}