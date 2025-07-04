package com.data.app.presentation.main.my

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.response_dto.community.ResponseFollowListDto
import com.data.app.databinding.ItemFollowBinding
import timber.log.Timber

class FollowAdapter(
    val clickProfile: (Int) -> Unit,
    val clickFollow:(Boolean, Int)->Unit
) : ListAdapter<ResponseFollowListDto.Follower, FollowAdapter.FollowViewHolder>(FollowDiffCallback()) {

    private var clickAbleFollow = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FollowViewHolder {
        val binding = ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FollowViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FollowViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun resetBtn(){
        clickAbleFollow=true
    }

    inner class FollowViewHolder(private val binding: ItemFollowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseFollowListDto.Follower) {
            with(binding) {
                tvName.text = data.name
                btnFollow.isSelected = data.isFollowing

                val profile =
                    data.profileImage?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }
                // val resourceId = resources.getIdentifier("ic_profile", "drawable", requireContext().packageName)
                ivProfile.load(profile) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_profile)
                    fallback(R.drawable.ic_profile) // profile이 null일 때 기본 이미지 표시
                }

            }

            buttonFollowColor()
            clickFollow(data.userId)
            clickother(data.userId)
        }

        private fun clickFollow(userId: Int) {
            binding.btnFollow.setOnClickListener {
                if(!clickAbleFollow) return@setOnClickListener
                clickAbleFollow=false
                binding.btnFollow.isSelected = !binding.btnFollow.isSelected

                Timber.d("isSelected?: ${binding.btnFollow.isSelected}")
                clickFollow(binding.btnFollow.isSelected, userId)
                buttonFollowColor()
            }
        }
        private fun buttonFollowColor() {
            with(binding) {
                btnFollow.apply {
                    if (isSelected) {
                        text = context.getString(R.string.community_following)
                        setTextColor(ContextCompat.getColor(context, R.color.community_content_black))
                    } else {
                        text = context.getString(R.string.community_follow)
                        setTextColor(
                            ContextCompat.getColor(
                                context,
                                R.color.white
                            )
                        )
                    }
                }
            }
        }

        private fun clickother(userId: Int) {
            with(binding){
                listOf(
                    ivProfile,
                    tvName,
                    tvId
                ).forEach {
                        view ->
                    view.setOnClickListener{
                        clickProfile(userId)
                    }
                }
            }

        }
    }
}