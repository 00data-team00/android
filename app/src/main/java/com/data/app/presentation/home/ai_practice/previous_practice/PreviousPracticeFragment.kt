package com.data.app.presentation.home.ai_practice.previous_practice

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.data.app.databinding.FragmentPreviousPracticeBinding
import java.util.Locale

class PreviousPracticeFragment:Fragment() {
    private var _binding: FragmentPreviousPracticeBinding?=null
    private val binding: FragmentPreviousPracticeBinding
        get()= requireNotNull(_binding){"AI Practice Fragment is null"}

    private val practiceRecordsViewModel: PreviousPracticeViewModel by viewModels()
    private lateinit var practiceRecordsAdapter: PreviousPracticeAdapter

    private lateinit var tts: TextToSpeech

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentPreviousPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        binding.ivSearch.setOnClickListener {
            // 키보드 내리기
            val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            binding.etSearch.clearFocus()
        }

        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEARCH) {
                // 키보드 내리기
                val imm = v.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)
                v.clearFocus()
                true
            } else {
                false
            }
        }

        resetTTS()
        setRecords()
        clickBackButton()
    }

    private fun setRecords(){
        practiceRecordsAdapter=PreviousPracticeAdapter { chat ->
            speakOut(chat)
        }
        binding.rvPracticeRecords.adapter=practiceRecordsAdapter
        practiceRecordsAdapter.getRecordsList(practiceRecordsViewModel.mockPracticeRecordList)
    }

    private fun resetTTS(){
        tts = TextToSpeech(requireContext()) { status ->
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

    private fun clickBackButton(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}