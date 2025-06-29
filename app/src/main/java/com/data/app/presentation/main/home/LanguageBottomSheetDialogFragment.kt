package com.data.app.presentation.main.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.layout.Layout
import com.data.app.R
import com.data.app.data.Language
import com.data.app.databinding.FragmentLanguageBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class LanguageBottomSheetDialogFragment(
    private val onLanguageSelected: (Language) -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: FragmentLanguageBottomSheetBinding?=null
    private val binding:FragmentLanguageBottomSheetBinding
        get() = requireNotNull(_binding){"laugnage bottom sheet dialog fragment is null"}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentLanguageBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setting()
    }

    private fun setting(){
        val adapter = LanguageAdapter {
            onLanguageSelected(it)
            dismiss()
        }
        binding.rvLanguages.adapter = adapter

        //한국어, 영어, 중국어, 일본어, 베트남어, 태국어
        val languageList = listOf(
            Language("ko", "Korean", R.drawable.ic_korea),
            Language("en", "English",R.drawable.ic_america),
            Language("zh", "Chinese",R.drawable.ic_china),
            Language("ja", "Japanese",R.drawable.ic_japan),
            Language("vi", "Vietnamese",R.drawable.ic_vietnam),
            Language("th", "Thai",R.drawable.ic_thailand)
        )
        adapter.getList(languageList)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
