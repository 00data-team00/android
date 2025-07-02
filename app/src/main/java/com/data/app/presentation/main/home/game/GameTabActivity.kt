package com.data.app.presentation.main.home.game

import android.content.Intent
import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.app.R
import com.data.app.databinding.ActivityGameTabBinding
import com.data.app.extension.UserGameInfoState
import com.data.app.presentation.main.BaseActivity
import com.data.app.presentation.main.home.game.quiz.GameQuizActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class GameTabActivity : BaseActivity() {
    private lateinit var binding: ActivityGameTabBinding

    private val gameTabViewModel: GameTabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityGameTabBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        saveToken()
        showWeeks()
        clickBack()
    }

    private fun saveToken() {
        val accessToken = intent.getStringExtra("accessToken")

        lifecycleScope.launch {
            gameTabViewModel.accessToken.observe(this@GameTabActivity) {
                getLevelInfo()
            }
        }

        gameTabViewModel.saveToken(accessToken!!)
    }

    private fun showWeeks() {
        val weekAdapter = GameTabWeekAdapter()
        binding.rvWeeks.adapter = weekAdapter
        weekAdapter.getList(gameTabViewModel.weeks)
    }

    private fun getLevelInfo() {
        lifecycleScope.launchWhenResumed {
            gameTabViewModel.userGameInfoState.collect { state ->
                when (state) {
                    is UserGameInfoState.Success -> {
                        showLevels()
                        gameTabViewModel.resetUserGameInfoState()
                    }

                    is UserGameInfoState.Loading -> {}
                    is UserGameInfoState.Error -> {}
                }
            }
        }

        gameTabViewModel.getUserGameInfo()
    }

    private fun showLevels() {
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true).apply {
            stackFromEnd = true
        }
        binding.rvLevels.layoutManager = layoutManager
        val levelAdapter = GameTabLevelAdapter(
            clickLevel = {
                val accessToken = gameTabViewModel.accessToken.value

                val intent = Intent(this, GameQuizActivity::class.java)
                intent.putExtra("level", it)
                intent.putExtra("accessToken", accessToken)
                startActivity(intent)
                this.overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
            }
        )
        binding.rvLevels.post {
            levelAdapter.getRecyclerViewWidth(binding.rvLevels.width)
            binding.rvLevels.adapter = levelAdapter
            binding.rvLevels.scrollToPosition(0)
        }
        levelAdapter.getList(gameTabViewModel.levels)
    }

    private fun clickBack() {
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }
    }

    override fun onResume() {
        super.onResume()
        gameTabViewModel.getUserGameInfo()
    }
}