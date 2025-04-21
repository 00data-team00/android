package com.data.app.presentation.community

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import com.data.app.R
import com.data.app.data.Feed
import com.data.app.databinding.ItemFeedBinding

class FeedsAdapter : RecyclerView.Adapter<FeedsAdapter.FeedsViewHolder>() {

    private val feedsList= mutableListOf<Feed>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedsViewHolder {
       val binding=ItemFeedBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FeedsViewHolder(binding)
    }

    override fun getItemCount(): Int = feedsList.size

    override fun onBindViewHolder(holder: FeedsViewHolder, position: Int) {
       holder.bind(feedsList[position])
    }

    fun getList(list: List<Feed>){
        feedsList.clear()
        feedsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class FeedsViewHolder(private val binding: ItemFeedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Feed) {
            with(binding) {
                ivProfile.load(data.profile) {
                    transformations(CircleCropTransformation())
                }
                ivImage.load(data.image) {
                    transformations(RoundedCornersTransformation(12f))
                }

                tvName.text = root.context.getString(R.string.community_name, data.name)
                tvTime.text = root.context.getString(R.string.community_time, data.time)

                btnFollow.isSelected = data.isFollowing
                if(!data.isFollowing) btnFollow.text=root.context.getString(R.string.community_follow)
                tvContent.text = data.content
                tvLikeCount.text = data.like.toString()
                tvCommentCount.text = data.comments.size.toString()

                btnFollow.setOnClickListener{
                    btnFollow.isSelected=!btnFollow.isSelected
                    btnFollow.text=root.context.getString(
                        if(btnFollow.isSelected) R.string.community_follow
                        else R.string.community_following
                    )
                }
                
                btnLike.setOnClickListener{
                    btnLike.isSelected=!btnLike.isSelected
                }
            }
        }
    }
}