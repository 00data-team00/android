package com.data.app.presentation.main.community.profile_detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil3.request.placeholder
import coil3.request.transformations
import coil.transform.CircleCropTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.Post
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.data.shared_preferences.AppPreferences
import com.data.app.databinding.FragmentPostDetailBinding
import com.data.app.extension.community.PostDetailState
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
    private lateinit var postDetailAdapter: PostDetailAdapter

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
                        showPost(post)
                        showImages(post)
                        showComments(post.authorProfileImage, post.comments)
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

    private fun showPost(post: ResponsePostDetailDto){
        with(binding){
            val profile = post.authorProfileImage?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }

            binding.ivProfile.load(profile) {
                transformations(CircleCropTransformation())
                placeholder(R.drawable.ic_profile)
                error(R.drawable.ic_profile)
            }

            tvId.text = getString(R.string.community_id, post.authorName)

            val timeAgo = TimeAgoFormatter.formatTimeAgo(post.createdAt)
            tvTime.text = getString(R.string.community_time, timeAgo)
            tvContent.text = post.content
            tvLikeCount.text = post.likeCount.toString()
            tvCommentCount.text = post.commentCount.toString()

            //btnFollow.isSelected = post.

            listOf(ivProfile, tvId).forEach {
                it.setOnClickListener {
                    clickProfileOrId(post.authorId)
                }
            }
        }

    }

    private fun showImages(post: ResponsePostDetailDto) {
        val lp = binding.vpImages.layoutParams as ConstraintLayout.LayoutParams
        val imageUrl =
            post.imageUrl?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }

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

    private fun showComments(profileUrl:String?, post: List<ResponsePostDetailDto.CommentDto>) {
        postDetailAdapter = PostDetailAdapter(
            addComment = { size ->
                binding.tvCommentCount.text = size.toString()
            },
            clickProfileOrId = { userId ->
                clickProfileOrId(userId)
            }
        )
        binding.rvComments.adapter = postDetailAdapter
        postDetailAdapter.getUser(profileUrl)
        postDetailAdapter.getList(post)
    }

    private fun clickProfileOrId(userId:Int) {
        val action = PostDetailFragmentDirections.actionPostDetailFragmentToOtherProfileFragment(userId.toString())
        findNavController().navigate(action)
    }

   /* private fun clickFollow() {
        with(binding.btnFollow) {
            setOnClickListener {
                isSelected = !isSelected
                text = context.getString(
                    if (isSelected) R.string.community_follow
                    else R.string.community_following
                )
            }
        }
    }
*/
    private fun clickLike() {
        Timber.d("like count: ${binding.tvLikeCount.text}")
        with(binding) {
            btnLike.setOnClickListener {
                btnLike.isSelected = !btnLike.isSelected
                tvLikeCount.text = (
                        if (btnLike.isSelected) tvLikeCount.text.toString().toInt() + 1
                        else tvLikeCount.text.toString().toInt() - 1
                        ).toString()
            }
        }
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