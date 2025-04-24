package com.data.app.presentation.my

import androidx.recyclerview.widget.DiffUtil
import com.data.app.data.Follow

class FollowDiffCallback : DiffUtil.ItemCallback<Follow>() {
    override fun areItemsTheSame(oldItem: Follow, newItem: Follow): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Follow, newItem: Follow): Boolean {
        return oldItem == newItem
    }
}
