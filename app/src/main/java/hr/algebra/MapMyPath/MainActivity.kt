package hr.algebra.MapMyPath

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Rect
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import hr.algebra.MapMyPath.shared.Constants
import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator


class MainActivity : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSkip: Button = findViewById(R.id.btn_skip)
        val buttonSubmit : Button = findViewById(R.id.btn_submit)
        val rootView = findViewById<View>(android.R.id.content)
        val tvUserName : TextView = findViewById(R.id.tv_username)
        val IvrunningRabbit : ImageView = findViewById(R.id.rabbit_running)
        val tv_registration :TextView = findViewById(R.id.tv_registration)



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET), 1)
        }


        tv_registration.isClickable=true
        tv_registration.setOnClickListener {

          startActivity(Intent(this, RegistrationActivity::class.java))
        }




        if (!isLocationEnabled()){
            Toast.makeText(this, "Please Enable GPS location", Toast.LENGTH_LONG).show()

            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        } else {

            Toast.makeText(this, "GPS enebled", Toast.LENGTH_LONG).show()



        }


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
        val screenWidth = resources.displayMetrics.widthPixels.toFloat()
        val animation = ValueAnimator.ofFloat(-ivrunningRabbit.width.toFloat(), screenWidth)
        animation.duration = 2000
        animation.repeatCount = ValueAnimator.INFINITE
        animation.repeatMode = ValueAnimator.RESTART
        animation.addUpdateListener { valueAnimator ->
            val value = valueAnimator.animatedValue as Float
            ivrunningRabbit.translationX = value
        }

        val rotationAnimation = ObjectAnimator.ofFloat(ivrunningRabbit, "rotation", 0f, 360f)
        rotationAnimation.duration = 1000
        rotationAnimation.repeatCount = ValueAnimator.INFINITE

        val animatorSet = AnimatorSet()
        animatorSet.playTogether(animation, rotationAnimation)
        animatorSet.start()
    }



    private fun isLocationEnabled():Boolean{
        val locationManager:LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
               /* || locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER) */


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.isNotEmpty() &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, you can access the internet
            } else {
                // Permission was denied, you cannot access the internet
            }
        }
    }

}