package com.data.app.presentation.main.home.game.quiz

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil.load
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.response_dto.ResponseQuizDto
import com.data.app.databinding.FragmentGameQuizBinding
import com.data.app.extension.QuizCompleteState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale

@AndroidEntryPoint
class GameQuizFragment : Fragment() {
    private var _binding: FragmentGameQuizBinding? = null
    private val binding: FragmentGameQuizBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val gameQuizViewModel: GameQuizViewModel by activityViewModels()

    private var currentIndex = 0  // 현재 문제 번호
    private var isAnswerSelected = false
    private var currentAnswerIsCorrect: Boolean? = null


    private val gameActivity: GameQuizActivity?
        get() = activity as? GameQuizActivity
    private var textToSpeech: TextToSpeech? = null
    var isTTSReady = false // TTS 준비 상태 플래그

    private var isCorrectAnswer:Boolean ?=null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        resetTTS()
        showQuestion()
    }

    private fun resetTTS() {
        // TTS 객체 초기화
        textToSpeech = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 언어 데이터가 없거나 지원하지 않는 언어일 때 처리
                    Timber.e("Language is not supported")
                    Toast.makeText(
                        context,
                        "TTS 언어 데이터가 필요합니다. Google TTS 앱에서 데이터를 설치해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    // 사용자를 Google TTS 앱 또는 설정 페이지로 안내할 수 있는 인텐트 실행
                    val installIntent = Intent()
                    installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                    context?.startActivity(installIntent)
                } else {
                    isTTSReady = true // TTS가 준비되었음을 표시
                    textToSpeech?.setSpeechRate(1.0f) // TTS 속도 설정
                }
            } else {
                // TTS 초기화 실패 처리
                Log.e("aifragment", "Initialization failed")
                Toast.makeText(
                    context,
                    "TTS 초기화에 실패하였습니다. 앱 설정에서 TTS 엔진을 확인해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun showQuestion() {
        binding.sflIvQuestion.visibility=View.VISIBLE
        binding.sflIvQuestion.startShimmer()

        val quiz = gameQuizViewModel.quiz.value!!
        if (currentIndex == quiz.size) {
            // 문제 다 풀었을 때 처리
            Timber.d("모든 문제를 다 풀었습니다.")
            gameActivity?.onAllQuestionsCompleted()
            return
        }

        val question = quiz[currentIndex]
        isAnswerSelected = false // 새 문제 표시 시 선택 상태 초기화
        currentAnswerIsCorrect = null
        binding.btnComplete.isSelected = false
        binding.btnComplete.setTextColor(requireActivity().getColor(R.color.black))

        // category에 따라 보여줌
        with(binding) {
            if(question.category=="그림/단어 매칭"){
                tvNum.text = getString(R.string.game_quiz_num, currentIndex + 1)
                tvQuestion.text = getString(R.string.game_quiz_picture)
                ivQuestion.visibility = View.VISIBLE
                btnListening.visibility = View.GONE
                ivQuestion.load("${BuildConfig.BASE_URL.trimEnd('/')}${question.image}"){
                    listener(
                        onError = { request, throwable ->
                            Timber.e("Image loading failed: ${throwable.throwable.message}")
                        },
                        onSuccess = {_,_,->
                            sflIvQuestion.stopShimmer()
                            sflIvQuestion.visibility=View.GONE
                            ivQuestion.visibility=View.VISIBLE
                        }
                    )
                }

                showAnswer(question)
                rvLocation(anchorView = ivQuestion)
            }else{
                tvNum.text =  getString(R.string.game_quiz_num, currentIndex + 1)
                tvQuestion.text = getString(R.string.game_quiz_picture)

                sflIvQuestion.stopShimmer()
                sflIvQuestion.visibility=View.GONE
                ivQuestion.visibility = View.GONE
                btnListening.visibility = View.VISIBLE

                showAnswer(question)

                rvLocation(anchorView = btnListening)  // Listening 문제면 btnListening 기준으로 rv 붙이기

                btnListening.setOnClickListener {
                    speakMessage(question.voice!!)
                }
            }
        }

        clickCompleteBtn(question)
    }

    private fun clickCompleteBtn(question: ResponseQuizDto.QuizDto) {
        binding.btnComplete.setOnClickListener {
            Timber.d("isanswerselected: $isAnswerSelected")
            if (isAnswerSelected) {  // 선택했을 때만 넘어가게
                // 선택한 답이 맞았는지 틀렸는지 activity로 전달
                currentAnswerIsCorrect?.let { isCorrect ->
                    gameActivity?.onQuestionAnswered(isCorrect)
                    if(isCorrect) {
                        updateProgressBar()
                        lifecycleScope.launch {
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
                            gameQuizViewModel.completeQuiz(currentIndex+1)
                        }
                    }
                }

                if (gameActivity?.isGameFinished == true) {
                    // 생명이 다 떨어져서 Activity에서 이미 finalResult()를 호출한 경우,
                    gameActivity!!.finalResult(false)
                    Timber.d("Game is already finished by Activity. No need to show GameSuccessOrNotFragment.")
                    return@setOnClickListener
                }
                val fragment = isCorrectAnswer?.let { it ->
                    GameSuccessOrNotFragment.newInstance(it)
                }
                fragment?.setOnNextClickListener(object : GameSuccessOrNotFragment.OnNextClickListener {
                    override fun onNextClicked(success: Boolean) {
                        if (success) {
                            updateProgressBar()
                            moveToNextQuestion()
                        } /*else {
                            // 문제 다시 풀게 하기

                        }*/
                    }
                })
                if (fragment != null) {
                    val bundle = Bundle()
                    bundle.putSerializable("question", question)

                    fragment.arguments = bundle
                    requireActivity().supportFragmentManager.beginTransaction()
                        .add(R.id.fcv_result, fragment) // 또는 .replace()
                        .commit()
                }
                (requireActivity() as GameQuizActivity).barToFront()
            } else {
                Toast.makeText(requireContext(), "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProgressBar() {
        (activity as? GameQuizActivity)?.updateProgress((currentIndex+1)*100/gameQuizViewModel.quiz.value!!.size)
    }

    private fun showAnswer(quiz:ResponseQuizDto.QuizDto) {
        val answerAdapter = GameQuizAnswerAdapter(
            clickAnswer = { answerIndex ->
                with(binding.btnComplete) {
                    isSelected = true
                    setTextColor(requireActivity().getColor(R.color.white))
                    isCorrectAnswer = (quiz.answer == answerIndex)
                }
                isAnswerSelected = true
                currentAnswerIsCorrect = (quiz.answer == answerIndex)
            }
        )
        binding.rvAnswer.adapter = answerAdapter
        answerAdapter.getList(quiz.choices)
    }

    private fun rvLocation(anchorView: View) {
        with(binding) {
            val constraintSet = ConstraintSet()
            constraintSet.clone(clQuestion)  // root는 clQuestion으로 설정해야 맞음
            constraintSet.connect(
                rvAnswer.id, ConstraintSet.TOP,
                anchorView.id, ConstraintSet.BOTTOM,
                20
            )

            constraintSet.applyTo(clQuestion)
        }
    }

    private fun speakMessage(text: String) {
        Log.d("gameQuizFragment", "startinitialmessage, ttsready: $isTTSReady")
        if (isTTSReady) { // TTS가 준비되었고, 프래그먼트가 보일 때만 실행
            // 설명 메시지를 TTS로 말하기
            textToSpeech?.speak(
                text,
                TextToSpeech.QUEUE_FLUSH,
                null,
                ""
            )
            Log.d("aifragment", "tts is ready")
        } else {
            Log.d("aifragment", "tts is not ready")
        }
    }

    private fun moveToNextQuestion() {
        currentIndex++
        isAnswerSelected = false
        binding.btnComplete.setTextColor(requireActivity().getColor(R.color.black))
        binding.btnComplete.isSelected = false
        showQuestion()
        Timber.d("currentIndex: $currentIndex")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        textToSpeech = null
        isTTSReady = false
        _binding = null
    }
}