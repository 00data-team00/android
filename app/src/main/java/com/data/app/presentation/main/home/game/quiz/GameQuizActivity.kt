package com.data.app.presentation.main.home.game.quiz

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.data.app.R
import com.data.app.databinding.ActivityGameQuizBinding
import android.graphics.PorterDuff
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

    private fun finalResult(success: Boolean){
        if (success) {
            binding.clGameQuiz.setBackgroundColor(Color.WHITE)
            binding.tvGameSuccessfail.text = getString(R.string.game_quiz_finalsucess)
            binding.tvGameComment.text = getString(R.string.game_quiz_commentsuccess)
            binding.ivGameFinal.setImageResource(R.drawable.ic_correct)
            binding.btnQuizNext.isSelected = true
            binding.btnQuizAgain.isSelected = true
            binding.rvWeeks.setBackgroundColor(ContextCompat.getColor(this, R.color.game_word_bg_green))
        }
        else{
            binding.clGameQuiz.setBackgroundColor(ContextCompat.getColor(this, R.color.game_word_bg_green))
            binding.tvGameSuccessfail.text = getString(R.string.game_quiz_finalfail)
            binding.tvGameComment.text = getString(R.string.game_quiz_commentafail)
            binding.ivGameFinal.setImageResource(R.drawable.ic_wrong)
            binding.btnQuizNext.isSelected = false
            binding.btnQuizAgain.isSelected = true
            binding.rvWeeks.setBackgroundColor(Color.WHITE)
            binding.progressQuiz.progressDrawable.setColorFilter(
                ContextCompat.getColor(this, R.color.game_flag_red),
                PorterDuff.Mode.SRC_IN)
        }
        binding.clGameSuccess.visibility = View.VISIBLE
    }

    fun barToFront(){
        binding.progressQuiz.bringToFront()
        binding.progressQuiz.invalidate()
        binding.progressQuiz.requestLayout()
    }
}