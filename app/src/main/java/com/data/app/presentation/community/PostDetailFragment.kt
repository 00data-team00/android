package com.data.app.presentation.community

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.data.app.R
import com.data.app.data.Post
import com.data.app.databinding.FragmentPostDetailBinding
import com.google.android.material.tabs.TabLayoutMediator
import timber.log.Timber

class PostDetailFragment : Fragment() {
    private var _binding: FragmentPostDetailBinding? = null
    private val binding: FragmentPostDetailBinding
        get() = requireNotNull(_binding) { "home fragment is null" }

    private val postDetailFragmentArgs: PostDetailFragmentArgs by navArgs()
    private lateinit var communityDetailAdapter: PostDetailAdapter

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
        val post = postDetailFragmentArgs.post
        showPost(post)
        showComments(post)
        clickBackButton()
    }

    private fun showPost(post: Post) {
        with(binding) {
            ivProfile.load(post.profile) {
                transformations(CircleCropTransformation())
            }
            tvName.text = getString(R.string.community_name, post.name)
            tvTime.text = getString(R.string.community_time, post.time)
            tvContent.text = post.content
            tvLikeCount.text = post.like.toString()
            tvCommentCount.text = post.comments.size.toString()

            btnFollow.isSelected = post.isFollowing
        }
        showImages(post)
        clickFollow()
        clickLike()
    }

    private fun showImages(post:Post){
        val lp = binding.vpImages.layoutParams as ConstraintLayout.LayoutParams
        if (!post.images.isNullOrEmpty()) {
            val postDetailImageAdapter = PostDetailImageAdapter(clickImage = { position ->
                val intent = Intent(requireContext(), ImagePopupActivity::class.java).apply {
                    putIntegerArrayListExtra("imageList", ArrayList(post.images))
                    putExtra("startIndex", position)
                }
                startActivity(intent)
                requireActivity().overridePendingTransition(0, 0)
            })

            binding.vpImages.adapter = postDetailImageAdapter
            postDetailImageAdapter.getList(post.images)

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

    private fun showComments(post: Post) {
        communityDetailAdapter = PostDetailAdapter()
        binding.rvComments.adapter = communityDetailAdapter
        communityDetailAdapter.getUser(post)
        communityDetailAdapter.getList(post.comments)
    }

    private fun clickFollow() {
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