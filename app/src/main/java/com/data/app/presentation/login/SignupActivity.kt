package com.data.app.presentation.login

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.data.app.databinding.ActivitySignupBinding
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import com.data.app.presentation.main.MainActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.data.app.extension.login.NationState
import com.data.app.extension.login.RegisterState
import com.data.app.extension.login.SendMailState
import com.data.app.extension.login.VerifyMailState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.sign

@AndroidEntryPoint
class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val signUpViewModel: SignupViewmodel by viewModels()

    private var nameFilled = false
    private var pwFilled = false
    private var nationFilled = false
    private var verifyCode: String = ""

    private lateinit var nationMap: Map<String, Int>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showValid()
        showElse()
        register()

        clickBackButton()

        val et_email = binding.etSignupEmail
        val tv_valid = binding.tvWritevalid
        val tv_already = binding.tvAlreadyuse
        val btn_send = binding.btnSend
        val et_code = binding.etVerifycode
        val btn_veri = binding.btnVerify
        val tv_timer = binding.tvTimer
        val btn_verified = binding.btnVerified
        val et_name = binding.etSignupName
        val et_nation = binding.etNationality
        val et_pw = binding.etSignupPassword
        val tv_pw = binding.tvPwcondition
        val btn_privacy = binding.btnPrivacy
        val btn_signup = binding.btnSignup

        et_email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString() ?: ""
                if (isValidEmail(email)) {
                    tv_valid.visibility = View.GONE
                    btn_send.visibility = View.VISIBLE

                    btn_send.setOnClickListener {
                        // verifyCode = "1234"
                        signUpViewModel.sendMail(email)
                    }

                    btn_veri.setOnClickListener {
                        Timber.d("code: ${et_code.text}")
                        val inputCode = et_code.text.toString()
                        signUpViewModel.verifyMail(inputCode)
                    }
                } else if (email == "1") {
                    tv_valid.visibility = View.GONE
                    tv_already.visibility = View.VISIBLE
                } else {
                    tv_valid.visibility = View.VISIBLE
                    tv_already.visibility = View.GONE
                }
            }
        })

        et_name.addTextChangedListener {
            signUpViewModel.username = it.toString()
            nameFilled = true
            updateSignupButtonVisibility()
        }
        et_pw.addTextChangedListener {
            if (isValidPW(it.toString())) {
                tv_pw.setTextColor("#c5c5c5".toColorInt())
                signUpViewModel.password = it.toString()
                pwFilled = true
                updateSignupButtonVisibility()
            } else {
                tv_pw.setTextColor("#aa1100".toColorInt())
            }
        }
        // 키보드 입력 막음
        et_nation.inputType= InputType.TYPE_NULL
        et_nation.keyListener=null

        et_nation.setOnClickListener {
            et_nation.showDropDown()
        }
        et_nation.setOnItemClickListener { parent, view, position, id ->
            val selectedNameKo = parent.getItemAtPosition(position).toString()
            val selectedCode = nationMap[selectedNameKo] ?: return@setOnItemClickListener
            signUpViewModel.nationality = selectedCode // 이제 code 저장
            nationFilled = true
            updateSignupButtonVisibility()
        }

        btn_privacy.setOnCheckedChangeListener { _, _ ->
            updateSignupButtonVisibility()
        }

        btn_signup.setOnClickListener {
            val name = et_name.text.toString()
            val pw = et_pw.text.toString()
            val nationId = signUpViewModel.nationality // 이미 setOnItemClickListener에서 선택된 코드

            if (nationId==0) {
                // 예외 처리: 나라 선택 안 한 경우
                Toast.makeText(this, "국가를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            signUpViewModel.register(name, pw, nationId)
        }

        getNation()
    }

    private fun getNation() {
        lifecycleScope.launch {
            signUpViewModel.nationState.collect { nationState ->
                when (nationState) {
                    is NationState.Success -> {
                        Timber.d("get nation state success!")

                        // ① nameKo -> code 맵으로 변환
                        nationMap = nationState.response.nations.associate { it.nameKo to it.id }

                        // ② UI에는 nameKo만 표시
                        val nationalityList = nationMap.keys.toList()
                        val adapter = ArrayAdapter(this@SignupActivity, android.R.layout.simple_dropdown_item_1line, nationalityList)
                        binding.etNationality.setAdapter(adapter)
                    }

                    is NationState.Loading -> {}
                    is NationState.Error -> {
                        Timber.e("get nation state error!")
                    }
                }
            }
        }

        signUpViewModel.getNation()
    }

    private fun showValid() {
        lifecycleScope.launch {
            signUpViewModel.sendMailState.collect { sendMailState ->
                when (sendMailState) {
                    is SendMailState.Success -> {
                        val timer = object : CountDownTimer(5 * 60 * 1000L, 1000L) {
                            override fun onTick(millisUntilFinished: Long) {
                                val minutes = millisUntilFinished / 1000 / 60
                                val seconds = (millisUntilFinished / 1000) % 60
                                binding.tvTimer.text = String.format("%01d:%02d", minutes, seconds)
                            }

                            override fun onFinish() {
                                binding.tvTimer.text = "0:00"
                                if (binding.btnVerified.isGone) {
                                    binding.btnSend.visibility = View.VISIBLE
                                }
                            }
                        }

                        with(binding) {
                            etVerifycode.visibility = View.VISIBLE
                            btnVerify.visibility = View.VISIBLE
                            tvTimer.visibility = View.VISIBLE
                            timer.start()

                            btnSend.visibility = View.GONE
                            inputVerifyCode()
                        }
                    }

                    is SendMailState.Loading -> {}
                    is SendMailState.Error -> {
                        Timber.e("get send mail state error!")
                    }
                }
            }
        }
    }

    private fun inputVerifyCode(){
        with(binding){
            etVerifycode.addTextChangedListener(object : TextWatcher {
                private var prevText = ""

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    val newText = s.toString()

                    // 숫자 또는 영어만 필터링하고, 영어는 대문자로 변환
                    val filtered = newText.filter {
                        val isDigit = it.isDigit()
                        val isEnglish = it in 'A'..'Z' || it in 'a'..'z'
                        val isNotKorean = it !in '\uAC00'..'\uD7A3'
                        (isDigit || isEnglish) && isNotKorean
                    }.uppercase()

                    if (filtered != newText) {
                        etVerifycode.setText(filtered)
                        etVerifycode.setSelection(filtered.length)
                    }
                }
            })
        }
    }

    private fun showElse() {
        lifecycleScope.launch {
            signUpViewModel.verifyMailState.collect { verifyMailState ->
                when (verifyMailState) {
                    is VerifyMailState.Success -> {
                        with(binding) {
                            btnVerify.visibility = View.GONE
                            tvCodenotvalid.visibility = View.GONE
                            btnVerified.visibility = View.VISIBLE
                            tvTimer.visibility = View.GONE
                            etSignupName.visibility = View.VISIBLE
                            etNationality.visibility = View.VISIBLE
                            etSignupPassword.visibility = View.VISIBLE
                            tvPwcondition.visibility = View.VISIBLE
                            btnPrivacy.visibility = View.VISIBLE
                        }
                    }

                    is VerifyMailState.Loading -> {}
                    is VerifyMailState.Error -> {
                        binding.tvCodenotvalid.visibility = View.VISIBLE
                        Timber.e("get verify mail state error!")
                    }
                }
            }
        }
    }

    private fun register() {
        val intent = Intent(this, LoginActivity::class.java)
        lifecycleScope.launch {
            signUpViewModel.registerState.collect { registerState ->
                when (registerState) {
                    is RegisterState.Success -> {
                        startActivity(intent)
                    }

                    is RegisterState.Loading -> {}
                    is RegisterState.Error -> {
                        Timber.e("get register state error!")
                    }
                }
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidPW(password: String): Boolean {
        val regex = Regex("^(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+=\\-{}|:;\"'<>,.?/]).{8,}$")
        return regex.matches(password)
    }

    private fun updateSignupButtonVisibility() {
        binding.btnSignup.visibility =
            if (nameFilled && pwFilled && nationFilled && binding.btnPrivacy.isChecked)
                View.VISIBLE else View.GONE
    }

    private fun clickBackButton() {
        binding.btnSignupBack.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}