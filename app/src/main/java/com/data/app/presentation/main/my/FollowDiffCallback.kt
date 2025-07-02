package com.data.app.presentation.main.my

import androidx.recyclerview.widget.DiffUtil
import com.data.app.data.response_dto.community.ResponseFollowersDto

class FollowDiffCallback : DiffUtil.ItemCallback<ResponseFollowersDto.Follower>() {
    override fun areItemsTheSame(oldItem: ResponseFollowersDto.Follower, newItem: ResponseFollowersDto.Follower): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: ResponseFollowersDto.Follower, newItem: ResponseFollowersDto.Follower): Boolean {
        return oldItem == newItem
    }
}
