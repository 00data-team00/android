package com.data.app.presentation.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.data.app.R
import com.data.app.databinding.ActivityLandingBinding

class LandingActivity:AppCompatActivity() {

    private lateinit var binding:ActivityLandingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textData = binding.tvLanding.text.toString()
        val builder = SpannableStringBuilder(textData)
        builder.setSpan(StyleSpan(Typeface.BOLD), 6, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.BOLD), 22, textData.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLanding.text = builder

        /*
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }, 2000)
         */

    }

}