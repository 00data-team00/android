package com.data.app.presentation.main.home.game.quiz

import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.PixelFormat
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.PixelCopy
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import com.data.app.R
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.databinding.FragmentGameSuccessOrNotBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber

class GameSuccessOrNotFragment:Fragment() {
    private var _binding: FragmentGameSuccessOrNotBinding? = null
    private val binding: FragmentGameSuccessOrNotBinding
        get() = requireNotNull(_binding) { "home fragment is null" }
    private var isCorrectAnswer: Boolean = false
    private var listener: OnNextClickListener? = null

    companion object {
        fun newInstance(isCorrectAnswer: Boolean): GameSuccessOrNotFragment {
            val fragment = GameSuccessOrNotFragment()
            fragment.isCorrectAnswer = isCorrectAnswer
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentGameSuccessOrNotBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        showAnimation()
        correctAns()
    }

    private fun showAnimation(){
        binding.clQuizSuccessOrNot.alpha = 0f
        binding.clQuizSuccessOrNot.animate()
            ?.alpha(1f)
            ?.setDuration(400)
            ?.start()

        binding.clQuizSuccessOrNot.translationY = 300f
        binding.clQuizSuccessOrNot.animate()
            .translationY(0f)
            .setDuration(500)
            .start()

        binding.ivEmogi.translationY = 300f
        binding.ivEmogi.animate()
            .translationY(0f)
            .setDuration(500)
            .start()
    }

    private fun correctAns(){
        with(binding){
            if (isCorrectAnswer) {
                isCorrect(isCorrectAnswer, false)

                btnNext.setOnClickListener {
                    listener?.onNextClicked(true) // 정답이었으면 true 전달
                    parentFragmentManager.beginTransaction()
                        .remove(this@GameSuccessOrNotFragment)
                        .commit()
                }
            } else {
                isCorrect(isCorrectAnswer, false)

                btnSkip.setOnClickListener {
                    isCorrect(!isCorrectAnswer, true)

                    btnNext.setOnClickListener {
                        listener?.onNextClicked(true) // 스킵 눌러서 정답 처리했으면 true 전달
                        parentFragmentManager.beginTransaction()
                            .remove(this@GameSuccessOrNotFragment)
                            .commit()
                    }
                }

                btnNext.setOnClickListener {
                    listener?.onNextClicked(false) // 오답인데 스킵 안했으면 false 전달
                    parentFragmentManager.beginTransaction()
                        .remove(this@GameSuccessOrNotFragment)
                        .commit()
                }
            }
        }
    }

    private fun isCorrect(visible:Boolean, again:Boolean){
       with(binding){
           if(visible){
               clContent.visibility=View.VISIBLE
               tvAnswerReason.visibility=View.VISIBLE

               ivGameFail.visibility=View.GONE
               ivGameFailHeartLeft.visibility=View.GONE
               ivGameFailHeartRight.visibility=View.GONE
               btnSkip.visibility=View.GONE

               tvTitle.text = (if(again) getString(R.string.game_quiz_skip_actually) else getString(R.string.game_quiz_correct))
               ivEmogi.setImageResource(R.drawable.ic_correct)
               btnNext.isSelected=true
               btnNext.text=getString(R.string.game_quiz_success_next)

               val question = arguments?.getSerializable("question") as ResponseQuizDto.QuizDto

               question?.let{ quiz->
                   tvAnswer.text = quiz.choices.getOrNull(question.answer - 1)!!.word
                   tvAnswerDescription.text = quiz.wordScript
                   tvAnswerReason.text = quiz.answerScript
               }
           }else{
               tvTitle.text = getString(R.string.game_quiz_incorrect)
               ivEmogi.setImageResource(R.drawable.ic_wrong)
               btnNext.isSelected=false
               btnNext.text=getString(R.string.game_quiz_fail_next)

               clContent.visibility=View.GONE
               tvAnswerReason.visibility=View.GONE

               ivGameFail.visibility=View.VISIBLE
               ivGameFailHeartLeft.visibility=View.VISIBLE
               ivGameFailHeartRight.visibility=View.VISIBLE
               btnSkip.visibility=View.VISIBLE
           }
       }
    }

    fun setOnNextClickListener(l: OnNextClickListener) { listener = l }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    interface OnNextClickListener {
        fun onNextClicked(success: Boolean)
    }
}