@file:Suppress("DEPRECATION")

package hr.algebra.MapMyPath

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.algebra.MapMyPath.databinding.ActivityNavigationBinding
import hr.algebra.MapMyPath.shared.Constants

class NavigationActivity : AppCompatActivity() {

    private lateinit var navigationBinding: ActivityNavigationBinding



    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        navigationBinding = ActivityNavigationBinding.inflate(layoutInflater)
        setContentView(navigationBinding?.root)

        setSupportActionBar(navigationBinding?.toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
supportActionBar?.setTitle("Welcome")

        navigationBinding?.toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        val name = intent.getStringExtra(Constants.USER_NAME)


        val navView: BottomNavigationView = navigationBinding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_navigation)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}