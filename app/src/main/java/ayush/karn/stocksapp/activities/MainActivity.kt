package ayush.karn.stocksapp.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import ayush.karn.stocksapp.R
import ayush.karn.stocksapp.databinding.ActivityMainBinding
import me.ibrahimsn.lib.SmoothBottomBar

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        val bottomBar: SmoothBottomBar = binding.bottomBar
        bottomBar.onItemSelected = {
            when (it) {
                0 -> navController.navigate(R.id.topGainerFragment)
                1 -> navController.navigate(R.id.topLosersFragment)
            }
        }
    }

    fun setBottomNavigationVisibility(visible: Boolean) {
        binding.bottomBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

}
