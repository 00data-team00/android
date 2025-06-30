package com.data.app.presentation.main.my

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.data.app.R
import com.data.app.databinding.FragmentMyBinding
import com.data.app.extension.MyState
import com.data.app.presentation.login.LoginActivity
import kotlinx.coroutines.launch
import timber.log.Timber

class MyFragment:Fragment() {
    private var _binding: FragmentMyBinding? = null
    private val binding: FragmentMyBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val myViewModel: MyViewModel by viewModels()
    private lateinit var myAdapter: _root_ide_package_.com.data.app.presentation.main.my.MyAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting(){
        showPosts()
        makeList()
        clickFollow()
        clickQuit()
    }

    private fun showPosts(){
        lifecycleScope.launch {
            myViewModel.myState.collect{myState->
                when(myState){
                    is MyState.Success->{
                        Timber.d("myState is success")
                        showProfile(myState.response[0].profile, myState.response[0].id)
                        myAdapter=
                            _root_ide_package_.com.data.app.presentation.main.my.MyAdapter(clickPost = { post ->
                                val action =
                                    MyFragmentDirections.actionMyFragmentToMyPostDetailFragment(post)
                                findNavController().navigate(action)
                            })
                        binding.rvPosts.adapter=myAdapter
                        myAdapter.getList(myState.response)
                    }
                    is MyState.Loading->{
                        Timber.d("myState is loading")
                    }
                    is MyState.Error ->{
                        Timber.d("myState is error")
                    }
                }
            }
        }

        binding.tvId.text="kkuming"
    }

    private fun showProfile(profile: Int, name: String) {
        with(binding){
            ivProfile.load(profile){
                transformations(CircleCropTransformation())
            }
            tvName.text=name
            tvCountry.text="한국"
            tvPostCount.text="4"
            tvFollowerCount.text="60"
            tvFollowingCount.text="60"
        }
    }

    private fun makeList() {
       myViewModel.getPosts()
    }

    private fun clickQuit(){
        binding.btnMenu.setOnClickListener {
            val dropdownInflater = LayoutInflater.from(context)
            val popupView = dropdownInflater.inflate(R.layout.dropdown_menu, null)

            val popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )

            popupWindow.showAsDropDown(binding.btnMenu, -175, 10)

            popupView.findViewById<TextView>(R.id.tv_logout).setOnClickListener{
                // 로그아웃시 필요한 기능 (사용자 DB정보 없애기 등)


                // dialog 로 한번 더 물어보기
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("로그아웃 확인")
                builder.setMessage("정말 로그아웃 하시겠습니까?")
                builder.setPositiveButton("예") { dialog, _ ->
                    dialog.dismiss()
                    popupWindow.dismiss()

                    // 화면 전환 (로그인 화면으로)
                    val intent = Intent(requireContext(), LoginActivity::class.java)
                    startActivity(intent)

                    Toast.makeText(context, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                }
                builder.setNegativeButton("아니오") { dialog, _ ->
                    dialog.dismiss()
                    popupWindow.dismiss()
                }

                val dialog = builder.create()
                dialog.show()

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            }

            popupView.findViewById<TextView>(R.id.tv_quit).setOnClickListener{
                // Overlay 컨테이너를 보이게 설정
                val overlayContainer = requireActivity().findViewById<FragmentContainerView>(R.id.fcv_quit)
                overlayContainer.visibility = View.VISIBLE

                // FragmentB 생성
                val fragmentB = QuitFragment()

                // MainActivity의 supportFragmentManager를 사용하여 fcv_overlay에 replace
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fcv_quit, fragmentB)
                    .addToBackStack(null) // 필요 시 뒤로가기 지원
                    .commit()

                popupWindow.dismiss()
            }
        }
    }

    private fun clickFollow(){
        listOf(
            binding.vFollower to "follower",
            binding.vFollowing to "following"
        ).forEach { (view, title) ->
            view.setOnClickListener {
                val action=MyFragmentDirections.actionMyFragmentToFollowFragment(title)
                findNavController().navigate(action)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}