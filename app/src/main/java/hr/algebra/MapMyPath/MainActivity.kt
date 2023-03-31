package hr.algebra.MapMyPath

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Log.ASSERT
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import hr.algebra.MapMyPath.shared.Constants


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSkip: Button = findViewById(R.id.btn_skip)
        val buttonSubmit : Button = findViewById(R.id.btn_submit)
        val rootView = findViewById<View>(android.R.id.content)
        val  tvUserName : TextView = findViewById(R.id.tv_username)
        val IvrunningRabbit : ImageView = findViewById(R.id.rabbit_running)
        
        
        animateRabbit(IvrunningRabbit)

        buttonSkip.setOnClickListener {
              Log.e(this.toString(), "Entering app without authorization")
                startActivity(Intent(this, NavigationActivity::class.java))
            }

        
        

        buttonSubmit.setOnClickListener {

            val intent = Intent(this, NavigationActivity::class.java)
            intent.putExtra(Constants.USER_NAME, tvUserName.text.toString())

            //TODO: firebase ili neka autentifikacija s dodatnim funkcionalnostima

            Log.e(this.toString(), "Entering  with authorization")
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

    private fun animateRabbit(ivrunningRabbit: ImageView) {
       // val animation = ObjectAnimator.ofFloat(ivrunningRabbit, "translationX", 0f, 500f)
      //  animation.duration = 1000
      //  animation.start()

        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val animation = ValueAnimator.ofFloat(-ivrunningRabbit.width.toFloat(), screenWidth)
        animation.duration = 2000
        animation.repeatCount = ValueAnimator.INFINITE
        animation.repeatMode = ValueAnimator.RESTART
        animation.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            ivrunningRabbit.translationX = value
        }
        animation.start()
    }
}