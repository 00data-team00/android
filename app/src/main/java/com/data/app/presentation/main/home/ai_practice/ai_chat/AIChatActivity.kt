package com.data.app.presentation.main.home.ai_practice.ai_chat

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.data.app.databinding.ActivityAiChatBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.Locale
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.data.app.R
import com.data.app.data.PreviousPractice
import com.data.app.extension.home.aichat.TranslateState
import com.data.app.extension.home.aichat.AiChatState
import com.data.app.extension.home.aichat.StartChatState
import com.data.app.presentation.main.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime
import java.util.Date

@AndroidEntryPoint
class AIChatActivity : BaseActivity() {
    private lateinit var binding: ActivityAiChatBinding

    private val aiChatViewModel: AIChatViewModel by viewModels()
    private lateinit var aiChatAdapter: AIChatAdapter

    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var recognizerIntent: Intent
    private var textToSpeech: TextToSpeech? = null
    var isTTSReady = false // TTS 준비 상태 플래그

    private lateinit var refreshHandler: Handler
    private lateinit var refreshRunnable: Runnable

    lateinit var pref: SharedPreferences
    var lang: String = ""

    private var topicKr: String? = ""
    private var topicEn: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAiChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = this.getSharedPreferences("settings", Context.MODE_PRIVATE)
        lang = pref.getString("lang", "ko")!!

