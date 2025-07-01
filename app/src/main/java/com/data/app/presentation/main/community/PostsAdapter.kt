package com.data.app.presentation.main.community

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import com.data.app.R
import com.data.app.data.Post
import com.data.app.databinding.ItemPostBinding
import timber.log.Timber

class PostsAdapter(val clickPost: (Post) -> Unit, val clickOtherUser:(String, String)->Unit) :
    RecyclerView.Adapter<PostsAdapter.FeedsViewHolder>() {

    private val postsList = mutableListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedsViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: FeedsViewHolder, position: Int) {
        holder.bind(postsList[position])
    }

    fun getList(list: List<Post>) {
        postsList.clear()
        postsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class FeedsViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Post) {
            with(binding) {
                ivProfile.load(data.profile) {
                    transformations(CircleCropTransformation())
                }

                val lp = binding.ivImage.layoutParams as ConstraintLayout.LayoutParams

                if (!data.images.isNullOrEmpty()) {
                    binding.ivImage.visibility = View.VISIBLE
                    binding.ivImage.load(data.images[0]) {
                        transformations(RoundedCornersTransformation(30f))
                    }
                    lp.dimensionRatio = "2:1"
                } else {
                    binding.ivImage.setImageDrawable(null)
                    binding.ivImage.visibility = View.GONE
                    lp.dimensionRatio = null
                }

                binding.ivImage.layoutParams = lp

                tvId.text = root.context.getString(R.string.community_id, data.id)
                tvTime.text = root.context.getString(R.string.community_time, data.time)

                btnFollow.isSelected = data.isFollowing
                if (data.isFollowing) btnFollow.text =
                    root.context.getString(R.string.community_follow)
                tvContent.text = data.content
                tvLikeCount.text = data.like.toString()
                tvCommentCount.text = data.comments.size.toString()

                clickFollow()
                clickLike()
                clickProfileOrId(data)

                showDetail(data)
            }
        }

        private fun clickFollow(){
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
        }

        private fun clickLike(){
            with(binding){
                btnLike.setOnClickListener {
                    btnLike.isSelected = !btnLike.isSelected
                    tvLikeCount.text = (
                            if (btnLike.isSelected) tvLikeCount.text.toString().toInt() + 1
                            else tvLikeCount.text.toString().toInt() - 1
                            ).toString()
                }
            }
        }

        private fun clickProfileOrId(data: Post){
            listOf(binding.ivProfile, binding.tvId).forEach {
                it.setOnClickListener {
                    clickOtherUser(data.profile, data.id)
                }
            }
        }

        private fun showDetail(data: Post){
            listOf(binding.tvContent, binding.ivImage).forEach {
                it.setOnClickListener { clickPost(data) }
            }
        }

    }
}