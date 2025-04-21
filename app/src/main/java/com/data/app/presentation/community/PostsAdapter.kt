package com.data.app.presentation.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import com.data.app.R
import com.data.app.data.Post
import com.data.app.databinding.ItemPostBinding
import timber.log.Timber

class PostsAdapter(val clickPost: (Post) -> Unit) :
    RecyclerView.Adapter<PostsAdapter.FeedsViewHolder>() {

    private val feedsList = mutableListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedsViewHolder(binding)
    }

    override fun getItemCount(): Int = feedsList.size

    override fun onBindViewHolder(holder: FeedsViewHolder, position: Int) {
        holder.bind(feedsList[position])
    }

    fun getList(list: List<Post>) {
        feedsList.clear()
        feedsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class FeedsViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Post) {
            with(binding) {
                ivProfile.load(data.profile) {
                    transformations(CircleCropTransformation())
                }
                ivImage.load(data.images[0]) {
                    transformations(RoundedCornersTransformation(30f))
                }

                tvName.text = root.context.getString(R.string.community_name, data.name)
                tvTime.text = root.context.getString(R.string.community_time, data.time)

                btnFollow.isSelected = data.isFollowing
                if (!data.isFollowing) btnFollow.text =
                    root.context.getString(R.string.community_following)
                tvContent.text = data.content
                tvLikeCount.text = data.like.toString()
                tvCommentCount.text = data.comments.size.toString()

                clickFollow()
                clickLike()

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

        private fun showDetail(data: Post){
            listOf(binding.tvName, binding.ivImage).forEach {
                it.setOnClickListener { clickPost(data) }
            }
        }
    }
}