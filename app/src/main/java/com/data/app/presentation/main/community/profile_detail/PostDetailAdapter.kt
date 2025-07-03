package com.data.app.presentation.main.community.profile_detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.databinding.ItemCommentBinding
import com.data.app.databinding.ItemCommentWriteBinding
import timber.log.Timber

class PostDetailAdapter(
    val addComment:(String)->Unit,
    val clickProfileOrId:(Int)->Unit,
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var profileUrl:String?=null
    private val commentsList = mutableListOf<ResponsePostDetailDto.CommentDto>()

    companion object {
        private const val VIEW_TYPE_WRITE = 0
        private const val VIEW_TYPE_COMMENT = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_WRITE else VIEW_TYPE_COMMENT
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_WRITE) {
            val binding = ItemCommentWriteBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            CommentWriteViewHolder(binding)
        } else {
            val binding = ItemCommentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false)
            CommentViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = commentsList.size+1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is CommentWriteViewHolder) {
            holder.bind(profileUrl)
        } else if (holder is CommentViewHolder) {
            holder.bind(commentsList[position - 1])
        }
    }

    fun getUser(profileUrl:String?){
        if (profileUrl != null) {
            this.profileUrl = profileUrl
        }

        notifyDataSetChanged()
    }

    fun getList(list: List<ResponsePostDetailDto.CommentDto>){
        commentsList.clear()
        commentsList.addAll(list)
        notifyDataSetChanged()
    }

    fun updateComment(comment: ResponsePostDetailDto.CommentDto){
        commentsList.add(0, comment)
        notifyItemInserted(0)
    }

    inner class CommentWriteViewHolder(private val binding:ItemCommentWriteBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(profileUrl: String?){
            val profile = profileUrl?.takeIf { it.isNotBlank() && it != "null" }?.let {
                BuildConfig.BASE_URL.removeSuffix("/") + it
            }
            Timber.e("ic_profile: $profile")

            binding.ivProfile.post {
                val context = itemView.context

                try {
                    binding.ivProfile.load(profile) {
                        transformations(CircleCropTransformation())
                        placeholder(R.drawable.ic_profile)
                        error(R.drawable.ic_profile)
                    }
                } catch (e: Exception) {
                    Timber.e( "Coil load failed: ${e.message}")
                    binding.ivProfile.setImageResource(R.drawable.ic_profile)
                }
            }

            binding.etCommentWrite.addTextChangedListener {
                val hasText = !it.isNullOrBlank()

                val context = itemView.context

                binding.btnWrite.isSelected = hasText
                binding.btnWrite.setTextColor(
                    ContextCompat.getColor(context,
                        if (hasText) R.color.black else R.color.mock_ai_practice_title_gray
                    )
                )
            }


           clickWrite()
        }

        private fun clickWrite(){
            binding.btnWrite.setOnClickListener {
                if(binding.btnWrite.isSelected){
                    val text = binding.etCommentWrite.text.toString()
                    addComment(text)
                    binding.etCommentWrite.setText("")
                }
            }
        }
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: ResponsePostDetailDto.CommentDto){
            with(binding){
                val profile = comment.commenterProfileImage?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }

                ivProfile.load(profile) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }
                tvId.text=comment.commenterName
                tvContent.text=comment.content
                //tvLikeCount.text="1"
            }

            listOf(binding.ivProfile, binding.tvId).forEach {
                it.setOnClickListener {
                    clickProfileOrId(comment.commenterId)
                }
            }

            //clickLike()
        }

       /* private fun clickLike(){
            with(binding){
                btnLike.setOnClickListener {
                    btnLike.isSelected = !btnLike.isSelected
                    tvLikeCount.text = (
                            if (btnLike.isSelected) tvLikeCount.text.toString().toInt() + 1
                            else tvLikeCount.text.toString().toInt() - 1
                            ).toString()
                }
            }
        }*/
    }
}