package com.data.app.presentation.main.community.other

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil3.request.placeholder
import coil3.request.transformations
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.Post
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.databinding.ItemPostBinding
import com.data.app.util.TimeAgoFormatter
import timber.log.Timber

class OtherProfileAdapter(
    val clickPost: (Int) -> Unit,
    val clickLike: (Int, Boolean) -> Unit,
    val clickShare: (Int) -> Unit
): RecyclerView.Adapter<OtherProfileAdapter.OtherProfileViewHolder>() {

    private val postsList = mutableListOf<ResponseTimeLineDto.TimelinePostItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherProfileViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OtherProfileViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: OtherProfileViewHolder, position: Int) {
        holder.bind(postsList[position])
    }

    fun getList(list: List<ResponseTimeLineDto.TimelinePostItem>) {
        postsList.clear()
        postsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class OtherProfileViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseTimeLineDto.TimelinePostItem) {
            with(binding) {
                val profile =
                    data.authorProfile.profileImage
                Timber.d("profile is $profile")
                binding.ivProfile.load(profile) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }

                val lp = binding.ivImage.layoutParams as ConstraintLayout.LayoutParams

                val post = data.post
                Timber.d("imageUrl: ${post.imageUrl}")
                if (!post.imageUrl.isNullOrEmpty()) {
                    val imageUrl = post.imageUrl
                    binding.ivImage.visibility = View.VISIBLE
                    binding.ivImage.load(imageUrl) {
                        transformations(RoundedCornersTransformation(30f))
                    }
                    lp.dimensionRatio = "2:1"
                } else {
                    binding.ivImage.setImageDrawable(null)
                    binding.ivImage.visibility = View.GONE
                    lp.dimensionRatio = null
                }

                binding.ivImage.setImageDrawable(null)
                binding.ivImage.visibility = View.GONE
                lp.dimensionRatio = null

                binding.ivImage.layoutParams = lp

                tvId.text = root.context.getString(R.string.community_id, data.authorProfile.name)

                val timeAgo = TimeAgoFormatter.formatTimeAgo(post.createdAt)
                tvTime.text = root.context.getString(R.string.community_time, timeAgo)

                /*  btnFollow.isSelected = data.isFollowing
                  if (data.isFollowing) btnFollow.text =
                      root.context.getString(R.string.community_follow)*/
                tvContent.text = post.content.removeSurrounding("\"").replace("\\n", "\n")   // 맨 앞뒤 " 제거
                tvLikeCount.text = post.likeCount.toString()
                tvCommentCount.text = post.commentCount.toString()

                if (data.post.isLiked) btnLike.isSelected = true

                btnShare.setOnClickListener {
                    clickShare(post.id)
                }

                // clickFollow()
                clickLike(post.id)
                showDetail(post.id)
            }
        }

        /*private fun clickFollow(){
            with(binding){
                btnFollow.setOnClickListener {
                    btnFollow.isSelected = !btnFollow.isSelected
                    btnFollow.text = root.context.getString(
                        if (btnFollow.isSelected) R.string.community_follow
                        else R.string.community_following
                    )
                    Timber.d("btn is select?${btnFollow.isSelected}")
                }
            }
        }*/

        private fun clickLike(postId: Int) {
            with(binding) {
                btnLike.setOnClickListener {
                    btnLike.isSelected = !btnLike.isSelected
                    tvLikeCount.text = (
                            if (btnLike.isSelected) tvLikeCount.text.toString().toInt() + 1
                            else tvLikeCount.text.toString().toInt() - 1
                            ).toString()

                    clickLike(postId, btnLike.isSelected)
                }
            }
        }

        private fun showDetail(postId: Int) {
            listOf(binding.tvContent, binding.ivImage).forEach {
                it.setOnClickListener { clickPost(postId) }
            }
        }

    }
}