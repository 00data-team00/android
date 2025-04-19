package com.data.app.presentation.login

import android.R
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.data.app.databinding.ActivitySignupBinding
import androidx.core.view.isGone
import androidx.core.widget.addTextChangedListener
import com.data.app.presentation.MainActivity
import androidx.core.graphics.toColorInt

class SignupActivity:AppCompatActivity() {

    private lateinit var binding:ActivitySignupBinding
    private val viewModel: SignupViewmodel by viewModels()

    private var nameFilled = false
    private var pwFilled = false
    private var nationFilled = false
    private var verifyCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        val nationalityList = listOf("Korea", "Japan", "China", "Canada", "France", "United States")
        val adapter = ArrayAdapter(this, R.layout.simple_dropdown_item_1line, nationalityList)
        et_nation.setAdapter(adapter)

        val timer = object : CountDownTimer(5 * 60 * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 1000 / 60
                val seconds = (millisUntilFinished / 1000) % 60
                tv_timer.text = String.format("%01d:%02d", minutes, seconds)
            }
            override fun onFinish() {
                tv_timer.text = "0:00"
                if(btn_verified.isGone){
                    btn_send.visibility = View.VISIBLE
                }
            }
        }

        et_email.addTextChangedListener(object:TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
            override fun afterTextChanged(s: Editable?) {
                val email = s?.toString() ?: ""
                if(isValidEmail(email)){
                    tv_valid.visibility = View.GONE
                    btn_send.visibility = View.VISIBLE
                    viewModel.email = email
                }
                else if(email == "1"){
                    tv_valid.visibility = View.GONE
                    tv_already.visibility = View.VISIBLE
                }
                else{
                    tv_valid.visibility = View.VISIBLE
                    tv_already.visibility = View.GONE
                }
            }
        })

        btn_send.setOnClickListener{
            verifyCode = "1234"

            et_code.visibility = View.VISIBLE
            btn_veri.visibility = View.VISIBLE
            tv_timer.visibility = View.VISIBLE
            timer.start()

            btn_send.visibility = View.GONE
        }

        btn_veri.setOnClickListener{
            val inputCode = et_code.text.toString()
            if(inputCode == verifyCode){
                btn_veri.visibility = View.GONE
                binding.tvCodenotvalid.visibility = View.GONE
                btn_verified.visibility = View.VISIBLE
                tv_timer.visibility = View.GONE
                et_name.visibility = View.VISIBLE
                et_nation.visibility = View.VISIBLE
                et_pw.visibility = View.VISIBLE
                tv_pw.visibility = View.VISIBLE
                btn_privacy.visibility = View.VISIBLE
            }
            else{
                binding.tvCodenotvalid.visibility = View.VISIBLE
            }
        }

        et_name.addTextChangedListener{
            viewModel.username = it.toString()
            nameFilled = true
            updateSignupButtonVisibility()
        }
        et_pw.addTextChangedListener{
            if(isValidPW(it.toString())) {
                tv_pw.setTextColor("#c5c5c5".toColorInt())
                viewModel.password = it.toString()
                pwFilled = true
                updateSignupButtonVisibility()
            }
            else{
                tv_pw.setTextColor("#aa1100".toColorInt())
            }
        }
        et_nation.setOnClickListener{
            et_nation.showDropDown()
        }
        et_nation.setOnItemClickListener {parent, view, position, id ->
            val selected = parent.getItemAtPosition(position).toString()
            viewModel.nationality = selected
            nationFilled = true
            updateSignupButtonVisibility()
        }

        btn_privacy.setOnCheckedChangeListener { _, _ ->
            updateSignupButtonVisibility()
        }

        btn_signup.setOnClickListener{
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun isValidEmail(email:String):Boolean{
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
}