package com.data.app.presentation.home.game

import android.os.Bundle
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.data.app.R
import com.data.app.databinding.ActivityGameTabBinding

class GameTabActivity:AppCompatActivity() {
    private lateinit var binding:ActivityGameTabBinding

    private val gameTabViewModel:GameTabViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds(){
        binding= ActivityGameTabBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(){
        showWeeks()
        showLevels()
        clickBack()
    }

    private fun showWeeks(){
        val weekAdapter = GameTabWeekAdapter()
        binding.rvWeeks.adapter=weekAdapter
        weekAdapter.getList(gameTabViewModel.weeks)
    }

    private fun showLevels(){
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true).apply {
            stackFromEnd = true
        }
        binding.rvLevels.layoutManager = layoutManager
        val levelAdapter=GameTabLevelAdapter()
        binding.rvLevels.post {
            levelAdapter.getRecyclerViewWidth(binding.rvLevels.width)
            binding.rvLevels.adapter = levelAdapter
            binding.rvLevels.scrollToPosition(0)
        }
        levelAdapter.getList(gameTabViewModel.levels)
    }

    private fun clickBack(){
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }
    }
}