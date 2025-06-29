package com.data.app.presentation.main.home

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil3.Canvas
import coil3.load
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.RoundedCornersTransformation
import coil3.transform.Transformation
import com.data.app.R
import com.data.app.data.Language
import com.data.app.databinding.FragmentHomeBinding
import com.data.app.presentation.main.MainViewModel
import com.data.app.presentation.main.OnTabReselectedListener
import com.data.app.presentation.main.home.ai_practice.AIPracticeActivity
import com.data.app.presentation.main.home.game.GameTabActivity
import com.data.app.util.security.updateLocale
import timber.log.Timber
import androidx.core.content.edit
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController

class HomeFragment:Fragment(), OnTabReselectedListener {
    private var _binding:FragmentHomeBinding?=null
    private val binding:FragmentHomeBinding
        get() = requireNotNull(_binding){"home fragment is null"}


    private val mainViewModel:MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding=FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        mainViewModel.accessToken.observe(viewLifecycleOwner){token->
            clickPractice(token)
        }
        showImage()
        //clickPractice(token)
        inputMockData()
        clickGame()
    }

    private fun showImage(){
        val radiusPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 20f, resources.displayMetrics
        )

        binding.ivOnline.load(R.drawable.iv_online){
            transformations(
                RoundedCornersTransformation(radiusPx)
            )
        }
        binding.ivQuiz.load(R.drawable.iv_quiz){
            transformations(
                RoundedCornersTransformation(radiusPx)
            )
        }
        binding.ivAiPractice.load(R.drawable.iv_ai_practice) {
            transformations(
                RoundedCornersTransformation(radiusPx)
            )
        }
    }

    private fun inputMockData(){
        with(binding){
            tvTitleKor.text=getString(R.string.home_title, "구구")
            tvSubtitleEng.text=getString(R.string.home_subtitle_eng, "GooGoo")
            tvNotGiveUp.text=getString(R.string.home_not_give_up, "구구")
            tvContinueStudy.text=getString(R.string.home_continue_study, 13)

            val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
            val lang = prefs.getString("lang", "ko")

            // 🔽 언어 코드에 따라 국기 이미지 설정
            val flagRes = when (lang) {
                "ko" -> R.drawable.ic_korea
                "en-US", "en" -> R.drawable.ic_america
                "zh" -> R.drawable.ic_china
                "ja" -> R.drawable.ic_japan
                "vi" -> R.drawable.ic_vietnam
                "th" -> R.drawable.ic_thailand
                else -> R.drawable.ic_korea // fallback
            }

            ivLanguage.load(flagRes) {
                transformations(
                    BorderTransformation(3f, ContextCompat.getColor(requireContext(), R.color.ic_language_stoke))
                )
            }

            showLanguage()
        }
    }

    private fun showLanguage(){
        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        when (prefs.getString("lang", "ko")) {
            "ko" -> binding.ivLanguage.setImageResource(R.drawable.ic_korea)
            "en-US", "en" -> binding.ivLanguage.setImageResource(R.drawable.ic_america)
            "zh" -> binding.ivLanguage.setImageResource(R.drawable.ic_china)
            "ja" -> binding.ivLanguage.setImageResource(R.drawable.ic_japan)
            "vi" -> binding.ivLanguage.setImageResource(R.drawable.ic_vietnam)
            "th" -> binding.ivLanguage.setImageResource(R.drawable.ic_thailand)
        }

        Log.d("LanguageDebug", "현재 언어: ${prefs.getString("lang", "ko")}")

        val adapter = LanguageAdapter { selectedLanguage ->
            applyLanguageChange(selectedLanguage.code)
            Timber.d("selected language: ${selectedLanguage.code}")
            binding.ivLanguage.load(selectedLanguage.iconRes) {
                transformations(
                    BorderTransformation(borderWidth = 3f, borderColor = resources.getColor(R.color.ic_language_stoke))
                )
            }
            binding.cvLanguageList.slideUp()
        }
        binding.rvLanguages.adapter = adapter

        adapter.getList(
            listOf(
                Language("ko", "Korean", R.drawable.ic_korea),
                Language("en", "English",R.drawable.ic_america),
                Language("zh", "Chinese",R.drawable.ic_china),
                Language("ja", "Japanese",R.drawable.ic_japan),
                Language("vi", "Vietnamese",R.drawable.ic_vietnam),
                Language("th", "Thai",R.drawable.ic_thailand)
            )
        )

        binding.ivLanguage.setOnClickListener {
            if (binding.cvLanguageList.visibility == View.VISIBLE) {
                binding.cvLanguageList.slideUp()
            } else {
                binding.cvLanguageList.slideDown()
            }
        }
    }

    private fun applyLanguageChange(languageCode: String) {
        //val newContext = requireContext().updateLocale(languageCode)
        val prefs = requireContext().getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit { putString("lang", languageCode) }
        requireActivity().recreate()
    }

    private fun clickPractice(token: String) {
        binding.ivAiPractice.setOnClickListener{
            Timber.d("click study")
            val intent= Intent(requireActivity(), AIPracticeActivity::class.java)
            intent.putExtra("accessToken", token)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    private fun clickGame(){
        binding.ivQuiz.setOnClickListener{
            val intent= Intent(requireActivity(), GameTabActivity::class.java)
            startActivity(intent)
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.stay)
        }
    }

    private fun View.slideDown(duration: Long = 200) {
        this.visibility = View.VISIBLE
        this.alpha = 0f
        this.translationY = -20f
        this.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(duration)
            .start()
    }

    private fun View.slideUp(duration: Long = 200) {
        this.animate()
            .translationY(-20f)
            .alpha(0f)
            .setDuration(duration)
            .withEndAction {
                this.visibility = View.GONE
            }
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    inner class BorderTransformation(
        private val borderWidth: Float = 6f,
        private val borderColor: Int = resources.getColor(R.color.ic_language_stoke)
    ) : Transformation() {

        override val cacheKey: String
            get() = "$borderWidth-$borderColor"

        override suspend fun transform(input: Bitmap, size: Size): Bitmap {
            val size = minOf(input.width, input.height)
            val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(output)

            // 원형 이미지 그리기
            val paint = Paint(Paint.ANTI_ALIAS_FLAG)
            val rect = RectF(0f, 0f, size.toFloat(), size.toFloat())
            canvas.drawOval(rect, paint)

            paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
            canvas.drawBitmap(input, null, rect, paint)

            // 테두리 그리기
            val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                color = borderColor
                strokeWidth = borderWidth
            }

            val halfStroke = borderWidth / 2
            val borderRect = RectF(halfStroke, halfStroke, size - halfStroke, size - halfStroke)
            canvas.drawOval(borderRect, borderPaint)

            return output
        }
    }

    override fun onTabReselected() {
        binding.nsvHome.smoothScrollTo(0,0)
    }
}