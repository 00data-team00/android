package com.data.app.presentation.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.data.app.databinding.ActivityLoginBinding
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.data.app.extension.login.LoginState
import com.data.app.extension.my.MyProfileState
import com.data.app.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textData = binding.tvRuready.text.toString()
        val builder1 = SpannableStringBuilder(textData)
        val start = textData.indexOf("Korean")
        val end = start + "Korean".length
        builder1.setSpan(
            ForegroundColorSpan("#A3D80D".toColorInt()),
            start,
            end,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
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
        builder2.setSpan(
            ForegroundColorSpan("#A3D80D".toColorInt()),
            start2,
            end2,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvJoinus.text = builder2
        binding.tvJoinus.highlightColor = android.graphics.Color.TRANSPARENT
        binding.tvJoinus.movementMethod = LinkMovementMethod.getInstance()

        setting()
    }

    private fun setting() {
        getLoginState()
        changeShowPassword()
        clickLoginButton()
    }

    private fun getLoginState() {
        lifecycleScope.launch {
            loginViewModel.loginState.collect { loginState ->
                when (loginState) {
                    is LoginState.Success -> {
                        saveInfo(loginState.accessToken)
                    }
                    is LoginState.Loading -> {}
                    is LoginState.Error -> {
                        val message = loginState.message

                        when {
                            message.contains("이메일") -> {
                                //binding.tvEmailError.text = message
                                binding.tvEmailError.visibility=View.VISIBLE
                                binding.tvPasswordError.visibility = View.GONE
                            }
                            message.contains("비밀번호") -> {
                                //binding.tvPasswordError.text = message
                                binding.tvEmailError.visibility=View.GONE
                                binding.tvPasswordError.visibility = View.VISIBLE
                            }
                            message.contains("No address") -> {
                                Toast.makeText(this@LoginActivity, "인터넷이 연결되어있지 않습니다.", Toast.LENGTH_SHORT).show()
                            }
                            else -> {
                                // 기타 에러 처리
                                binding.tvEmailError.visibility=View.GONE
                                binding.tvPasswordError.visibility = View.GONE
                            }
                        }
                    }
                    else->{}
                }
            }
        }
    }

    private fun saveInfo(accessToken: String) {
        lifecycleScope.launch {
            loginViewModel.myProfileState.collect { myProfileState ->
                when (myProfileState) {
                    is MyProfileState.Success -> {
                        startMain()
                    }
                    is MyProfileState.Loading -> {}
                    is MyProfileState.Error -> {
                        Timber.e(myProfileState.message)
                    }
                }
            }
        }

        loginViewModel.getProfile(accessToken)
    }
    private fun startMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    private fun changeShowPassword(){
        with(binding){
            btnPwShowable.setOnClickListener{
                btnPwShowable.isSelected=!btnPwShowable.isSelected

                if(btnPwShowable.isSelected){
                    etLoginPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                }else {
                    etLoginPassword.inputType =
                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                }

                // 커서를 항상 맨 뒤로 이동
                etLoginPassword.setSelection(etLoginPassword.text?.length ?: 0)
            }
        }
    }

    // login process to be implemented
    // 1. verifying ID & PW
    // 2. pass its ID & PW to next main activity
    private fun clickLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val pw = binding.etLoginPassword.text.toString()
            loginViewModel.login(email, pw)
        }
    }


}