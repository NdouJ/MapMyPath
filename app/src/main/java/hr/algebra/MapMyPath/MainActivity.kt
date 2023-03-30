package hr.algebra.MapMyPath

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso



class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSkip: Button = findViewById(R.id.btn_skip)

        val buttonSubmit : Button = findViewById(R.id.btn_submit)

        val rootView = findViewById<View>(android.R.id.content)


        buttonSkip.setOnClickListener {

                startActivity(Intent(this, NavigationActivity::class.java))
            }


        buttonSubmit.setOnClickListener {

            //TODO: firebase ili neka autentifikacija s dodatnim funkcionalnostima
            startActivity(Intent(this, NavigationActivity::class.java))

        }

        rootView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val focusedView = currentFocus
                if (focusedView != null) {
                    if (focusedView is TextView) {
                        val outRect = Rect()
                        focusedView.getGlobalVisibleRect(outRect)
                        if (!outRect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                            focusedView.clearFocus()
                            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                            imm.hideSoftInputFromWindow(focusedView.windowToken, 0)
                        }
                    }
                }
            }
            false
        }







    }
}