package com.data.app.presentation.main.home.ai_practice.previous_practice

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.data.app.R
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousRecordsDto
import com.data.app.databinding.ActivityPreviousPracticeBinding
import com.data.app.extension.home.aichat.AIPreviousChatMessageState
import com.data.app.extension.home.aichat.AIPreviousPracticeState
import com.data.app.extension.home.aichat.TranslateState
import com.data.app.presentation.main.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import kotlin.getValue

@AndroidEntryPoint
class PreviousPracticeActivity: BaseActivity() {
    private lateinit var binding: ActivityPreviousPracticeBinding

    private val previousPracticeViewModel: PreviousPracticeViewModel by viewModels()
    private lateinit var previousPracticeAdapter: PreviousPracticeAdapter

    private lateinit var tts: TextToSpeech

    lateinit var pref: SharedPreferences
    var lang: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
        pref = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        lang = pref.getString("lang", "ko")!!
    }

    private fun initBinds() {
        binding = ActivityPreviousPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        val token = intent.getStringExtra("accessToken")
        previousPracticeViewModel.saveToken(token!!)

        getRecords()

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

    private fun getRecords(){
        previousPracticeViewModel.accessToken.observe(this){token->
            previousPracticeViewModel.getAIPreviousRecords()
        }
    }

    private fun setRecords() {
        previousPracticeAdapter = PreviousPracticeAdapter (
            showChatMessages = {id->
                Timber.d("click chat: $id")
                previousPracticeViewModel.getMessages(id)
            },
            stopChat = {speakStop()},
            clickChat = {chat->speakOut(chat)},
            request = { id -> getTranslate(id) },
            change = { pos, id2 -> setTranslate(pos, id2) }
        )
        binding.rvPracticeRecords.adapter = previousPracticeAdapter

        lifecycleScope.launch {
            previousPracticeViewModel.aiPreviousRecordsState.collect{state->
                when(state){
                    is AIPreviousPracticeState.Success->{
                        previousPracticeAdapter.getRecordsList(state.response.chatRooms)
                        searchList(state.response.chatRooms)
                        previousPracticeAdapter.setListLoading(false)
                    }
                    is AIPreviousPracticeState.Loading->{}
                    is AIPreviousPracticeState.Error->{
                        Timber.e("set records ai previous records state error")
                    }
                }
            }
        }

        lifecycleScope.launch {
            previousPracticeViewModel.aiPreviousChatMessagesState.collect{state->
                when(state){
                    is AIPreviousChatMessageState.Success->{
                        previousPracticeAdapter.getMessages(state.chatRoomId, state.response.messages)
                    }
                    is AIPreviousChatMessageState.Loading->{}
                    is AIPreviousChatMessageState.Error->{
                        Timber.e("set records ai previous chat messages state error")
                    }
                }
            }
        }
    }

    private fun getTranslate(messageId: Int) {
        Timber.d("get translate msgId: ${messageId}")
        if (lang != "ko") {
            if (lang == "en") lang = "en-us"
            previousPracticeViewModel.getTranslate(messageId, lang)
        }
    }

    private fun setTranslate(position: Int, messageId: Int) {
        Timber.d("set translate")

        previousPracticeViewModel.resetTranslateState()

        lifecycleScope.launchWhenStarted {
            previousPracticeViewModel.translateState.collectLatest { state ->
                when (state) {
                    is TranslateState.Success -> {
                        Timber.d("translate success: ${state.response.text}")
                        previousPracticeAdapter.translateChildChat(messageId, position, state.response.text)
                        previousPracticeViewModel.resetTranslateState() // collect 종료 유도
                        cancel() // 이 collect 종료
                    }

                    is TranslateState.Loading -> Timber.d("translate loading...")
                    is TranslateState.Error -> {
                        Timber.e("translate error: ${state.message}")
                        previousPracticeViewModel.resetTranslateState()
                        cancel()
                    }

                    else -> Unit
                }
            }
        }
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

    private fun speakStop() {
        tts.stop()
    }

    private fun clickBack() {
        tts.stop()
        binding.btnBack.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }
    }

    private fun searchList(followlist: List<ResponseAIPreviousRecordsDto.ChatRoom>){
        binding.etSearch.doOnTextChanged{ text, _, _, _ ->
            val keyword = text.toString().trim()

            Timber.d("keyword: $keyword")

            if (keyword.isEmpty()) {
                previousPracticeAdapter.updateList(followlist)
            } else {
                val filteredList = followlist.filter {
                    it.title.contains(keyword, ignoreCase = true) ||
                    it.description.contains(keyword, ignoreCase = true)
                }
                previousPracticeAdapter.updateList(filteredList)
            }
        }
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        tts.stop()
        finish()
    }
}