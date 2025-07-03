package com.data.app.presentation.main.community.other

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.Post
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.databinding.ItemPostBinding
import com.data.app.util.TimeAgoFormatter
import timber.log.Timber

class OtherProfileAdapter(val clickPost:(Int)->Unit):
RecyclerView.Adapter<OtherProfileAdapter.OtherProfileViewHolder>(){
    private val postsList = mutableListOf<ResponseMyPostDto.PostDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OtherProfileViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OtherProfileViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: OtherProfileViewHolder, position: Int) {
        holder.bind(postsList[position])
    }

    fun getList(list: List<ResponseMyPostDto.PostDto>) {
        postsList.clear()
        postsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class OtherProfileViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseMyPostDto.PostDto) {
            with(binding) {
                val profile =
                    BuildConfig.BASE_URL.removeSuffix("/")
                binding.ivProfile.load(profile){
                    transformations(CircleCropTransformation())
                }
                /*ivProfile.load(data.) {
                    transformations(CircleCropTransformation())
                }
*/
                val lp = binding.ivImage.layoutParams as ConstraintLayout.LayoutParams

                /*if (!data.images.isNullOrEmpty()) {
                    binding.ivImage.visibility = View.VISIBLE
                    binding.ivImage.load(data.images[0]) {
                        transformations(RoundedCornersTransformation(30f))
                    }
                    lp.dimensionRatio = "2:1"
                } else {
                    binding.ivImage.setImageDrawable(null)
                    binding.ivImage.visibility = View.GONE
                    lp.dimensionRatio = null
                }*/

                binding.ivImage.setImageDrawable(null)
                binding.ivImage.visibility = View.GONE
                lp.dimensionRatio = null

                binding.ivImage.layoutParams = lp

                tvId.text = root.context.getString(R.string.community_id, data.id)

                val timeAgo = TimeAgoFormatter.formatTimeAgo(data.createdAt)
                tvTime.text = root.context.getString(R.string.community_time, timeAgo)

              /*  btnFollow.isSelected = data.isFollowing
                if (data.isFollowing) btnFollow.text =
                    root.context.getString(R.string.community_follow)*/
                tvContent.text = data.content
                tvLikeCount.text = data.likeCount.toString()
                tvCommentCount.text = data.commentCount.toString()

               // clickFollow()
                clickLike()

                //showDetail(data)
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

        /*private fun showDetail(data: Post){
            listOf(binding.tvContent, binding.ivImage).forEach {
                it.setOnClickListener { clickPost(data) }
            }
        }*/

    }
}