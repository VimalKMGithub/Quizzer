package com.example.quizpr

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import com.example.quizpr.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportFragmentManager.beginTransaction().replace(R.id.content, HomeFragment()).commit()
        binding.bottomBar.setOnItemSelectedListener {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            when (it) {
                0 -> transaction.replace(R.id.content, HomeFragment())
                1 -> transaction.replace(R.id.content, LeaderboardsFragment())
                2 -> transaction.replace(R.id.content, WalletFragment())
                3 -> transaction.replace(R.id.content, ProfileFragment())
            }
            transaction.commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.wallet) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            transaction.replace(R.id.content, WalletFragment())
            transaction.commit()
        }
        return super.onOptionsItemSelected(item)
    }
}