package com.data.app.presentation.community

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import coil3.request.transformations
import coil3.transform.CircleCropTransformation
import com.data.app.R
import com.data.app.data.Post
import com.data.app.databinding.ItemCommentBinding
import com.data.app.databinding.ItemCommentWriteBinding

class PostDetailAdapter(
    val addComment:(Int)->Unit
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var user:Post
    private val commentsList = mutableListOf<Post.Comments>()

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
            holder.bind(user)
        } else if (holder is CommentViewHolder) {
            holder.bind(commentsList[position - 1])
        }
    }

    fun getUser(data:Post){
        user=data
        notifyDataSetChanged()
    }

    fun getList(list: List<Post.Comments>){
        commentsList.clear()
        commentsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class CommentWriteViewHolder(private val binding:ItemCommentWriteBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data:Post){
            binding.ivProfile.load(data.profile){
                transformations(CircleCropTransformation())
            }

            clickWrite(data.name, data.profile)
        }

        private fun clickWrite(user:String, profile:Int){
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
        }
    }

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Post.Comments){
            with(binding){
                ivProfile.load(comment.profile){
                    transformations(CircleCropTransformation())
                }
                tvName.text=comment.name
                tvContent.text=comment.content
                tvLikeCount.text=comment.like.toString()
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