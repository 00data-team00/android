package com.data.app.presentation.home.game.quiz

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.data.app.R
import com.data.app.databinding.ActivityGameQuizBinding
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

class GameQuizActivity:AppCompatActivity() {
    private lateinit var binding:ActivityGameQuizBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds(){
        binding=ActivityGameQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting(){
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.progressQuiz) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = systemBarsInsets.bottom + 4  // 안전 영역 + 여유
            }
            insets
        }



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

    fun updateProgress(current: Int, total: Int) {
        val percent = (current * 100) / total
        ObjectAnimator.ofInt(binding.progressQuiz, "progress", percent).setDuration(300).start()
    }

    fun barToFront(){
        binding.progressQuiz.bringToFront()
        binding.progressQuiz.invalidate()
        binding.progressQuiz.requestLayout()
    }
}