package com.data.app.presentation.home

import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.Fragment
import coil3.Canvas
import coil3.load
import coil3.request.transformations
import coil3.size.Size
import coil3.transform.CircleCropTransformation
import coil3.transform.Transformation
import com.data.app.R
import com.data.app.data.Language
import com.data.app.databinding.FragmentHomeBinding

class HomeFragment:Fragment() {
    private var _binding:FragmentHomeBinding?=null
    private val binding:FragmentHomeBinding
        get() = requireNotNull(_binding){"home fragment is null"}

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
        inputMockData()
    }

    private fun inputMockData(){
        with(binding){
            tvTitleKor.text=getString(R.string.home_title_kor, "구구")
            tvTitleEng.text=getString(R.string.home_title_eng, "GooGoo")
            tvNotGiveUp.text=getString(R.string.home_not_give_up, "구구")
            tvContinueStudy.text=getString(R.string.home_continue_study, 13)
            ivLanguage.load(R.drawable.ic_korea) {
                transformations(
                    BorderTransformation(borderWidth = 3f, borderColor = resources.getColor(R.color.ic_language_stoke))
                )
            }

            showLanguage()
            /*ivLanguage.setOnClickListener{
                showLanguagePopup(it)
            }*/
        }
    }

    private fun showLanguage(){
        val adapter = LanguageAdapter { selectedLanguage ->
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
                Language("Korean", R.drawable.ic_korea),
                Language("English", R.drawable.ic_america),
                Language("Chinese", R.drawable.ic_china)
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

    private fun showLanguagePopup(anchor: View) {
        val popup = PopupMenu(requireContext(), anchor)
        popup.menuInflater.inflate(R.menu.menu_language, popup.menu)

        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_korea -> {
                    binding.ivLanguage.setImageResource(R.drawable.ic_korea)
                    true
                }
                R.id.menu_usa -> {
                    binding.ivLanguage.setImageResource(R.drawable.ic_america)
                    true
                }
                R.id.menu_china -> {
                    binding.ivLanguage.setImageResource(R.drawable.ic_china)
                    true
                }
                else -> false
            }
        }

        popup.show()
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

}