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
    private var currentTabId = -1
    private var previousTabId: Int = R.id.menu_home

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
        Timber.d("token: $token")
        if (token != null) {
            mainViewModel.saveToken(token)
            Timber.d("token: $token")
        }

        setupBottomNavigation()
        binding.bnvMain.selectedItemId = R.id.menu_home
        switchTab(R.id.menu_home)
    }

    private fun setupBottomNavigation() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            switchTab(item.itemId)
            true
        }
    }

    private fun switchTab(targetTabId: Int) {
        val wasReselected = previousTabId == targetTabId
        previousTabId = currentTabId
        currentTabId = targetTabId

        val tag = "tab_$targetTabId"
        var targetFragment = supportFragmentManager.findFragmentByTag(tag)

        val transaction = supportFragmentManager.beginTransaction()

        // 모든 탭 숨김
        listOf(R.id.menu_home, R.id.menu_explore, R.id.menu_community, R.id.menu_my).forEach { id ->
            val existing = supportFragmentManager.findFragmentByTag("tab_$id")
            if (existing != null && id != targetTabId) {
                transaction.hide(existing)
            }
        }

        val isNewlyCreated = targetFragment == null

        if (isNewlyCreated) {
            val navHost = NavHostFragment.create(getNavGraphId(targetTabId))
            transaction.add(R.id.fcv_main, navHost, tag)
            targetFragment = navHost
        } else {
            transaction.show(targetFragment!!)
        }

        transaction.commitNow()

        val currentNavHost = targetFragment as? NavHostFragment
        val navController = currentNavHost?.navController

        if (wasReselected) {
            navController?.popBackStack(navController.graph.startDestinationId, false)

            val fragment = currentNavHost
                ?.childFragmentManager
                ?.fragments
                ?.firstOrNull()
            (fragment as? OnTabReselectedListener)?.onTabReselected()
        }
    }


    /* private fun switchTab(targetTabId: Int) {
         val wasCurrentTab = targetTabId == currentTabId
         val tag = "tab_$targetTabId"
         var targetFragment = supportFragmentManager.findFragmentByTag(tag)

         val transaction = supportFragmentManager.beginTransaction()

         // 모든 탭 숨김
         listOf(R.id.menu_home, R.id.menu_explore, R.id.menu_community, R.id.menu_my).forEach { id ->
             val existing = supportFragmentManager.findFragmentByTag("tab_$id")
             if (existing != null && id != targetTabId) {
                 transaction.hide(existing)
             }
         }

         val isNewlyCreated = targetFragment == null

         if (isNewlyCreated) {
             val navHost = NavHostFragment.create(getNavGraphId(targetTabId))
             transaction.add(R.id.fcv_main, navHost, tag)
             targetFragment = navHost
         } else {
             transaction.show(targetFragment!!)
         }

         transaction.commitNow()

         val currentNavHost = targetFragment as? NavHostFragment
         val navController = currentNavHost?.navController

         if (wasCurrentTab) {
             // ❗️오직 같은 탭을 다시 눌렀을 때만 popBackStack 수행
             navController?.popBackStack(navController.graph.startDestinationId, false)

             val fragment = currentNavHost
                 ?.childFragmentManager
                 ?.fragments
                 ?.firstOrNull()
             (fragment as? OnTabReselectedListener)?.onTabReselected()
         }

         currentTabId = targetTabId
     }
 */

    /* private fun switchTab(targetTabId: Int) {
         val wasCurrentTab = targetTabId == currentTabId
         val tag = "tab_$targetTabId"
         var targetFragment = supportFragmentManager.findFragmentByTag(tag)

         val transaction = supportFragmentManager.beginTransaction()

         // 모든 탭 숨김
         listOf(R.id.menu_home, R.id.menu_explore, R.id.menu_community, R.id.menu_my).forEach { id ->
             val existing = supportFragmentManager.findFragmentByTag("tab_$id")
             if (existing != null && id != targetTabId) {
                 transaction.hide(existing)
             }
         }

         val isNewlyCreated = targetFragment == null

         // 해당 탭 프래그먼트가 없으면 생성, 있으면 show
         if (isNewlyCreated) {
             val navHost = NavHostFragment.create(getNavGraphId(targetTabId))
             transaction.add(R.id.fcv_main, navHost, tag)
             targetFragment = navHost
         } else {
             transaction.show(targetFragment!!)
         }

         transaction.commitNow()
         // 새로 만든 게 아니라면 → 무조건 onTabReselected() 호출
         if (!isNewlyCreated) {
             val currentNavHost = targetFragment as? NavHostFragment
             val navController = currentNavHost?.navController

             navController?.popBackStack(navController.graph.startDestinationId, false)
             val fragment = currentNavHost
                 ?.childFragmentManager
                 ?.fragments
                 ?.firstOrNull()
             (fragment as? OnTabReselectedListener)?.onTabReselected()

             *//* val currentNavHost = targetFragment as? NavHostFragment
             val navController = currentNavHost?.navController

             val popped = navController?.popBackStack(navController.graph.startDestinationId, false) ?: false
             if (!popped) {
                 val fragment = currentNavHost
                     ?.childFragmentManager
                     ?.fragments
                     ?.firstOrNull()
                 (fragment as? OnTabReselectedListener)?.onTabReselected()
             }*//*
        }

        currentTabId = targetTabId
    }
*/

    /*private fun switchTab(targetTabId: Int) {
        if (targetTabId == currentTabId) {
            val currentNavHost = supportFragmentManager.findFragmentByTag("tab_$targetTabId") as? NavHostFragment
            val navController = currentNavHost?.navController
            val popped = navController?.popBackStack(navController.graph.startDestinationId, false) ?: false
            if (!popped) {
                val reselFragment = currentNavHost
                    ?.childFragmentManager
                    ?.fragments
                    ?.firstOrNull()

                if (reselFragment is OnTabReselectedListener) {
                    Timber.d("🔁 onTabReselected triggered for tab $targetTabId")
                    reselFragment.onTabReselected()
                } else {
                    Timber.w("🚨 No fragment implementing OnTabReselectedListener found for tab $targetTabId")
                }
               // (currentNavHost?.childFragmentManager?.fragments?.firstOrNull() as? OnTabReselectedListener)?.onTabReselected()
            }
            return
        }

        val tag = "tab_$targetTabId"
        var targetFragment = supportFragmentManager.findFragmentByTag(tag)

        val transaction = supportFragmentManager.beginTransaction()

        // 먼저 모든 fragment를 숨김
        listOf(R.id.menu_home, R.id.menu_explore, R.id.menu_community, R.id.menu_my).forEach { id ->
            val existing = supportFragmentManager.findFragmentByTag("tab_$id")
            if (existing != null && id != targetTabId) {
                transaction.hide(existing)
            }
        }

        // 없으면 새로 생성
        if (targetFragment == null) {
            val navHost = NavHostFragment.create(getNavGraphId(targetTabId))
            transaction.add(R.id.fcv_main, navHost, tag)
            targetFragment = navHost
        } else {
            transaction.show(targetFragment)
        }

        transaction.commitNow()
        currentTabId = targetTabId
    }*/

    private fun getNavGraphId(menuId: Int): Int {
        return when (menuId) {
            R.id.menu_home -> R.navigation.nav_graph_home
            R.id.menu_explore -> R.navigation.nav_graph_explore
            R.id.menu_community -> R.navigation.nav_graph_community
            R.id.menu_my -> R.navigation.nav_graph_my
            else -> throw IllegalArgumentException("Unknown menu id")
        }
    }


    override fun onBackPressed() {
        super.onBackPressed()
        val quitFragment = supportFragmentManager.findFragmentById(R.id.fcv_quit)
        if (quitFragment != null) {
            supportFragmentManager.popBackStack()
            binding.fcvQuit.visibility = View.GONE
            return
        }

        val currentNavHost = supportFragmentManager.findFragmentByTag("tab_$currentTabId") as? NavHostFragment
        val navController = currentNavHost?.navController

        if (navController == null || !navController.popBackStack()) {
            val now = System.currentTimeMillis()
            if (now - lastBackPressedTime < 2000) {
                finish()
            } else {
                Toast.makeText(this, "한 번 더 누르면 종료됩니다", Toast.LENGTH_SHORT).show()
                lastBackPressedTime = now
            }
        }
       /* val navHostFragment = supportFragmentManager.findFragmentById(navHostMap[currentTabId]!!.id) as NavHostFragment
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
        }*/
    }
}