package com.data.app.presentation.main.my

import androidx.recyclerview.widget.DiffUtil
import com.data.app.data.response_dto.community.ResponseFollowListDto

class FollowDiffCallback : DiffUtil.ItemCallback<ResponseFollowListDto.Follower>() {
    override fun areItemsTheSame(oldItem: ResponseFollowListDto.Follower, newItem: ResponseFollowListDto.Follower): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: ResponseFollowListDto.Follower, newItem: ResponseFollowListDto.Follower): Boolean {
        return oldItem == newItem
    }
}
