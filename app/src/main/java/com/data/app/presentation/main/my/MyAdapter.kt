package com.data.app.presentation.main.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import coil.transform.RoundedCornersTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.response_dto.my.ResponseMyPostDto
import com.data.app.databinding.ItemPostBinding
import com.data.app.util.TimeAgoFormatter
import timber.log.Timber

class MyAdapter(val clickPost:(Int)->Unit, val clickLike:(isLike:Boolean, postId:Int)->Unit):
RecyclerView.Adapter<com.data.app.presentation.main.my.MyAdapter.MyViewHolder>(){

    private var userProfile:String?=null
    private val postsList = mutableListOf<ResponseMyPostDto.PostDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): _root_ide_package_.com.data.app.presentation.main.my.MyAdapter.MyViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: _root_ide_package_.com.data.app.presentation.main.my.MyAdapter.MyViewHolder, position: Int) {
        holder.bind(postsList[position])
    }

    fun getList(profile:String?, list: List<ResponseMyPostDto.PostDto>) {
        userProfile=profile
        postsList.clear()
        postsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseMyPostDto.PostDto) {
            with(binding) {
                Timber.d("userProfile: $userProfile")
                if(userProfile!=null) {
                    ivProfile.load(userProfile) {
                        transformations(CircleCropTransformation())
                    }
                }else{
                    ivProfile.load(R.drawable.ic_profile){
                        transformations(CircleCropTransformation())
                    }
                }

                val lp = ivImage.layoutParams as ConstraintLayout.LayoutParams

                if (!data.imageUrl.isNullOrEmpty()) {
                    ivImage.visibility = View.VISIBLE
                    val imageUrl =
                        BuildConfig.BASE_URL.removeSuffix("/")+data.imageUrl
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

                tvId.text = root.context.getString(R.string.community_id, data.authorName)

                val timeAgo = TimeAgoFormatter.formatTimeAgo(data.createdAt)
                Timber.d("createdAt: ${data.createdAt}, formatted: $timeAgo")
                tvTime.text = root.context.getString(R.string.community_time, timeAgo)

                tvContent.text = data.content
                tvLikeCount.text = data.likeCount.toString()
                tvCommentCount.text = data.commentCount.toString()

                if(data.isLiked) btnLike.isSelected=true

                //btnFollow.visibility=View.GONE

                clickLike()

                showDetail(data)
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

                    clickLike(btnLike.isSelected, postsList[adapterPosition].id)
                }
            }
        }

        private fun showDetail(data: ResponseMyPostDto.PostDto){
            listOf(binding.tvContent, binding.ivImage).forEach {
                it.setOnClickListener { clickPost(data.id) }
            }
        }

    }
}