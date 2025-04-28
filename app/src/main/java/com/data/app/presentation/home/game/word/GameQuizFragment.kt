package com.data.app.presentation.home.game.word

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil3.load
import com.data.app.R
import com.data.app.data.Quiz
import com.data.app.databinding.FragmentGameQuizBinding
import timber.log.Timber
import java.util.Locale

class GameQuizFragment : Fragment() {
    private var _binding: FragmentGameQuizBinding? = null
    private val binding: FragmentGameQuizBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val gameQuizViewModel: GameQuizViewModel by viewModels()

    private var currentIndex = 0  // 현재 문제 번호
    private var isAnswerSelected = false

    private var textToSpeech: TextToSpeech? = null
    var isTTSReady = false // TTS 준비 상태 플래그

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
        requestPermission()
        resetTTS()

        showQuestion()

        binding.btnComplete.setOnClickListener {
            if (isAnswerSelected) {  // 선택했을 때만 넘어가게
                currentIndex++
                isAnswerSelected = false
                binding.btnComplete.setTextColor(requireActivity().getColor(R.color.black))
                binding.btnComplete.isSelected=false
                showQuestion()
                Timber.d("currentIndex: $currentIndex")
            } else {
                Toast.makeText(requireContext(), "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
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
        if (currentIndex >= gameQuizViewModel.quiz.size) {
            // 문제 다 풀었을 때 처리
            Timber.d("모든 문제를 다 풀었습니다.")
            return
        }

        val question = gameQuizViewModel.quiz[currentIndex]

        with(binding) {
            when (question) {
                is Quiz.Word -> {
                    tvNum.text = question.num
                    tvQuestion.text = question.question
                    ivQuestion.visibility = View.VISIBLE
                    btnListening.visibility = View.GONE
                    ivQuestion.load(question.image)

                    showAnswer(question.answer)

                    rvLocation(anchorView = ivQuestion)  // Word 문제면 ivQuestion 기준으로 rv 붙이기
                }

                is Quiz.Listening -> {
                    tvNum.text = question.num
                    tvQuestion.text = question.question
                    ivQuestion.visibility = View.GONE
                    btnListening.visibility = View.VISIBLE

                    showAnswer(question.answer)

                    rvLocation(anchorView = btnListening)  // Listening 문제면 btnListening 기준으로 rv 붙이기

                    btnListening.setOnClickListener {
                        speakMessage(question.text)
                    }
                }

                else -> {
                    Timber.e("question type is error")
                }
            }
        }
    }

    private fun showAnswer(answer: List<Quiz.Word.Answer>) {
        val answerAdapter = GameQuizAnswerAdapter(
            clickAnswer = { isCorrect ->
                with(binding.btnComplete) {
                    isSelected = true
                    setTextColor(requireActivity().getColor(R.color.white))
                }
                isAnswerSelected = true
            }
        )
        binding.rvAnswer.adapter = answerAdapter
        answerAdapter.getList(answer)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}