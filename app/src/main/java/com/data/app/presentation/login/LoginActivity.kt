package com.data.app.presentation.login

import android.content.Intent
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import com.data.app.databinding.ActivityLoginBinding
import androidx.core.graphics.toColorInt
import com.data.app.presentation.MainActivity
import com.data.app.presentation.login.SignupActivity

class LoginActivity:AppCompatActivity() {

    private lateinit var binding:ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textData = binding.tvRuready.text.toString()
        val builder1 = SpannableStringBuilder(textData)
        val start = textData.indexOf("Korean")
        val end = start + "Korean".length
        builder1.setSpan(ForegroundColorSpan("#A3D80D".toColorInt()), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvRuready.text = builder1

        val textData2 = binding.tvJoinus.text.toString()
        val builder2 = SpannableStringBuilder(textData2)
        val start2 = textData2.indexOf("Join us")
        val end2 = start2 + "Join us".length

        // span value which makes some text can do some function (for here, activity switch)
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@LoginActivity, SignupActivity::class.java)
                startActivity(intent)
            }
        }

        builder2.setSpan(clickableSpan, start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder2.setSpan(ForegroundColorSpan("#A3D80D".toColorInt()), start2, end2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvJoinus.text = builder2
        binding.tvJoinus.highlightColor = android.graphics.Color.TRANSPARENT
        binding.tvJoinus.movementMethod = LinkMovementMethod.getInstance()

        setting()
    }

    private fun setting(){
        clickLoginButton()
    }

    // login process to be implemented
    // 1. verifying ID & PW
    // 2. pass its ID & PW to next main activity
    private fun clickLoginButton(){
        binding.btnLogin.setOnClickListener{
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }


}