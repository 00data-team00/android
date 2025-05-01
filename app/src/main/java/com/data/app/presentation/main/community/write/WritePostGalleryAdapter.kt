package com.data.app.presentation.main.community.write

import android.content.ClipData.Item
import android.net.Uri
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.RoundedCornersTransformation
import com.data.app.R
import com.data.app.databinding.ItemGalleryBinding
import timber.log.Timber

class WritePostGalleryAdapter(
    val clickGalleryIcon:()->Unit,
    val clickImage:(Uri)->Unit,
):RecyclerView.Adapter<WritePostGalleryAdapter.WritePostGalleryViewHolder>() {
    private val galleryList = mutableListOf<Uri>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WritePostGalleryViewHolder {
        val binding=ItemGalleryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WritePostGalleryViewHolder(binding)
    }

    override fun getItemCount(): Int = galleryList.size

    override fun onBindViewHolder(holder: WritePostGalleryViewHolder, position: Int) {
        holder.bind(galleryList[position])
    }

    fun getList(list: List<Uri>){
        galleryList.clear()
        galleryList.addAll(list)
        notifyDataSetChanged()
    }

    inner class WritePostGalleryViewHolder(private val binding:ItemGalleryBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(uri:Uri){
            with(binding){
                if (position == 0) {
                    ivPicture.setImageResource(R.drawable.ic_gallery)
                    ivCheck.visibility = View.GONE
                    ivPicture.setOnClickListener {
                        clickGalleryIcon()
                    }
                } else {
                    val radiusPx = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        16f,
                        itemView.context.resources.displayMetrics
                    )

                    ivPicture.load(uri) {
                        transformations(RoundedCornersTransformation(radiusPx))
                    }
                    ivPicture.setOnClickListener {
                        Timber.d("iv picture click")
                        ivCheck.visibility = (if (ivPicture.visibility==View.GONE) View.VISIBLE else View.GONE)
                        if(ivCheck.visibility==View.GONE) clickImage(uri)
                    }
                }
            }
        }
    }
}