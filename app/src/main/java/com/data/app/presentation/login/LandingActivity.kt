package com.data.app.presentation.login

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.StyleSpan
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.data.app.data.response_dto.ResponseLoginDto
import com.data.app.databinding.ActivityLandingBinding
import com.data.app.extension.LoginState
import com.data.app.presentation.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class LandingActivity:AppCompatActivity() {

    private lateinit var binding:ActivityLandingBinding
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val textData = binding.tvLanding.text.toString()
        val builder = SpannableStringBuilder(textData)
        builder.setSpan(StyleSpan(Typeface.BOLD), 6, 13, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(StyleSpan(Typeface.BOLD), 22, textData.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvLanding.text = builder


        observeLoginStateAndNavigate()
        /*Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
        }, 2000)*/


    }

    private fun observeLoginStateAndNavigate() {
        val minSplashTimeMillis = 2000L // 최소 보여주기 시간 (2초)
        val startTime = System.currentTimeMillis()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                loginViewModel.loginState.collect { state ->
                    Timber.d("LandingActivity: LoginState changed to $state")
                    when (state) {
                        is LoginState.Success -> {
                            Timber.d("token: ${state.response.accessToken}")
                            val elapsedTime = System.currentTimeMillis() - startTime
                            val remainingTime = minSplashTimeMillis - elapsedTime
                            if (remainingTime > 0) {
                                kotlinx.coroutines.delay(remainingTime)
                            }
                            Timber.i("LandingActivity: Saved login found. Navigating to MainActivity.")
                            navigateToMain(state.response)
                        }
                        is LoginState.Error -> {
                            val elapsedTime = System.currentTimeMillis() - startTime
                            val remainingTime = minSplashTimeMillis - elapsedTime
                            if (remainingTime > 0) {
                                kotlinx.coroutines.delay(remainingTime)
                            }
                            Timber.w("LandingActivity: No saved login or error: ${state.message}. Navigating to LoginActivity.")
                            navigateToLogin()
                        }
                        is LoginState.Loading -> {
                            Timber.d("LandingActivity: Checking saved login state...")
                        }
                        is LoginState.Idle -> {
                            Timber.d("LandingActivity: LoginState is Idle. Checking for saved login.")
                            /*loginViewModel.checkForSavedLogin()*/
                        }
                    }
                }
            }
        }

        loginViewModel.checkForSavedLogin()
    }

    private fun navigateToMain(response: ResponseLoginDto) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("accessToken", response.accessToken)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish() // LandingActivity 종료
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // 선택 사항: LoginActivity에서 뒤로가기 시 앱 종료 원하면 추가
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        finish() // LandingActivity 종료
    }

}