package com.data.app.presentation.main.explore

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.NestedScrollView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.data.app.R
import com.data.app.data.response_dto.explore.ResponseAllProgramDto
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousRecordsDto
import com.data.app.databinding.DialogExpiredBinding
import com.data.app.databinding.FragmentExploreBinding
import com.data.app.extension.explore.AllProgramsState
import com.data.app.extension.explore.DeadLineProgramState
import com.data.app.presentation.main.OnTabReselectedListener
import com.data.app.presentation.main.home.ai_practice.previous_practice.PreviousPracticeAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.exp

@AndroidEntryPoint
class ExploreFragment : Fragment(), OnTabReselectedListener {
    private var _binding: FragmentExploreBinding? = null
    private val binding: FragmentExploreBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val exploreViewModel: ExploreViewModel by viewModels()
    private lateinit var exploreProgramAdapter: ExploreProgramAdapter

    private var searchTextWatcher: TextWatcher? = null

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
        searchList_()
        getDeadLineList()
        getProgramList()
        switchPrograms()
        getText()
    }

    private fun getDeadLineList() {
        val deadlineAdapter = ExploreDeadLineAdapter(onClick = { appLink ->
            if (appLink.isNullOrEmpty() || !appLink.startsWith("http")) {
                val dialogBinding = DialogExpiredBinding.inflate(LayoutInflater.from(requireContext()))

                val alertDialog = AlertDialog.Builder(requireContext())
                    .setView(dialogBinding.root)
                    .create()

                dialogBinding.btnConfirm.setOnClickListener {
                    alertDialog.dismiss()
                }

                alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog.show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appLink))
                startActivity(intent)
            }
        })
        binding.rvDeadline.adapter = deadlineAdapter

        lifecycleScope.launch {
            exploreViewModel.deadLineProgramState.collect { deadLineProgramState ->
                when (deadLineProgramState) {
                    is DeadLineProgramState.Success -> {
                        Timber.d("deadlineprogram: ${deadLineProgramState.response.eduPrograms}")
                        deadlineAdapter.getList(deadLineProgramState.response.eduPrograms)
                    }

                    is DeadLineProgramState.Loading -> {}
                    is DeadLineProgramState.Error -> {
                        Timber.e("get deadline list error")
                    }
                }
            }
        }

        exploreViewModel.getDeadLinePrograms()
    }

    private fun getProgramList() {
        exploreProgramAdapter = ExploreProgramAdapter(clickProgram = { appLink ->
            if (appLink.isNullOrEmpty() || !appLink.startsWith("http")) {
                val dialogBinding = DialogExpiredBinding.inflate(LayoutInflater.from(requireContext()))

                val alertDialog = AlertDialog.Builder(requireContext())
                    .setView(dialogBinding.root)
                    .create()

                dialogBinding.btnConfirm.setOnClickListener {
                    alertDialog.dismiss()
                }

                alertDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog.show()
            } else {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appLink))
                startActivity(intent)
            }
        })
        binding.rvAllProgram.adapter = exploreProgramAdapter

        lifecycleScope.launch {
            exploreViewModel.allProgramsState.collect { allProgramsState ->
                when (allProgramsState) {
                    is AllProgramsState.Success -> {
//                        searchList(allProgramsState.response.content)
                        if (allProgramsState.isAppend)
                            exploreProgramAdapter.addPrograms(allProgramsState.response.content)
                        else exploreProgramAdapter.replacePrograms(allProgramsState.response.content)
                        reflectFreePaid(allProgramsState.response.content)
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
            exploreViewModel.getAllPrograms(true, 10)

            btnFree.setOnClickListener {
                btnFree.isSelected = true
                btnFree.setTextColor(getResources().getColor(R.color.white))
                btnPaid.isSelected = false
                btnPaid.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))

                exploreViewModel.resetPage()
                if (binding.etSearch.text.isEmpty()){
                    exploreViewModel.getAllPrograms(true, 10)
                } else {exploreViewModel.getAllPrograms(true, 100)}


//                if(!etSearch.text.isEmpty()){
//                    exploreViewModel.resetPage()
//                    exploreViewModel.getAllPrograms(true, 10)
//                    val currentState = exploreViewModel.allProgramsState.value
//                    var curList: List<ResponseAllProgramDto.ProgramDto> = emptyList()
//
//                    if (currentState is AllProgramsState.Success) {
//                        val response = currentState.response
//                        curList = response.content
//                        Timber.d(curList.toString())
//                    }
//
//                    val keyword = etSearch.text
//                    val filteredList = curList.filter {
//                        it.titleNm.contains(keyword, ignoreCase = true)
//                    }
//                    Timber.d(filteredList.toString())
//                    exploreProgramAdapter.updateList(filteredList)
//                }
//                else{
//                    exploreViewModel.resetPage()
//                    exploreViewModel.getAllPrograms(true, 10)
//                }

            }
            btnPaid.setOnClickListener {
                btnFree.isSelected = false
                btnFree.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))
                btnPaid.isSelected = true
                btnPaid.setTextColor(getResources().getColor(R.color.white))

                exploreViewModel.resetPage()
                if (binding.etSearch.text.isEmpty()){
                    exploreViewModel.getAllPrograms(false, 10)
                } else {exploreViewModel.getAllPrograms(false, 100)}

//               if(!etSearch.text.isEmpty()){
//                    exploreViewModel.resetPage()
//                    exploreViewModel.getAllPrograms(false, 10)
//                    val currentState = exploreViewModel.allProgramsState.value
//                    var curList: List<ResponseAllProgramDto.ProgramDto> = emptyList()
//
//                    if (currentState is AllProgramsState.Success) {
//                        val response = currentState.response
//                        curList = response.content
//                        Log.d("HIHIHIHI", curList.toString())
//                    }
//
//                    val keyword = etSearch.text
//                    val filteredList = curList.filter {
//                        it.titleNm.contains(keyword, ignoreCase = true)
//                    }
//                    Log.d("HIHIHIHI", filteredList.toString())
//                    exploreProgramAdapter.updateList(filteredList)
//               }else{
//                    exploreViewModel.resetPage()
//                    exploreViewModel.getAllPrograms(false, 10)
//               }
            }

            nsvExplore.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
                val view = v.getChildAt(v.childCount - 1)

                if (scrollY >= (view.measuredHeight - v.measuredHeight)) {
                    // 스크롤이 맨 아래 도달
                    if (binding.etSearch.text.isEmpty()){
                        exploreViewModel.getAllPrograms(binding.btnFree.isSelected, 10)
                    }
                }
            }
        }
    }

   /* private fun switchPrograms() {
        with(binding) {
            btnFree.isSelected = true
            btnFree.setTextColor(getResources().getColor(R.color.white))
            exploreViewModel.getAllPrograms(true, 10)

            btnFree.setOnClickListener {
                btnFree.isSelected = true
                btnFree.setTextColor(getResources().getColor(R.color.white))
                btnPaid.isSelected = false
                btnPaid.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))

                if(!etSearch.text.isEmpty()){
                    val currentState = exploreViewModel.allProgramsState.value
                    var curList: List<ResponseAllProgramDto.ProgramDto> = emptyList()

                    if (currentState is AllProgramsState.Success) {
                        val response = currentState.response
                        curList = response.content
                    }

                    val keyword = etSearch.text
                    val filteredList = curList.filter {
                        it.titleNm.contains(keyword, ignoreCase = true)
                    }
                    exploreProgramAdapter.updateList(filteredList)
                }
                else{
                    exploreViewModel.resetPage()
                    exploreViewModel.getAllPrograms(true, 10)
                }

            }
            btnPaid.setOnClickListener {
                btnFree.isSelected = false
                btnFree.setTextColor(getResources().getColor(R.color.mock_ai_practice_title_gray))
                btnPaid.isSelected = true
                btnPaid.setTextColor(getResources().getColor(R.color.white))

                if(!etSearch.text.isEmpty()){
                    val currentState = exploreViewModel.allProgramsState.value
                    var curList: List<ResponseAllProgramDto.ProgramDto> = emptyList()

                    if (currentState is AllProgramsState.Success) {
                        val response = currentState.response
                        curList = response.content
                    }

                    val keyword = etSearch.text
                    val filteredList = curList.filter {
                        it.titleNm.contains(keyword, ignoreCase = true)
                    }
                    exploreProgramAdapter.updateList(filteredList)
                }else{
                    exploreViewModel.resetPage()
                    exploreViewModel.getAllPrograms(false, 10)
                }
            }

            nsvExplore.setOnScrollChangeListener { v: NestedScrollView, _, scrollY, _, oldScrollY ->
                val view = v.getChildAt(v.childCount - 1)

                if (scrollY >= (view.measuredHeight - v.measuredHeight)) {
                    // 스크롤이 맨 아래 도달
                    exploreViewModel.getAllPrograms(binding.btnFree.isSelected, 10)
                }
            }
        }
    }*/

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

    private fun reflectFreePaid(followlist: List<ResponseAllProgramDto.ProgramDto>){
        val keyword = binding.etSearch.text.toString().trim()
        val filteredList = followlist.filter {
            it.titleNm.contains(keyword, ignoreCase = true)
        }

        if (keyword.isNotEmpty()) {
            exploreProgramAdapter.updateList(filteredList)
        }
    }

    private fun searchList_(){
        binding.etSearch.doOnTextChanged{ text, _, _, _ ->
            val keyword = text.toString().trim()
            if (keyword.isEmpty()) {
                exploreViewModel.resetPage()
                exploreViewModel.getAllPrograms(binding.btnFree.isSelected, 10)
            } else {
                exploreViewModel.resetPage()
                exploreViewModel.getAllPrograms(binding.btnFree.isSelected, 100)
            }
        }
    }

//    private fun searchList(followlist: List<ResponseAllProgramDto.ProgramDto>){
//        searchTextWatcher?.let { binding.etSearch.removeTextChangedListener(it) }
//
//       binding.etSearch.doOnTextChanged{ text, _, _, _ ->
//           val keyword_ = text.toString().trim()
//           val filteredList_ = followlist.filter {
//               it.titleNm.contains(keyword_, ignoreCase = true)
//           }
//
//            Timber.d("keyword: $keyword_")
//
//            if (keyword_.isEmpty()) {
//                exploreProgramAdapter.updateList(followlist)
//            } else {
//                exploreProgramAdapter.updateList(filteredList_)
//            }
//        }
//    }

    private fun getText(){
       binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!s.isNullOrBlank()) {
                    binding.rvDeadline.visibility = View.GONE
                } else {
                    binding.rvDeadline.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onTabReselected() {
        binding.nsvExplore.smoothScrollTo(0, 0)
        binding.etSearch.text = null

        exploreViewModel.resetPaging()

        // 데이터 새로 fetch
        exploreViewModel.getAllPrograms(isFree = binding.btnFree.isSelected, 10)  // 혹은 현재 선택된 필터값
        exploreViewModel.getDeadLinePrograms()
    }
}