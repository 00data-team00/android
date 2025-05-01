package com.data.app.presentation.main.explore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.data.app.R
import com.data.app.databinding.FragmentExploreBinding
import com.data.app.extension.AllProgramsState
import com.data.app.extension.DeadLineProgramState
import com.data.app.presentation.main.OnTabReselectedListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.exp

@AndroidEntryPoint
class ExploreFragment : Fragment(), OnTabReselectedListener {
    private var _binding: FragmentExploreBinding? = null
    private val binding: FragmentExploreBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val exploreViewModel: ExploreViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        getDeadLineList()
        getProgramList()
        switchPrograms()
    }

    private fun getDeadLineList() {
        val deadlineAdapter = ExploreDeadLineAdapter()
        binding.rvDeadline.adapter = deadlineAdapter

        lifecycleScope.launch {
            exploreViewModel.deadLineProgramState.collect{deadLineProgramState ->
                when(deadLineProgramState){
                    is DeadLineProgramState.Success->{
                        Timber.d("deadlineprogram: ${deadLineProgramState.response.eduPrograms}")
                        deadlineAdapter.getList(deadLineProgramState.response.eduPrograms)
                    }
                    is DeadLineProgramState.Loading->{}
                    is DeadLineProgramState.Error->{
                        Timber.e("get deadline list error")
                    }
                }
            }
        }

        exploreViewModel.getDeadLinePrograms()
    }

    private fun getProgramList() {
        val programAdapter = ExploreProgramAdapter(clickProgram = {
            val action = ExploreFragmentDirections.actionExploreFragmentToProgramFragment()
            findNavController().navigate(action)
        })
        binding.rvAllProgram.adapter = programAdapter

        lifecycleScope.launch {
            exploreViewModel.allProgramsState.collect { allProgramsState ->
                when (allProgramsState) {
                    is AllProgramsState.Success -> {
                        if (allProgramsState.isAppend)
                            programAdapter.addPrograms(allProgramsState.response.content)
                        else programAdapter.replacePrograms(allProgramsState.response.content)
                    }

                    is AllProgramsState.Loading -> {}
                    is AllProgramsState.Error -> {
                        Timber.e("explore all programs state error!")
                    }
                }

            }
        }
    }

    private fun switchPrograms() {
        with(binding) {
            btnFree.isSelected = true
            btnFree.setTextColor(getResources().getColor(R.color.white))
            exploreViewModel.getAllPrograms(true)

            btnFree.setOnClickListener {
                btnFree.isSelected = true
                btnFree.setTextColor(getResources().getColor(R.color.white))
                btnPaid.isSelected = false
                btnPaid.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))

                exploreViewModel.resetPage()
                exploreViewModel.getAllPrograms(true)
            }
            btnPaid.setOnClickListener {
                btnFree.isSelected = false
                btnFree.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))
                btnPaid.isSelected = true
                btnPaid.setTextColor(getResources().getColor(R.color.white))

                exploreViewModel.resetPage()
                exploreViewModel.getAllPrograms(false)
            }

            nsvExplore.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
                val view = v.getChildAt(v.childCount - 1)

                if (scrollY >= (view.measuredHeight - v.measuredHeight)) {
                    // 스크롤이 맨 아래 도달
                    exploreViewModel.getAllPrograms(binding.btnFree.isSelected)
                }
            }
        }
    }

    /*private fun clickPriceButton() {
        val programAdapter = ExploreProgramAdapter(clickProgram = {
            val action = ExploreFragmentDirections.actionExploreFragmentToProgramFragment()
            findNavController().navigate(action)
        })
        binding.rvAllProgram.adapter = programAdapter
        programAdapter.getList(exploreViewModel.freeProgramList)
        with(binding) {
            btnFree.isSelected = true
            btnFree.setTextColor(getResources().getColor(R.color.white))

            btnFree.setOnClickListener {
                btnFree.isSelected = true
                btnFree.setTextColor(getResources().getColor(R.color.white))
                btnPaid.isSelected = false
                btnPaid.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))
                programAdapter.getList(exploreViewModel.freeProgramList)
            }
            btnPaid.setOnClickListener {
                btnFree.isSelected = false
                btnFree.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))
                btnPaid.isSelected = true
                btnPaid.setTextColor(getResources().getColor(R.color.white))
                programAdapter.getList(exploreViewModel.paidProgramList)
            }
        }
    }*/

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTabReselected() {
        binding.nsvExplore.smoothScrollTo(0, 0)
    }
}