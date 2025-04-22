package com.data.app.presentation.my

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import coil3.transform.RoundedCornersTransformation
import com.data.app.R
import com.data.app.data.Post
import com.data.app.databinding.ItemPostBinding
import timber.log.Timber

class MyAdapter(val clickPost:(Post)->Unit):
RecyclerView.Adapter<MyAdapter.MyViewHolder>(){

    private val postsList = mutableListOf<Post>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun getItemCount(): Int = postsList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bind(postsList[position])
    }

    fun getList(list: List<Post>) {
        postsList.clear()
        postsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class MyViewHolder(private val binding: ItemPostBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Post) {
            with(binding) {
                ivProfile.load(data.profile) {
                    transformations(CircleCropTransformation())
                }

                val lp = ivImage.layoutParams as ConstraintLayout.LayoutParams

                if (!data.images.isNullOrEmpty()) {
                    ivImage.visibility = View.VISIBLE
                    ivImage.load(data.images[0]) {
                        transformations(RoundedCornersTransformation(30f))
                    }
                    lp.dimensionRatio = "2:1"
                } else {
                    ivImage.setImageDrawable(null)
                    ivImage.visibility = View.GONE
                    lp.dimensionRatio = null
                }

                ivImage.layoutParams = lp

                tvId.text = root.context.getString(R.string.community_name, data.name)
                tvTime.text = root.context.getString(R.string.community_time, data.time)

                tvContent.text = data.content
                tvLikeCount.text = data.like.toString()
                tvCommentCount.text = data.comments.size.toString()

                btnFollow.visibility=View.GONE

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