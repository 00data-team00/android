package com.data.app.presentation.main.home.ai_practice.previous_practice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.data.app.R
import com.data.app.databinding.ActivityPreviousPracticeBinding
import java.util.Locale

class PreviousPracticeActivity:AppCompatActivity() {
    private lateinit var binding: ActivityPreviousPracticeBinding

    private val practiceRecordsViewModel: PreviousPracticeViewModel by viewModels()
    private lateinit var practiceRecordsAdapter: PreviousPracticeAdapter

    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityPreviousPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        binding.ivSearch.setOnClickListener {
            // 키보드 내리기
            val inputMethodManager =
                this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            binding.etSearch.clearFocus()
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 키보드 내리기
                val imm =
                    v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        resetTTS()
        setRecords()
        clickBack()
    }

    private fun setRecords() {
        practiceRecordsAdapter = PreviousPracticeAdapter { chat ->
            speakOut(chat)
        }
        binding.rvPracticeRecords.adapter = practiceRecordsAdapter
        practiceRecordsAdapter.getRecordsList(practiceRecordsViewModel.mockPracticeRecordList)
    }

    private fun resetTTS() {
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts.language = Locale.KOREAN

                tts.setSpeechRate(1.2f)
            }
        }
    }

    private fun speakOut(text: String) {
        if (text.isNotBlank()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
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
}