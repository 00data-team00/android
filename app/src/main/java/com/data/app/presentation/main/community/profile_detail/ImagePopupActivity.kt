package com.data.app.presentation.main.community.profile_detail

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil3.load
import com.data.app.R
import com.data.app.databinding.ActivityImagePopupBinding

class ImagePopupActivity() : AppCompatActivity() {
    private lateinit var binding: ActivityImagePopupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initBinds()
        setting()
    }

    private fun initBinds() {
        binding = ActivityImagePopupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun setting() {
        window.setBackgroundDrawable(ColorDrawable(Color.WHITE))

        showImage()
    }

    private fun showImage() {
        val images = intent.getIntegerArrayListExtra("imageList") ?: arrayListOf(R.drawable.bg_post_gray)
        val startIndex = intent.getIntExtra("startIndex", 0).coerceIn(0, images.lastIndex)

        val adapter = object : RecyclerView.Adapter<ImageViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
                val iv = ImageView(parent.context).apply {
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    scaleType = ImageView.ScaleType.FIT_CENTER
                }
                return ImageViewHolder(iv)
            }

            override fun getItemCount() = images.size

            override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
                (holder.itemView as ImageView).load(images[position])
            }
        }

        binding.vpImages.adapter = adapter
        binding.vpImages.setCurrentItem(startIndex, false)

        updatePageText(startIndex, images.size)

        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updatePageText(position, images.size)
            }
        })

        binding.btnClose.setOnClickListener {
            finish()
            overridePendingTransition(0, 0)
        }

        onBackPressedDispatcher.addCallback(this) {
            finish()
            overridePendingTransition(0, 0)
        }
    }

    private fun updatePageText(current: Int, total: Int) {
        binding.tvCount.text = "${current + 1} / $total"
    }

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view)
}
