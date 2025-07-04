package com.data.app.presentation.main.community.post_detail

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import timber.log.Timber

class PostDetailImageAdapter(val clickImage: (Int) -> Unit) :
    RecyclerView.Adapter<PostDetailImageAdapter.PostDetailImageViewHolder>() {
    private val images = mutableListOf<String>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostDetailImageViewHolder {
        val imageView = ImageView(parent.context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }
        return PostDetailImageViewHolder(imageView)
    }

    override fun getItemCount(): Int = images.size

    override fun onBindViewHolder(holder: PostDetailImageViewHolder, position: Int) {
        holder.bind(images[position])
    }

    fun getList(imageUrl: String) {
        images.clear()
        images.addAll(listOf(imageUrl))
        notifyDataSetChanged()
    }


    inner class PostDetailImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView = itemView as ImageView

        fun bind(imageRes: String) {
            imageView.scaleType = ImageView.ScaleType.CENTER_CROP
            Timber.d("imageRes: $imageRes")

            imageView.load(imageRes) {
                transformations(
                    RoundedCornersTransformation(30f)
                )
            }

            imageView.setOnClickListener {
                clickImage(position)
            }
        }
    }
}