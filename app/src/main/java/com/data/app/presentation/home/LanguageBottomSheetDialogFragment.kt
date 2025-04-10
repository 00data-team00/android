package com.data.app.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

        val languageList = listOf(
            Language("Korean", R.drawable.ic_korea),
            Language("English", R.drawable.ic_america),
            Language("Chinese", R.drawable.ic_china)
        )
        adapter.getList(languageList)
    }

    override fun onDestroyView() {
        super.onDestroyView()

    }
}
