package com.data.app.presentation.home.ai_practice

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.data.app.R
import com.data.app.databinding.FragmentAiPracticeBinding
import com.google.android.material.tabs.TabLayout

class AIPracticeFragment:Fragment() {
    private var _binding:FragmentAiPracticeBinding?=null
    private val binding:FragmentAiPracticeBinding
        get()= requireNotNull(_binding){"AI Practice Fragment is null"}

    private lateinit var aiAdaper: AIPracticeAdapter
    private val aiPracticeViewModel: AIPracticeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding= FragmentAiPracticeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        showList()
        clickPracticeRecord()
        clickBackButton()
    }

    private fun showList(){
        aiAdaper= AIPracticeAdapter()
        binding.rvAiPractice.adapter=aiAdaper
        aiAdaper.getList(aiPracticeViewModel.mockDailyList)

        setupTabs()
       /* binding.tlAiPractice.apply {
            addTab(newTab().setText(getString(R.string.ai_practice_daily)))
            addTab(newTab().setText(getString(R.string.ai_practice_culture)))
            addTab(newTab().setText(getString(R.string.ai_practice_job)))
        }

        binding.tlAiPractice.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                moveIndicatorTo(tab)
                aiAdaper.getList(aiPracticeViewModel.mockList)
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        binding.tlAiPractice.post {
            val selectedTab = binding.tlAiPractice.getTabAt(binding.tlAiPractice.selectedTabPosition)
            selectedTab?.let { moveIndicatorTo(it) }
        }*/
    }

    private fun setupTabs(){
        val tabTitles = listOf(getString(R.string.ai_practice_daily),getString(R.string.ai_practice_culture), getString(R.string.ai_practice_job) )

        binding.tlAiPractice.apply {
            addTab(newTab().setText(getString(R.string.ai_practice_daily)))
            addTab(newTab().setText(getString(R.string.ai_practice_culture)))
            addTab(newTab().setText(getString(R.string.ai_practice_job)))
        }

        applyTabMargins()

        binding.tlAiPractice.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> aiAdaper.getList(aiPracticeViewModel.mockDailyList)
                    1 -> aiAdaper.getList(aiPracticeViewModel.mockCultureList)
                    2 -> aiAdaper.getList(aiPracticeViewModel.mockJobList)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })

        /* tabTitles.forEachIndexed { index, title ->
             val tab = binding.tlAiPractice.newTab()
             val tabBinding = ItemTabAiBinding.inflate(layoutInflater)
             tabBinding.tvTabTitle.text = title
             tab.customView = tabBinding.root
             binding.tlAiPractice.addTab(tab)

             // 기본 첫 번째 탭 선택 효과
             if (index == 0) {
                 tabBinding.tvTabTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                 tabBinding.tvTabTitle.setBackgroundResource(R.drawable.indicator_white) // 선택된 탭 배경
             }
         }

         binding.tlAiPractice.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
             override fun onTabSelected(tab: TabLayout.Tab?) {
                 val customView = tab?.customView ?: return
                 val tabBinding = ItemTabAiBinding.bind(customView)
                 tabBinding.tvTabTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
                 tabBinding.tvTabTitle.setBackgroundResource(R.drawable.indicator_white)

                 // 탭 위치에 따라 데이터 변경
                 when (tab.position) {
                     0 -> aiAdaper.getList(aiPracticeViewModel.mockDailyList)
                     1 -> aiAdaper.getList(aiPracticeViewModel.mockCultureList)
                     2 -> aiAdaper.getList(aiPracticeViewModel.mockJobList)
                 }
             }

             override fun onTabUnselected(tab: TabLayout.Tab?) {
                 val customView = tab?.customView ?: return
                 val tabBinding = ItemTabAiBinding.bind(customView)
                 tabBinding.tvTabTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                 tabBinding.tvTabTitle.setBackgroundResource(android.R.color.transparent)
             }

             override fun onTabReselected(tab: TabLayout.Tab?) {}
         })*/
      /*  for (title in tabTitles) {
            val tab = binding.tlAiPractice.newTab()

            val tabBinding = ItemTabAiBinding.inflate(layoutInflater)
            tabBinding.tvTabTitle.text = title

            tab.customView = tabBinding.root
            binding.tlAiPractice.addTab(tab)
        }

        setupTabListeners()*/
    }

    private fun applyTabMargins() {
        binding.tlAiPractice.post {
            val tabLayout = binding.tlAiPractice.getChildAt(0) as? ViewGroup ?: return@post
            for (i in 0 until tabLayout.childCount) {
                val tab = tabLayout.getChildAt(i)
                val layoutParams = tab.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.marginStart = 6.dpToPx()
                layoutParams.marginEnd = 8.dpToPx()
                tab.layoutParams = layoutParams
                tab.requestLayout()
            }
        }
    }

    private fun Int.dpToPx(): Int {
        return (this * Resources.getSystem().displayMetrics.density).toInt()
    }

    private fun clickPracticeRecord(){
        binding.btnShowPreviousPractice.setOnClickListener{
            findNavController().navigate(R.id.action_aiPractice_to_practiceRecords)
        }
    }

    private fun clickBackButton(){
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}