package com.data.app.presentation.home.game.word

import android.os.Bundle
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import com.data.app.R
import com.data.app.databinding.ActivityGameTabBinding
import com.data.app.databinding.ActivityGameWordBinding

class GameQuizActivity:AppCompatActivity() {
    private lateinit var binding:ActivityGameWordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds(){
        binding=ActivityGameWordBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(){
        setLife()
        setQuestion()
        clickBack()
    }

    private fun setLife(){
        val lifeAdapter = GameQuizLifeAdapter()
        binding.rvLife.adapter=lifeAdapter
    }

    private fun setQuestion(){
        supportFragmentManager.beginTransaction()
            .replace(R.id.fcv_question, GameQuizFragment())
//            .addToBackStack(null)
            .commit()
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