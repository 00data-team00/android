package com.data.app.presentation.main.community.post_detail

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.DialogConfirmDeleteBinding
import com.data.app.databinding.FragmentPostDetailBinding
import com.data.app.extension.community.DeletePostState
import com.data.app.extension.community.LikePostState
import com.data.app.extension.community.PostDetailState
import com.data.app.extension.community.WriteCommentState
import com.data.app.extension.my.MyProfileState
import com.data.app.presentation.main.MainViewModel
import com.data.app.util.TimeAgoFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding: FragmentPostDetailBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val postDetailFragmentArgs: PostDetailFragmentArgs by navArgs()
    private val postDetailViewModel: PostDetailViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()
    private lateinit var postDetailAdapter: PostDetailAdapter
    private var isFirstLoading=false

    @Inject
    lateinit var appPreferences: AppPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPostDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setting()
    }

    private fun setting() {
        getPost()
        clickBackButton()
    }

    private fun getPost() {
        val postId = postDetailFragmentArgs.postId.toInt()
        lifecycleScope.launch {
            postDetailViewModel.postDetailState.collect { postState ->
                when (postState) {
                    is PostDetailState.Success -> {
                        val post = postState.response

                        //binding.btnFollow.isSelected = post.isFollowing
                        if(isFirstLoading){
                            binding.tvCommentCount.text = post.commentCount.toString()
                        } else{
                            showPost(post)
                            showImages(post)
                            isFirstLoading=true
                        }

                        getUserProfile(postState.response.comments)

                    }

                    is PostDetailState.Loading -> {}
                    is PostDetailState.Error -> {}
                }
            }
        }

        postDetailViewModel.getPostDetail(appPreferences.getAccessToken()!!, postId)

        /*
         showImages(post)
         clickFollow()
         clickLike()*/
    }

    private fun getUserProfile(comments:List<ResponsePostDetailDto.CommentDto>){
        lifecycleScope.launch {
            postDetailViewModel.myProfileState.collect{state->
                when(state){
                    is MyProfileState.Success -> {
                        showComments(state.response.profileImage, comments)
                    }
                    is MyProfileState.Loading -> {
                        Timber.d("my profile state loading...")
                    }
                    is MyProfileState.Error -> {
                        Timber.d("my profile state error: ${state.message}")
                    }
                }
            }
        }

        postDetailViewModel.getProfile(appPreferences.getAccessToken()!!)
    }

    private fun showPost(post: ResponsePostDetailDto) {
        with(binding) {
            val profile =
                post.authorProfileImage
            binding.ivProfile.load(profile) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
            }

            tvId.text = getString(R.string.community_id, post.authorName)

            val timeAgo = TimeAgoFormatter.formatTimeAgo(post.createdAt)
            tvTime.text = getString(R.string.community_time, timeAgo)
            tvContent.text = post.content.removeSurrounding("\"").replace("\\n", "\n")
            tvLikeCount.text = post.likeCount.toString()
            tvCommentCount.text = post.commentCount.toString()

            if (post.isLiked) btnLike.isSelected = true

            if (mainViewModel.getUserId() == post.authorId) {
                btnMenu.visibility = View.VISIBLE
                binding.btnMenu.setOnClickListener {
                    clickMenu(post.id)
                }
            }
            //btnFollow.isSelected = post.

            listOf(ivProfile, tvId).forEach {
                it.setOnClickListener {
                    clickProfileOrId(post.authorId)
                }
            }
        }

        clickLike(post.id)
    }

    private fun clickMenu(postId: Int) {
        val wrapper = ContextThemeWrapper(requireContext(), R.style.CustomPopupMenu)
        val popupMenu = PopupMenu(wrapper, binding.btnMenu)

        popupMenu.menuInflater.inflate(R.menu.menu_post, popupMenu.menu)
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_delete -> {
                    showDeleteDialog(postId)
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showDeleteDialog(postId: Int) {
        val dialogBinding = DialogConfirmDeleteBinding.inflate(layoutInflater)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .setCancelable(false)
            .create()

        dialog.setCancelable(true) // 시스템 뒤로가기 가능
        dialog.setCanceledOnTouchOutside(true) // 바깥 클릭도 닫힘

        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.btnConfirm.setOnClickListener {
            dialog.dismiss()
            lifecycleScope.launch {
                postDetailViewModel.deletePostState.collect { state ->
                    when(state){
                        is DeletePostState.Success -> {
                            Toast.makeText(requireContext(), "게시물이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack() // 게시물 삭제 후 프래그먼트 종료
                        }
                        is DeletePostState.Loading -> {
                            Timber.d("delete post state loading...")
                        }
                        is DeletePostState.Error -> {
                            Timber.d("delete post state error: ${state.message}")
                        }
                    }
                }
            }
            Timber.d("token: ${appPreferences.getAccessToken()!!}")
            postDetailViewModel.deletePost(appPreferences.getAccessToken()!!, postId)
        }

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()
       /* AlertDialog.Builder(requireContext())
            .setTitle("게시물 삭제")
            .setMessage("게시물을 삭제하시겠습니까?")
            .setPositiveButton("확인") { _, _ ->
                deletePost(postId)
            }
            .setNegativeButton("취소", null)
            .show()*/
    }

    private fun showImages(post: ResponsePostDetailDto) {
        val lp = binding.vpImages.layoutParams as ConstraintLayout.LayoutParams
        val imageUrl =
            post.imageUrl

        if (!imageUrl.isNullOrEmpty()) {
            val postDetailImageAdapter = PostDetailImageAdapter(clickImage = { position ->
                val intent = Intent(requireContext(), ImagePopupActivity::class.java).apply {
                    putStringArrayListExtra("imageList", arrayListOf(imageUrl))
                    putExtra("startIndex", position)
                }
                startActivity(intent)
                requireActivity().overridePendingTransition(0, 0)
            })

            Timber.d("imageUrl: $imageUrl")
            binding.vpImages.adapter = postDetailImageAdapter
            postDetailImageAdapter.getList(imageUrl)

            // ViewPager 간 마진 설정
            binding.vpImages.apply {
                val marginDecoration =
                    HorizontalMarginItemDecoration(requireContext(), R.dimen.viewpager_margin)
                (getChildAt(0) as RecyclerView).addItemDecoration(marginDecoration)
            }

            // 인디케이터 연결
            binding.wdiImages.attachTo(binding.vpImages)

            lp.dimensionRatio = "2:1"

        } else {
            // 이미지 없을 경우 GONE 처리
            binding.vpImages.visibility = View.GONE
            binding.wdiImages.visibility = View.GONE
            lp.dimensionRatio = null
        }

    }

    private fun showComments(profileUrl: String?, post: List<ResponsePostDetailDto.CommentDto>) {
        postDetailAdapter = PostDetailAdapter(
            addComment = { content ->
                writeComment(postDetailFragmentArgs.postId.toInt(), content)
                //updateCommentCount()
            },
            clickProfileOrId = { userId ->
                clickProfileOrId(userId)
            }
        )
        binding.rvComments.adapter = postDetailAdapter
        postDetailAdapter.getUser(profileUrl)
        postDetailAdapter.getList(post)

        lifecycleScope.launch {
            postDetailViewModel.writeCommentState.collect { state ->
                when (state) {
                    is WriteCommentState.Success -> {
                        //val comment = state.response
                        postDetailViewModel.getPostDetail(appPreferences.getAccessToken()!!, postDetailFragmentArgs.postId.toInt())
                        // postDetailAdapter.updateComment(comment)
                    }

                    is WriteCommentState.Loading -> {
                        Timber.d("write comment state loading...")
                    }

                    is WriteCommentState.Error -> {
                        Timber.d("write comment state error: ${state.message}")
                    }
                }
            }
        }

        // updateCommentCount()
    }

    private fun writeComment(postId: Int, content: String) {
        postDetailViewModel.writeComment(appPreferences.getAccessToken()!!, postId, content)
    }

    private fun clickProfileOrId(userId: Int) {
        val action =
            PostDetailFragmentDirections.actionPostDetailFragmentToOtherProfileFragment(userId.toString())
        findNavController().navigate(action)
    }

    private fun clickLike(postId: Int) {
        Timber.d("like count: ${binding.tvLikeCount.text}")
        with(binding) {
            btnLike.setOnClickListener {
                btnLike.isSelected = !btnLike.isSelected
                tvLikeCount.text = (
                        if (btnLike.isSelected) tvLikeCount.text.toString().toInt() + 1
                        else tvLikeCount.text.toString().toInt() - 1
                        ).toString()

                setLike(postId, btnLike.isSelected)
            }
        }
    }

    private fun setLike(postId: Int, isLike: Boolean) {
        lifecycleScope.launch {
            postDetailViewModel.likePostState.collect { state ->
                when (state) {
                    is LikePostState.Success -> {
                        Timber.d("like post state success!1")
                        postDetailViewModel.resetLikeState()
                    }

                    is LikePostState.Loading -> {}
                    is LikePostState.Error -> {}
                }
            }
        }

        if (isLike) postDetailViewModel.likePost(appPreferences.getAccessToken()!!, postId)
        else postDetailViewModel.unLikePost(appPreferences.getAccessToken()!!, postId)
    }

    private fun clickBackButton() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}