        setting()

    }

    private fun setting() {
        val token = intent.getStringExtra("accessToken")
        val topicId = intent.getIntExtra("topicId", -1)
        topicKr = intent.getStringExtra("topic")
        topicEn = intent.getStringExtra("topic_en")
        Timber.d("topicId: $topicId, accessToken: $token")

        aiChatViewModel.accessToken.observe(this) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
            speechRecognizer.setRecognitionListener(recognitionListener)
            recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName) //여분의 키
                putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ko-KR")
                // 자동 종료 시간을없애는건 불가능 -> 30초로 바꿈
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
                    30000
                ) // 30초
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
                    30000
                ) // 30초
                putExtra(
                    RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
                    30000
                ) // 최소 듣기 시간 (30초)
            }

            setTitle()
            requestPermission()
            resetTTS()
            setChats(topicId)

            clickButton()
            clickExitButton()
            clickSendButton()
            // clickAIButton()
            clickBack()
        }

        aiChatViewModel.saveToken(token!!)
    }

    // 대화 제목, 영어제목, 필수여부 표시
    private fun setTitle() {
        binding.tvAichatTitleKor.text = topicKr
        binding.tvAichatTitleEng.text = topicEn
        // "필수" 표시 -> 해당 학습이 필수학습인지 아닌지에 따라 표시할지 말지 달라짐.
        if (true) binding.tvAichatEssential.visibility = View.VISIBLE
        else binding.tvAichatEssential.visibility = View.GONE
    }

    private fun setChats(topicId: Int) {
        aiChatAdapter = AIChatAdapter(
            clickChat = { chat -> speakMessage(chat) },
            request = { id -> getTranslate(id) },
            change = { pos -> setTranslate(pos) })
        binding.rvChat.adapter = aiChatAdapter
        lifecycleScope.launch {
            aiChatViewModel.startChatState.collect{state->
                when(state){
                    is StartChatState.Success->{
                        aiChatAdapter.deleteLoadMessage()
                        aiChatAdapter.startAiMessage(state.response)
                        binding.rvChat.scrollToPosition(aiChatAdapter.itemCount - 1)
                    }

                    is StartChatState.Loading -> {}
                    is StartChatState.Error -> {
                        Timber.e("setChats start chat state is error!!")
                    }
                }
            }
        }

        val currentTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
        aiChatAdapter.loadAiMessage(currentTime)

        aiChatViewModel.startChat(topicId)
        getAiChat()
        //aiChatAdapter.getList(aiChatViewModel.mockAIChat.chatList)
    }

    private fun getAiChat() {
        lifecycleScope.launch {
            aiChatViewModel.aiChatState.collect{ state->
                when(state){
                    is AiChatState.Success->{
                        aiChatAdapter.deleteLoadMessage()
                        aiChatAdapter.addAiMessage(state.response.aiMessage)
                        binding.rvChat.scrollToPosition(aiChatAdapter.itemCount - 1)
                    }

                    is AiChatState.Loading -> {}
                    is AiChatState.Error -> {
                        Timber.e("get ai chat error!!")
                    }
                }

            }
        }
        /*binding.btnTemp.setOnClickListener{
            val newstring = "우리가 서버에서 받아올 AI 채팅메세지"
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            var newchat = PreviousPractice.ChatItem.Ai(name = "홍대의", profile = R.drawable.ic_profile2, chat = newstring, time = currentTime)
            //aiChatViewModel.addchat(newchat)
            //aiChatAdapter.addList(newchat)
            //binding.rvChat.scrollToPosition(aiChatAdapter.itemCount - 1)
        }*/
    }

    private fun clickSendButton() {
        binding.btnMicsend.setOnClickListener {
            if (binding.tvListentext.text != "Listening .. ..") {
                val newstring = binding.tvListentext.text.toString()
                speechRecognizer.stopListening()
                speechRecognizer.cancel()
                if (::refreshHandler.isInitialized) {
                    refreshHandler.removeCallbacksAndMessages(null)
                }
                with(binding) {
                    binding.btnMic.visibility = View.VISIBLE
                    binding.btnMicsend.visibility = View.GONE
                    binding.btnMicexit.visibility = View.GONE
                    binding.tvListentext.text = "Listening .. .."
                    binding.tvListentext.visibility = View.GONE
                    binding.tvListentext.setTextColor("#dddddd".toColorInt())
                    binding.cardListening.visibility = View.GONE
                }
                Toast.makeText(this@AIChatActivity, "전송 완료!", Toast.LENGTH_SHORT).show()
                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                //var newchat = PreviousPractice.ChatItem.My(chat = newstring, time = currentTime)
                //aiChatViewModel.addchat(newchat)
                aiChatAdapter.addUserMessage(newstring, currentTime)
                binding.rvChat.scrollToPosition(aiChatAdapter.itemCount - 1)

                val currentTime2 = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))
                aiChatAdapter.loadAiMessage(currentTime2)

                aiChatViewModel.getAiChat(newstring)
            }
        }
    }

    private fun clickExitButton() {
        binding.btnMicexit.setOnClickListener {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            if (::refreshHandler.isInitialized) {
                refreshHandler.removeCallbacksAndMessages(null)
            }
            with(binding) {
                binding.btnMic.visibility = View.VISIBLE
                binding.btnMicsend.visibility = View.GONE
                binding.btnMicexit.visibility = View.GONE
                binding.tvListentext.text = "Listening .. .."
                binding.tvListentext.visibility = View.GONE
                binding.tvListentext.setTextColor("#dddddd".toColorInt())
                binding.cardListening.visibility = View.GONE
            }
        }
    }

    private fun startListening() {
        speechRecognizer.startListening(recognizerIntent)
        startRefreshTimer()
    }

    private fun startRefreshTimer() {
        refreshHandler = Handler(Looper.getMainLooper())
        refreshRunnable = Runnable {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            speechRecognizer.startListening(recognizerIntent)
            startRefreshTimer()
        }
        refreshHandler.postDelayed(refreshRunnable, 25000L)
    }

    // 말하기 버튼 클릭
    private fun clickButton() {
        binding.btnMic.setOnClickListener {
            binding.btnMicexit.visibility = View.VISIBLE
            binding.cardListening.visibility = View.VISIBLE
            binding.btnMic.visibility = View.GONE
            binding.btnMicsend.visibility = View.VISIBLE

            stopTTS()
            startListening()

        }
    }

    private fun stopTTS() {
        textToSpeech?.stop()
    }

    // 음성 듣는 listener
    private val recognitionListener: RecognitionListener = object : RecognitionListener {
        override fun onReadyForSpeech(params: Bundle) {
            if (binding.tvListentext.text == "Listening .. ..") {
                Toast.makeText(this@AIChatActivity, "이제 말씀하세요!", Toast.LENGTH_SHORT).show()
                startBlinking()
            } else Toast.makeText(this@AIChatActivity, "잠시 지연..", Toast.LENGTH_SHORT).show()
        }

        override fun onBeginningOfSpeech() {
            blinkingJob?.cancel()
            binding.ivIcon1.visibility = View.GONE
            binding.ivIcon2.visibility = View.GONE
            binding.tvListentext.visibility = View.VISIBLE
        }

        // 입력받는 소리의 크기를 알려줌
        override fun onRmsChanged(rmsdB: Float) {}

        // 말을 시작하고 인식이 된 단어를 buffer에 담음
        override fun onBufferReceived(buffer: ByteArray) {}

        // 말하기를 중지하면 호출
        override fun onEndOfSpeech() {
            //startListening()
        }

        // 오류 발생했을 때 호출
        override fun onError(error: Int) {
            val message = when (error) {
                SpeechRecognizer.ERROR_AUDIO -> "오디오 에러"
                SpeechRecognizer.ERROR_CLIENT -> "클라이언트 에러"
                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "퍼미션 없음"
                SpeechRecognizer.ERROR_NETWORK -> "네트워크 에러"
                SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "네트워크 타임아웃"
                SpeechRecognizer.ERROR_NO_MATCH -> "찾을 수 없음"
                SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "RECOGNIZER 가 바쁨"
                SpeechRecognizer.ERROR_SERVER -> "서버가 이상함"
                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "말하는 시간초과"
                else -> "알 수 없는 오류임"
            }
            Timber.e("$message")
            if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT || error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
                //startListening()
            }
        }

        override fun onResults(results: Bundle) {}

        // 부분 인식 결과를 사용할 수 있을 때 호출
        override fun onPartialResults(partialResults: Bundle) {
            val matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
            if (!matches.isNullOrEmpty()) {
                binding.tvListentext.setTextColor("#339933".toColorInt())
                var currentText = binding.tvListentext.text.toString()
                if (currentText == "Listening .. ..") currentText = ""
                var newText = matches.joinToString(separator = " ")
                binding.tvListentext.text = "$currentText $newText"
            }
        }

        // 향후 이벤트를 추가하기 위해 예약
        override fun onEvent(eventType: Int, params: Bundle) {}
    }


    private fun requestPermission() {
        // 버전 체크, 권한 허용했는지 체크
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO), 0
            )
        }
    }

    private fun resetTTS() {
        // TTS 객체 초기화
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = textToSpeech?.setLanguage(Locale.KOREAN)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    // 언어 데이터가 없거나 지원하지 않는 언어일 때 처리
                    Timber.e("Language is not supported")
                    Toast.makeText(
                        this,
                        "TTS 언어 데이터가 필요합니다. Google TTS 앱에서 데이터를 설치해주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    // 사용자를 Google TTS 앱 또는 설정 페이지로 안내할 수 있는 인텐트 실행
                    val installIntent = Intent()
                    installIntent.action = TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA
                    this.startActivity(installIntent)
                } else {
                    isTTSReady = true // TTS가 준비되었음을 표시
                    textToSpeech?.setSpeechRate(1.2f) // TTS 속도 설정
                }
            } else {
                // TTS 초기화 실패 처리
                Log.e("aifragment", "Initialization failed")
                Toast.makeText(
                    this,
                    "TTS 초기화에 실패하였습니다. 앱 설정에서 TTS 엔진을 확인해주세요.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun speakMessage(text: String) {
        if (text.isNotBlank()) {
            Log.d("aifragment", "startinitialmessage, ttsready: $isTTSReady")
            if (isTTSReady) { // TTS가 준비되었고, 프래그먼트가 보일 때만 실행
                // 설명 메시지를 TTS로 말하기
                textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
                Log.d("aifragment", "tts is ready")
            } else {
                Log.d("aifragment", "tts is not ready")
            }
        }
    }

    private fun getTranslate(messageId: Int) {
        Timber.d("get translate msgId: ${messageId}")
        if (lang != "ko") {
            if (lang == "en") lang = "en-us"
            aiChatViewModel.getTranslate(messageId, lang)
        }
    }

    private fun setTranslate(position: Int) {
        Timber.d("set translate")

        aiChatViewModel.resetTranslateState()

        lifecycleScope.launchWhenStarted {
            aiChatViewModel.translateState.collectLatest { state ->
                when (state) {
                    is TranslateState.Success -> {
                        Timber.d("translate success: ${state.response.text}")
                        aiChatAdapter.translatePosition(position, state.response.text)
                        aiChatViewModel.resetTranslateState() // collect 종료 유도
                        cancel() // 이 collect 종료
                    }

                    is TranslateState.Loading -> Timber.d("translate loading...")
                    is TranslateState.Error -> {
                        Timber.e("translate error: ${state.message}")
                        aiChatViewModel.resetTranslateState()
                        cancel()
                    }

                    else -> Unit
                }
            }
        }
        /*lifecycleScope.launch {
            val state = aiChatViewModel.translateState.first{ it is TranslateState.Success || it is TranslateState.Error }

            when (state) {
                is TranslateState.Success -> {
                    Timber.d(state.response.text)
                    aiChatAdapter.translatePosition(position, state.response.text)
                }
                is TranslateState.Error -> {
                    Timber.e("get translate error!!")
                }
                else -> Unit
            }
            *//*aiChatViewModel.translateState.collect{ state->
                when(state){
                    is TranslateState.Success->{
                        Timber.d(state.response.text)
                        aiChatAdapter.translatePosition(position, state.response.text)
                    }
                    is TranslateState.Loading->{
                        Timber.d("get translate loading!!")
                    }
                    is TranslateState.Error->{
                        Timber.e("get translate error!!")
                    }
                }
            }*//*
        }*/
    }

    private var blinkingJob: Job? = null
    private fun startBlinking() {
        blinkingJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                binding.ivIcon1.visibility = View.VISIBLE
                binding.ivIcon2.visibility = View.GONE
                delay(300)

                binding.ivIcon1.visibility = View.GONE
                binding.ivIcon2.visibility = View.VISIBLE
                delay(300)
            }
        }
    }

    private fun clickBack() {
        binding.btnBack.setOnClickListener {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        textToSpeech?.stop()
    }

    @Override
    override fun onDestroy() {
        super.onDestroy()
        binding.btnBack.setOnClickListener {
            speechRecognizer.stopListening()
            speechRecognizer.cancel()
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(R.anim.stay, R.anim.slide_out_right)
        }

        textToSpeech?.stop()
    }
}