package com.data.app.presentation.main.community.post_detail

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import com.data.app.extension.my.SharePostState
import com.data.app.presentation.main.MainViewModel
import com.data.app.util.TimeAgoFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
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
    private var isFirstLoading = false
    private var isFromCommentWrite = false


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
        val postId = postDetailFragmentArgs.postId.toInt()
        Timber.d("postId: ${postId}")
        getPost(postId)
        clickBackButton()

        refresh(postId)
    }

    private fun refresh(postId:Int){
        binding.btnRefresh.setOnClickListener{
            postDetailViewModel.getPostDetail(appPreferences.getAccessToken()!!, postId)
        }
    }

    private fun getPost(postId:Int) {
        lifecycleScope.launch {
            postDetailViewModel.postDetailState.collect { postState ->
                when (postState) {
                    is PostDetailState.Success -> {
                        with(binding){
                            tvId.visibility = View.VISIBLE
                            ivProfile.visibility = View.VISIBLE
                            tvTime.visibility = View.VISIBLE
                            tvContent.visibility = View.VISIBLE
                            btnLike.visibility = View.VISIBLE
                            tvLikeCount.visibility = View.VISIBLE
                            btnComment.visibility = View.VISIBLE
                            tvCommentCount.visibility = View.VISIBLE
                            btnShare.visibility = View.VISIBLE

                            ivNointernet.visibility = View.GONE
                            tvNointernet.visibility = View.GONE
                            btnRefresh.visibility = View.GONE
                        }

                        val post = postState.response

                        //binding.btnFollow.isSelected = post.isFollowing
                        if(isFromCommentWrite){
                            binding.tvCommentCount.text = post.commentCount.toString()
                            isFromCommentWrite = false
                        } else{
                            showPost(post)
                            showImages(post)
                        }

                        getUserProfile(postState.response.comments)
                        binding.btnShare.setOnClickListener {
                            sharePost(postState.response.id)
                        }

                    }

                    is PostDetailState.Loading -> {
                        Timber.d("loading in post detail")
                        with(binding){
                            tvId.visibility = View.GONE
                            ivProfile.visibility = View.GONE
                            tvTime.visibility = View.GONE
                            tvContent.visibility = View.GONE
                            btnLike.visibility = View.GONE
                            tvLikeCount.visibility = View.GONE
                            btnComment.visibility = View.GONE
                            tvCommentCount.visibility = View.GONE
                            btnShare.visibility = View.GONE

                            ivNointernet.visibility = View.VISIBLE
                            tvNointernet.visibility = View.VISIBLE
                            btnRefresh.visibility = View.VISIBLE
                        }
                    }
                    is PostDetailState.Error -> {
                        if(postState.message.contains("No address")){
                            binding.ivNointernet.visibility = View.VISIBLE
                            binding.tvNointernet.visibility = View.VISIBLE
                            binding.btnRefresh.visibility = View.VISIBLE
                        }
                    }
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

            if (appPreferences.getUserId() == post.authorId) {
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
        isFromCommentWrite = true
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

    private fun sharePost(postId: Int) {
        lifecycleScope.launch {
            postDetailViewModel.sharePostState.collect { state ->
                when (state) {
                    is SharePostState.Loading -> {
                        Timber.d("share post loading...")
                    }

                    is SharePostState.Success -> {
                        val url = BuildConfig.BASE_URL.removeSuffix("/") + state.response.shareUrl
                        copyToClipboard(url)
                        this.cancel() // 종료
                    }

                    is SharePostState.Error -> {
                        Timber.e("share post error!")
                        this.cancel() // 종료
                    }
                }
            }
        }

        postDetailViewModel.sharePost(postId)
    }

    private fun copyToClipboard(text: String) {
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Profile URL", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(requireContext(), "링크가 복사되었습니다.", Toast.LENGTH_SHORT).show()
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