package com.data.app.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.data.app.R
import com.data.app.databinding.ActivityMainBinding
import com.data.app.presentation.home.HomeFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        binding.bnvMain.post {
            val navController = findNavController(R.id.fcv_main)
            binding.bnvMain.setupWithNavController(navController)
        }
        //clickBottomNavigation()
        //binding.bnvMain.selectedItemId = R.id.menu_home
        //replaceFragment(HomeFragment())
    }

    /*private fun clickBottomNavigation() {
        binding.bnvMain.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.menu_menu -> {
                    replaceFragment(MenuFragment())
                    true
                }

                R.id.menu_home -> {
                    replaceFragment(HomeFragment())
                    true
                }

                R.id.menu_map -> {
                    replaceFragment(MapFragment())
                    true
                }

                R.id.menu_my -> {
                    replaceFragment(MyFragment())
                    true
                }

                else -> {
                    false
                }
            }
        }
    }*/

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_main, fragment)
            .commit()
    }
}