package com.data.app.presentation.main.home.game.quiz

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.data.app.R
import com.data.app.databinding.ActivityGameQuizBinding
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.data.app.data.Week
import com.data.app.extension.QuizCompleteState
import com.data.app.extension.QuizState
import com.data.app.presentation.main.BaseActivity
import com.data.app.presentation.main.home.game.GameTabWeekAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class GameQuizActivity : BaseActivity() {
    private lateinit var binding: ActivityGameQuizBinding
    private lateinit var lifeAdapter: GameQuizLifeAdapter
    private val gameQuizViewModel : GameQuizViewModel by viewModels()
    private var currentLives = 3 // 초기 생명
    var isGameFinished = false
    private var currentGameQuizFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityGameQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        WindowCompat.setDecorFitsSystemWindows(window, false)

        ViewCompat.setOnApplyWindowInsetsListener(binding.progressQuiz) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<ConstraintLayout.LayoutParams> {
                bottomMargin = systemBarsInsets.bottom + 3  // 안전 영역 + 여유
            }
            insets
        }

        setLife()
        setQuestion()

        clickAgain()
        clickStop()

        binding.btnBack.setOnClickListener {
            clickBack()
        }

        onBackPressedDispatcher.addCallback(this) {
            clickBack()
        }
    }

    private fun setLife() {
        currentLives = 3
        lifeAdapter = GameQuizLifeAdapter()
        binding.rvLife.adapter = lifeAdapter
        lifeAdapter.updateLifeCount(currentLives)
    }

    private fun setQuestion() {
        isGameFinished = false

        val token=intent.getStringExtra("accessToken")!!

        lifecycleScope.launch {
            gameQuizViewModel.accessToken.observe(this@GameQuizActivity){
                val level = intent.getIntExtra("level", 0)
                showQuestions()

                lifecycleScope.launch{
                    gameQuizViewModel.level.observe(this@GameQuizActivity){
                        val lang = getSharedPreferences("settings", Context.MODE_PRIVATE)
                            .getString("lang", "ko") ?: "ko"

                        // 언어가 "en"이면 "en-US"로 설정, 아니면 해당 언어 그대로 사용
                        val locale = if (lang == "en") "en-US" else lang

                        gameQuizViewModel.getQuiz(locale)
                    }
                }

                gameQuizViewModel.saveLevel(level)
            }
        }

        gameQuizViewModel.saveToken(token!!)
    }

    private fun showQuestions(){
        lifecycleScope.launch {
            gameQuizViewModel.quizState.collect { state ->
                when(state){
                    is QuizState.Success->{
                        // ivLoadingBg와 laLoading이 동시에 fadeout으로 사라지도록 설정
                        binding.ivLoadingBg.animate().alpha(0f).setDuration(500).withEndAction {
                            // 애니메이션 끝난 후 GONE 처리
                            binding.ivLoadingBg.visibility = View.GONE
                        }

                        binding.laLoading.loop(false)
                        binding.laLoading.animate().alpha(0f).setDuration(500).withEndAction {
                            // 애니메이션 끝난 후 GONE 처리
                            binding.laLoading.visibility = View.GONE
                        }

                        val fragment = GameQuizFragment()
                        currentGameQuizFragment = fragment // 새로운 프래그먼트 인스턴스 참조 저장
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fcv_question, fragment) // ID가 fcv_question인 FrameLayout에 교체
                            // .addToBackStack(null) // 필요에 따라 추가
                            .commit()
                        // fcvQuestion FrameLayout 자체를 보이게 설정 (만약 이전에 GONE 처리했다면)
                        binding.fcvQuestion.visibility = View.VISIBLE
                    }
                    is QuizState.Loading->{}
                    is QuizState.Error->{
                        Timber.e("quiz state error")
                    }
                }
            }
        }
    }

    // life가 없으면 true, 남아있으면 false
    fun onQuestionAnswered(isCorrect: Boolean) {
        if (isGameFinished) return // 이미 게임이 종료되었다면 아무것도 하지 않음

        if (!isCorrect) {
            currentLives--
            lifeAdapter.updateLifeCount(currentLives)
            Timber.d("Incorrect answer. Lives remaining: $currentLives")
            if (currentLives <= 0) {
                Timber.d("All lives lost. Game Over - Failure.")
                //finalResult(false) // 생명 모두 소진 시 최종 실패
                isGameFinished = true
            }
            binding.clGameQuiz.setBackgroundColor(getColor(R.color.game_fail_pink))
            binding.progressQuiz.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.game_flag_red))
            Timber.d("game quiz background color is red")
        } else {
            Timber.d("Correct answer.")

        }
        // 정답이든 오답이든, 생명이 남아있다면 다음 문제로 진행 (Fragment에서 처리)

    }

    fun onAllQuestionsCompleted() {
        if (isGameFinished) return

        Timber.d("All questions completed.")
        if (currentLives > 0) {
            Timber.d("Lives remaining. Game Over - Success.")
            finalResult(true) // 생명이 남아있으면 최종 성공
        } else {
            // 이 경우는 onQuestionAnswered에서 이미 처리되었어야 함
            Timber.w("All questions completed, but no lives left. Should have been handled by onQuestionAnswered.")
            finalResult(false) // 안전 장치로 실패 처리
        }
        isGameFinished = true
    }

    fun updateProgress(percent:Int) {
        if(binding.progressQuiz.progress==percent) return

        binding.progressQuiz.progressTintList =
            ColorStateList.valueOf(ContextCompat.getColor(this, R.color.community_follow_green))
        ObjectAnimator.ofInt(binding.progressQuiz, "progress", percent).setDuration(300).start()
        Timber.d("Progress updated to $percent%")
        binding.clGameQuiz.setBackgroundColor(getColor(R.color.game_word_bg_green))
    }

    fun finalResult(success: Boolean) {
        Timber.d("Progress: ${binding.progressQuiz.progress}%")
        if (binding.clGameSuccess.visibility == View.VISIBLE && isGameFinished) {
            Timber.d("Final result screen is already visible and game is finished.")
            return // 이미 결과 화면이 보이고 게임이 종료된 상태면 중복 호출 방지
        }
        isGameFinished = true // 게임 종료 상태로 설정

        // 현재 붙어있는 GameQuizFragment를 제거
        currentGameQuizFragment?.let { fragment ->
            // 프래그먼트의 상태가 저장되기 전이고, 제거되지 않았을 때만 제거 시도
            if (!fragment.isStateSaved && !fragment.isRemoving) {
                try {
                    supportFragmentManager.beginTransaction().remove(fragment)
                        .commitAllowingStateLoss()
                    Timber.d("GameQuizFragment removed.")
                } catch (e: IllegalStateException) {
                    Timber.e(e, "Error removing fragment in finalResult")
                    // 이 경우 FrameLayout을 그냥 숨기는 것으로 대체할 수 있음
                    binding.fcvQuestion.visibility = View.GONE
                }
            } else {
                Timber.d("Fragment state already saved or fragment is being removed, hiding FrameLayout.")
                binding.fcvQuestion.visibility = View.GONE // 안전하게 FrameLayout 숨기기
            }
            currentGameQuizFragment = null // 참조 제거
        }
        // 만약 currentGameQuizFragment가 null이어도 FrameLayout은 숨겨야 할 수 있음
        if (currentGameQuizFragment == null && binding.fcvQuestion.visibility == View.VISIBLE) {
            binding.fcvQuestion.visibility = View.GONE
        }

        if (success) {
            binding.clGameQuiz.setBackgroundColor(Color.WHITE)
            binding.tvGameSuccessfail.text = getString(R.string.game_quiz_finalsucess)
            binding.tvGameComment.text = getString(R.string.game_quiz_commentsuccess)
            binding.ivGameFinal.setImageResource(R.drawable.ic_correct)
            binding.btnQuizStop.isSelected = true
            binding.btnQuizAgain.isSelected = true
            binding.rvWeeks.setBackgroundResource(R.drawable.bg_week_success_green)

           /* lifecycleScope.launch {
                gameQuizViewModel.quizCompleteState.collect { state->
                    when(state){
                        is QuizCompleteState.Success->{
                            Timber.d("quiz complete!")
                        }
                        is QuizCompleteState.Loading->{}
                        is QuizCompleteState.Error->{
                            Timber.e("quiz complete state error!!!")
                        }
                    }
                }
            }

            if(!gameQuizViewModel.accessToken.value.isNullOrEmpty()){
                gameQuizViewModel.completeQuiz()
            }*/
        } else {
            binding.clGameQuiz.setBackgroundColor(getColor(R.color.game_fail_pink))
            binding.tvGameSuccessfail.text = getString(R.string.game_quiz_finalfail)
            binding.tvGameComment.text = getString(R.string.game_quiz_commentafail)
            binding.ivGameFinal.setImageResource(R.drawable.ic_wrong)
            binding.btnQuizStop.isSelected = false
            binding.btnQuizAgain.isSelected = false
            binding.rvWeeks.setBackgroundResource(R.drawable.bg_week_fail_white)
            binding.progressQuiz.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(this, R.color.game_flag_red))
        }
        binding.clGameSuccess.visibility = View.VISIBLE
        //binding.fcvQuestion.visibility=View.GONE
        binding.rvLife.visibility = View.GONE

        setWeek()

        Timber.d("Progress: ${binding.progressQuiz.progress}%")
    }

    private fun setWeek() {
        val weekAdapter = GameTabWeekAdapter()
        val weeks = listOf(
            Week("월", false),
            Week("화", false),
            Week("수", false),
            Week("목", true),
            Week("금", true),
            Week("토", false),
            Week("일", false),
        )
        binding.rvWeeks.setPadding(0, binding.rvWeeks.paddingTop, 0, binding.rvWeeks.paddingBottom) //  좌우 패딩 초기화
        binding.rvWeeks.scrollToPosition(0)
        binding.rvWeeks.adapter = weekAdapter
        weekAdapter.getList(weeks)
        centerAlignItems(binding.rvWeeks, weekAdapter)
    }

    private fun centerAlignItems(recyclerView: RecyclerView, adapter: GameTabWeekAdapter) {
        recyclerView.doOnPreDraw { // RecyclerView의 크기가 측정된 후에 실행
            val initialPaddingTop = recyclerView.paddingTop
            val initialPaddingBottom = recyclerView.paddingBottom
            recyclerView.setPadding(0, initialPaddingTop, 0, initialPaddingBottom)

            val totalItemsWidth = calculateTotalItemsWidth(recyclerView, adapter)
            val recyclerViewWidth = recyclerView.width

            if (totalItemsWidth < recyclerViewWidth) {
                val padding = (recyclerViewWidth - totalItemsWidth) / 2
                if (padding > 0) {
                    // 기존 패딩 값을 유지하면서 추가하려면 현재 패딩 값을 가져와서 더해야 합니다.
                    // 여기서는 단순히 새로 설정하는 예시입니다.
                    val currentPaddingTop = recyclerView.paddingTop
                    val currentPaddingBottom = recyclerView.paddingBottom
                    recyclerView.setPadding(
                        padding,
                        currentPaddingTop,
                        padding,
                        currentPaddingBottom
                    )
                }
            } else {
                // 아이템 너비가 RecyclerView 너비보다 크거나 같으면 기존 패딩 또는 0으로 설정
                val currentPaddingTop = recyclerView.paddingTop
                val currentPaddingBottom = recyclerView.paddingBottom
                // 만약 원래 XML에 설정된 paddingStart/End가 있다면 그 값을 사용하거나 0으로 설정
                val defaultPaddingStart =
                    resources.getDimensionPixelSize(R.dimen.week_padding) // 예시
                val defaultPaddingEnd =
                    resources.getDimensionPixelSize(R.dimen.week_padding)   // 예시
                recyclerView.setPadding(
                    defaultPaddingStart,
                    currentPaddingTop,
                    defaultPaddingEnd,
                    currentPaddingBottom
                )
            }
        }
    }

    private fun calculateTotalItemsWidth(
        recyclerView: RecyclerView,
        adapter: GameTabWeekAdapter
    ): Int {
        var totalWidth = 0
        val layoutInflater = LayoutInflater.from(recyclerView.context)

        for (i in 0 until adapter.itemCount) {
            val view = layoutInflater.inflate(R.layout.item_game_week, recyclerView, false)
            view.measure(
                View.MeasureSpec.UNSPECIFIED,
                View.MeasureSpec.UNSPECIFIED
            )
            totalWidth += view.measuredWidth
        }

        return totalWidth
    }

    fun barToFront() {
        binding.progressQuiz.bringToFront()
        binding.progressQuiz.invalidate()
        binding.progressQuiz.requestLayout()
    }

    private fun clickAgain() {
        binding.btnQuizAgain.setOnClickListener {
            binding.clGameSuccess.visibility = View.GONE
            binding.rvLife.visibility = View.VISIBLE
            binding.clGameQuiz.setBackgroundColor(getColor(R.color.game_word_bg_green))
            setLife()
            setQuestion()

            binding.rvWeeks.adapter=null
            binding.progressQuiz.progress=0
            binding.progressQuiz.progressDrawable?.clearColorFilter()
        }
    }

    private fun clickStop() {
        binding.btnQuizStop.setOnClickListener {
            clickBack()
        }
    }

    private fun clickBack() {
        finish()
        overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
    }
}