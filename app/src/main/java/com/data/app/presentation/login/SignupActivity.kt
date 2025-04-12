package com.data.app.presentation.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.data.app.databinding.ActivitySignupBinding

class SignupActivity:AppCompatActivity() {

    private lateinit var binding:ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}