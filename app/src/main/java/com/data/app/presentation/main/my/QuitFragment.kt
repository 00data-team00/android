package com.data.app.presentation.main.my

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import com.data.app.R
import com.data.app.databinding.FragmentMyBinding
import com.data.app.databinding.FragmentQuitBinding
import androidx.core.graphics.toColorInt
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.extension.my.QuitState
import com.data.app.presentation.login.LoginActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class QuitFragment:Fragment() {
    private var selectedButton: Button? = null

    private var _binding: FragmentQuitBinding? = null
    private val binding: FragmentQuitBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val quitViewModel: QuitViewModel by viewModels()

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentQuitBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        clickButtons()

        clickQuit()

        clickCancel()
        clickDecide()
    }

    private fun clickButtons() {
        val btn1 = binding.btnReason1
        val btn2 = binding.btnReason2
        val btn3 = binding.btnReason3
        val btn4 = binding.btnReason4
        val btn5 = binding.btnReason5

        val buttons = listOf(btn1, btn2, btn3, btn4, btn5)

        buttons.forEach { button ->
            button.setOnClickListener {
                // 직접입력 버튼을 한번 더 누른 경우
                if (selectedButton?.id == binding.btnReason5.id){
                    binding.etReason.visibility = View.GONE
                }
                // 다른 버튼을 한번 더 누른 경우
                if (selectedButton?.id == button.id){
                    button.isSelected = false
                    button.setTextColor("#BBBBBB".toColorInt())
                    binding.etReason.visibility = View.GONE
                    selectedButton = null
                }
                // 처음 누른 버튼이거나 아예 다른 버튼을 다시 누른 경우
                else {
                    selectedButton?.isSelected = false
                    selectedButton?.setTextColor("#BBBBBB".toColorInt())
                    button.isSelected = true
                    button.setTextColor("#000000".toColorInt())
                    selectedButton = button
                    if (button.id == binding.btnReason5.id) {
                        binding.etReason.visibility = View.VISIBLE
                    }
                }

                // 선택된 버튼이 있는 경우에만 다음 버튼 표시
                if (selectedButton != null) {
                    binding.btnQuit.visibility = View.VISIBLE
                }
                else {
                    binding.btnQuit.visibility = View.GONE
                }

            }
        }
    }

    private fun clickQuit() {
        binding.btnQuit.setOnClickListener {
            binding.clQuit.visibility = View.GONE
            binding.clQuitagain.visibility = View.VISIBLE
        }
    }

    private fun clickDecide() {
        lifecycleScope.launch {
            quitViewModel.quitState.collect { quitState ->
                when (quitState) {
                    is QuitState.Success -> {
                        // 로그인 정보 삭제
                        appPreferences.clearAccessToken()
                        // 로그인 액티비티로 전환하는 코드
                        val intent = Intent(requireContext(), LoginActivity::class.java)
                        startActivity(intent)
                        Toast.makeText(context, "회원탈퇴 되었습니다.", Toast.LENGTH_SHORT).show()
                    }
                    is QuitState.Loading -> {
                        Timber.d("quitState is loading")
                    }
                    is QuitState.Error -> {
                        Timber.d("quitState is error")
                    }
                }
            }
        }

        binding.btnDecide.setOnClickListener {
            // 서버로 사용자 정보 전송하는 코드
            quitViewModel.quit(appPreferences.getAccessToken()!!)
        }
    }

    private fun clickCancel() {
        binding.btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}