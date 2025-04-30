package com.data.app.presentation.main.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.data.app.R
import com.data.app.data.Follow
import com.data.app.databinding.ItemFollowBinding

class FollowAdapter(
    val clickProfile: (Int, String) -> Unit,
) : ListAdapter<Follow, FollowAdapter.FollowViewHolder>(FollowDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class FollowViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Follow) {
            with(binding) {
                tvName.text = data.name
                tvId.text = root.context.getString(R.string.community_id, data.id)
                btnFollow.isSelected = data.isFollow

                ivProfile.load(R.drawable.ic_profile) {
                    transformations(CircleCropTransformation())
                }
            }

            buttonFollowColor()
            clickFollow()
            clickother(R.drawable.ic_profile, data.name)
        }

        private fun buttonFollowColor() {
            with(binding) {
                btnFollow.apply {
                    if (isSelected) {
                        text = context.getString(R.string.community_follow)
                        setTextColor(ContextCompat.getColor(context, R.color.white))
                    } else {
                        text = context.getString(R.string.community_following)
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.community_content_black
                            )
                        )
                    }
                }
            }
        }

        private fun clickFollow() {
            binding.btnFollow.setOnClickListener {
                binding.btnFollow.isSelected = !binding.btnFollow.isSelected
                buttonFollowColor()
            }
        }

        private fun clickother(profile:Int, name:String) {
            with(binding){
                listOf(
                    ivProfile,
                    tvName,
                    tvId
                ).forEach {
                        view ->
                    view.setOnClickListener{
                        clickProfile(profile, name)
                    }
                }
            }

        }
    }
}