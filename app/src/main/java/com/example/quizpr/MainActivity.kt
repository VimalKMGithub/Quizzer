package com.example.quizpr

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.quizpr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar
        setSupportActionBar(binding.toolbar)

        // Setup Navigation Drawer
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.drawerArrowDrawable.color = resources.getColor(android.R.color.white, theme)
        toggle.syncState()

        // Load Default Fragment
        supportFragmentManager.beginTransaction().replace(R.id.content, HomeFragment()).commit()

        // Bottom Navigation Bar Handling
        binding.bottomBar.setOnItemSelectedListener {
            val transaction = supportFragmentManager.beginTransaction()
            when (it) {
                0 -> transaction.replace(R.id.content, HomeFragment())
                1 -> transaction.replace(R.id.content, LeaderboardsFragment())
                2 -> transaction.replace(R.id.content, WalletFragment())
                3 -> transaction.replace(R.id.content, ProfileFragment())
            }
            transaction.commit()
        }

        // Drawer Navigation Handling
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            val transaction = supportFragmentManager.beginTransaction()
            when (menuItem.itemId) {
            }
            transaction.commit()
            binding.drawerLayout.closeDrawer(binding.navigationView)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.wallet) {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content, WalletFragment())
            transaction.commit()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(binding.navigationView)) {
            binding.drawerLayout.closeDrawer(binding.navigationView)
        } else {
            super.onBackPressed()
        }
    }
}