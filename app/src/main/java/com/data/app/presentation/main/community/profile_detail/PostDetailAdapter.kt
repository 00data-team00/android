package com.data.app.presentation.main.community.profile_detail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil3.request.error
import coil3.request.placeholder
import coil3.request.transformations
import coil.transform.CircleCropTransformation
import com.data.app.BuildConfig
import com.data.app.R
import com.data.app.data.Post
import com.data.app.data.response_dto.community.ResponsePostDetailDto
import com.data.app.databinding.ItemCommentBinding
import com.data.app.databinding.ItemCommentWriteBinding
import timber.log.Timber

class PostDetailAdapter(
    val addComment:(Int)->Unit,
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

    inner class CommentWriteViewHolder(private val binding:ItemCommentWriteBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(profileUrl:String?){
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


           // clickWrite(data.id, data.profile)
        }

       /* private fun clickWrite(user:String, profile:String){
            binding.btnWrite.setOnClickListener {
                val text = binding.etCommentWrite.text.toString()
                if (text.isNotBlank()) {
                    val newComment = Post.Comments(
                        profile = profile,
                        name = user,
                        content = text,
                        like = 0
                    )
                    commentsList.add(0, newComment)
                    notifyDataSetChanged()
                    binding.etCommentWrite.text?.clear()

                    // 키보드 내리기
                    val imm = binding.root.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(binding.etCommentWrite.windowToken, 0)

                    // focus 제거
                    binding.etCommentWrite.clearFocus()
                    addComment(commentsList.size)
                }
            }
        }*/
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: ResponsePostDetailDto.CommentDto){
            with(binding){
                val profile = profileUrl?.let { BuildConfig.BASE_URL.removeSuffix("/") + it }

                binding.ivProfile.load(profile) {
                    transformations(CircleCropTransformation())
                    placeholder(R.drawable.ic_profile)
                    error(R.drawable.ic_profile)
                }
                tvId.text=comment.commenterName
                tvContent.text=comment.content
                tvLikeCount.text="1"


            }

            listOf(binding.ivProfile, binding.tvId).forEach {
                it.setOnClickListener {
                    clickProfileOrId(comment.commenterId)
                }
            }

            clickLike()
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
    }
}