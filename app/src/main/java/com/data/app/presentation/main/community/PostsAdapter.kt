package com.data.app.presentation.main.community

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil3.request.transformations
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.response_dto.community.ResponseTimeLineDto
import com.data.app.databinding.ItemPostBinding
import com.data.app.util.TimeAgoFormatter
import timber.log.Timber

class PostsAdapter(
    val clickPost: (Int) -> Unit,
    val clickOtherUser: (Int) -> Unit,
    val clickLikeBtn: (Int, Boolean) -> Unit
) :
    RecyclerView.Adapter<PostsAdapter.FeedsViewHolder>() {

    private val postsList = mutableListOf<ResponseTimeLineDto.Posts>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedsViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: FeedsViewHolder, position: Int) {
        holder.bind(postsList[position])
    }

    fun getList(list: List<ResponseTimeLineDto.Posts>) {
        postsList.clear()
        postsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class FeedsViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseTimeLineDto.Posts) {
            with(binding) {
                val profile =
                    data.authorProfile.profileImage?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }
                ivProfile.load(profile) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }

                val lp = ivImage.layoutParams as ConstraintLayout.LayoutParams

                if (!data.post.imageUrl.isNullOrEmpty()) {
                    ivImage.visibility = View.VISIBLE

                    val imageUrl =
                        data.post.imageUrl?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }
                    Timber.d("imageUrl: $imageUrl")

                    ivImage.load(imageUrl) {
                        transformations(RoundedCornersTransformation(30f))
                    }
                    lp.dimensionRatio = "2:1"
                } else {
                    ivImage.setImageDrawable(null)
                    ivImage.visibility = View.GONE
                    lp.dimensionRatio = null
                }

                ivImage.layoutParams = lp

                tvId.text = root.context.getString(R.string.community_id, data.post.authorName)

                val timeAgo = TimeAgoFormatter.formatTimeAgo(data.post.createdAt)
                Timber.d("createdAt: ${data.post.createdAt}, formatted: $timeAgo")
                tvTime.text = root.context.getString(R.string.community_time, timeAgo)

                /* btnFollow.isSelected = data.authorProfile.isFollowing
                 if (data.authorProfile.isFollowing) btnFollow.text =
                     root.context.getString(R.string.community_follow)*/
                tvContent.text = data.post.content
                tvLikeCount.text = data.post.likeCount.toString()
                tvCommentCount.text = data.post.commentCount.toString()

                if(data.authorProfile.isLiked) btnLike.isSelected=true

                //clickFollow()
                clickLike(data.post.id)
                clickProfileOrId(data)

                showDetail(data)
            }
        }

        /* private fun clickFollow(){
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
                    clickLikeBtn(postId, btnLike.isSelected)
                    tvLikeCount.text = (
                            if (btnLike.isSelected) tvLikeCount.text.toString().toInt() + 1
                            else tvLikeCount.text.toString().toInt() - 1
                            ).toString()
                }
            }
        }

        private fun clickProfileOrId(data: ResponseTimeLineDto.Posts) {
            listOf(binding.ivProfile, binding.tvId).forEach {
                it.setOnClickListener {
                    clickOtherUser(data.post.authorId)
                }
            }
        }

        private fun showDetail(data: ResponseTimeLineDto.Posts) {
            listOf(binding.tvContent, binding.ivImage).forEach {
                it.setOnClickListener { clickPost(data.post.id) }
            }
        }

    }
}