package com.data.app.presentation.main

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentContainerView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.data.app.R
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.ActivityMainBinding
import com.data.app.extension.main.GetIdFromTokenState
import com.data.app.extension.my.MyProfileState
import com.data.app.presentation.main.community.CommunityFragmentDirections
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private var currentTabId = -1
    private var previousTabId: Int = R.id.menu_home

    private var lastBackPressedTime = 0L

    private lateinit var navHostMap: Map<Int, FragmentContainerView>
    private val mainViewModel:MainViewModel by viewModels()

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("🧠 savedInstanceState = $savedInstanceState")
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        showFirstFragment()
    }

    private fun showFirstFragment(){
        val token = intent.getStringExtra("token")
        Timber.d("profiletoken: ${token}")
        if (!token.isNullOrBlank()) {
            goCommunity()
            mainViewModel.getIdFromToken(token)
        }else{
            binding.bnvMain.selectedItemId = R.id.menu_home
            switchTab(R.id.menu_home, false)
            setupBottomNavigation()
        }
    }

    private fun goCommunity(){
        lifecycleScope.launch {
            mainViewModel.getIdFromTokenState.collect { state->
                when(state){
                    is GetIdFromTokenState.Success-> {
                        binding.bnvMain.selectedItemId = R.id.menu_community

                       /* val bundle = Bundle().apply {
                            putString("profile_id", state.response.contentId.toString())
                            //putBoolean("shouldNavigateToOtherProfile", true)
                        }*/

                        switchTab(R.id.menu_community, true)
                        setupBottomNavigation()
                        //navController?.navigate(R.id.communityFragment, bundle)

                        /*binding.fcvMain.post {
                            val navHost = supportFragmentManager.findFragmentByTag("tab_${R.id.menu_community}") as? NavHostFragment
                            val navController = navHost?.navController

                            *//*val action = CommunityFragmentDirections
                                .actionCommunityFragmentToOtherProfileFragment(state.response.contentId.toString()) // userId 넘김
                            navController?.navigate(action)*//*
                        }*/
                    }
                    is GetIdFromTokenState.Error->{
                        Timber.e("get id from token state error: ${state.message}")
                    }
                    is GetIdFromTokenState.Loading->{
                        Timber.d("get id from token state loading...")
                    }
                }
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bnvMain.setOnItemSelectedListener { item ->
            Timber.d("👆 유저 탭 클릭 감지: ${item.itemId}")
            switchTab(item.itemId, false)
            true
        }
    }

    private fun switchTab(targetTabId: Int, skipShow: Boolean) {
        Timber.d("🔁 switchTab 호출됨 targetTabId=$targetTabId, skipShow=$skipShow")
        val wasReselected = currentTabId  == targetTabId
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

        /*if (isNewlyCreated) {
            val navHost = if (args != null) {
                NavHostFragment.create(getNavGraphId(targetTabId), args)
            } else {
                NavHostFragment.create(getNavGraphId(targetTabId))
            }
            transaction.add(R.id.fcv_main, navHost, tag)
            targetFragment = navHost

            Timber.d("✅ navHost arguments = ${navHost.arguments}")
        } else {
            transaction.show(targetFragment!!)

            // 이미 생성된 경우에도 args가 있으면 넘겨줌
            args?.let { bundle ->
                (targetFragment as? NavHostFragment)
                    ?.childFragmentManager
                    ?.primaryNavigationFragment
                    ?.arguments = bundle
            }
        }*/

         if (isNewlyCreated) {
             Timber.d("newly created")
             val navHost = NavHostFragment.create(getNavGraphId(targetTabId))
             transaction.add(R.id.fcv_main, navHost, tag)
             targetFragment = navHost
         } else {
             if (!skipShow) {
                 transaction.show(targetFragment!!) // ✅ 조건적으로 show
             } else {
                 Timber.d("🔕 skipShow == true: show() 생략")
             }
             //transaction.show(targetFragment!!)
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
        //super.onBackPressed()
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