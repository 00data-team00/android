package com.data.app.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.navigation.fragment.NavHostFragment
import com.data.app.R
import com.data.app.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentTabId = R.id.menu_home
    private var lastBackPressedTime = 0L

    private lateinit var navHostMap: Map<Int, FragmentContainerView>
    private val mainViewModel:MainViewModel by viewModels()

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
       /* binding.bnvMain.post {
            val navController = findNavController(R.id.fcv_main)
            binding.bnvMain.setupWithNavController(navController)
        }*/
        //clickBottomNavigation()
        //replaceFragment(HomeFragment())
        val token=intent.getStringExtra("accessToken")
        if (token != null) {
            mainViewModel.saveToken(token)
            Timber.d("token: $token")
        }

        binding.bnvMain.selectedItemId = R.id.menu_home
        clickBottomNavigation()
    }

    private fun clickBottomNavigation() {
        navHostMap = mapOf(
            R.id.menu_community to binding.fcvCommunity,
            R.id.menu_home to binding.fcvHome,
            R.id.menu_explore to binding.fcvExplore,
            R.id.menu_my to binding.fcvMy
        )

        binding.bnvMain.setOnItemSelectedListener { item ->
            switchTab(item.itemId)
            true
        }
    }

    private fun switchTab(targetTabId: Int) {
        val isReselected = targetTabId == currentTabId

        navHostMap.forEach { (id, container) ->
            container.visibility = if (id == targetTabId) View.VISIBLE else View.GONE
        }

        val navHostFragment = supportFragmentManager.findFragmentById(
            navHostMap[targetTabId]!!.id
        ) as NavHostFragment

        val navController = navHostFragment.navController

        if (isReselected) {
            // 1. 루트가 아닐 경우 popBackStack
            val popped = navController.popBackStack(
                navController.graph.startDestinationId,
                false
            )
            // 2. 이미 루트 상태였다면 → onTabReselected() 호출
            if (!popped) {
                val currentFragment = navHostFragment.childFragmentManager.fragments.firstOrNull()
                if (currentFragment is OnTabReselectedListener) {
                    currentFragment.onTabReselected()
                }
            }
        }

        currentTabId = targetTabId
    }

    override fun onBackPressed() {
        val navHostFragment = supportFragmentManager.findFragmentById(navHostMap[currentTabId]!!.id) as NavHostFragment
        val navController = navHostFragment.navController

        // 현재 탭에서 popBackStack 할 게 있다면 pop
        if (!navController.popBackStack()) {
            // pop할 게 없으면 → 종료 로직
            val now = System.currentTimeMillis()
            if (now - lastBackPressedTime < 2000) {
                finish()
            } else {
                Toast.makeText(this, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
                lastBackPressedTime = now
            }
        }
    }
}