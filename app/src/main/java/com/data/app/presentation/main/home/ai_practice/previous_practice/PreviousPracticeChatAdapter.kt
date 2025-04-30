package com.data.app.presentation.main.home.ai_practice.previous_practice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.data.PreviousPractice
import com.data.app.databinding.ItemChatAiBinding
import com.data.app.databinding.ItemChatMyBinding

class PreviousPracticeChatAdapter(
    private val clickChat:(String)->Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_MY_CHAT = 0
        private const val TYPE_AI_CHAT = 1
    }

    private val chatList = mutableListOf<PreviousPractice.ChatItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_MY_CHAT -> {
                val binding = ItemChatMyBinding.inflate(inflater, parent, false)
                ChatMyViewHolder(binding)
            }

            TYPE_AI_CHAT -> {
                val binding = ItemChatAiBinding.inflate(inflater, parent, false)
                ChatAiViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return when (chatList[position]) {
            is PreviousPractice.ChatItem.My -> TYPE_MY_CHAT
            is PreviousPractice.ChatItem.Ai -> TYPE_AI_CHAT
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = chatList[position]
        val isFirstOfType = position == 0 || currentItem::class != chatList[position - 1]::class

        when (currentItem) {
            is PreviousPractice.ChatItem.My -> {
                (holder as ChatMyViewHolder).bind(currentItem, isFirstOfType)
            }

            is PreviousPractice.ChatItem.Ai -> {
                (holder as ChatAiViewHolder).bind(currentItem, isFirstOfType)
            }
        }
    }

    fun getList(list: List<PreviousPractice.ChatItem>) {
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ChatMyViewHolder(private val binding: ItemChatMyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: PreviousPractice.ChatItem.My, isFirstOfType: Boolean) {
            binding.tvChat.text = content.chat
            binding.tvTime.text = content.time
            binding.ivFirst.visibility = if (isFirstOfType) View.VISIBLE else View.INVISIBLE

            val screenWidth = itemView.context.resources.displayMetrics.widthPixels
            binding.tvChat.maxWidth = (screenWidth * 0.57).toInt()

            binding.itemChatMy.setOnClickListener{
                clickChat(content.chat)
            }
        }
    }

    inner class ChatAiViewHolder(private val binding: ItemChatAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: PreviousPractice.ChatItem.Ai, isFirstOfType: Boolean) {
            with(binding) {
                ivProfile.load(content.profile)
                tvName.text = content.name
                tvChat.text = content.chat
                tvTime.text = content.time

                val screenWidth = itemView.context.resources.displayMetrics.widthPixels
                binding.tvChat.maxWidth = (screenWidth * 0.6).toInt()

                ivFirst.visibility = if (isFirstOfType) View.VISIBLE else View.INVISIBLE
                ivProfile.visibility = if (isFirstOfType) View.VISIBLE else View.INVISIBLE
                tvName.visibility = if (isFirstOfType) View.VISIBLE else View.GONE

                val layoutParams = tvChat.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topMargin = if (isFirstOfType) 0 else dpToPx(8)
                tvChat.layoutParams = layoutParams

                binding.itemChatAi.setOnClickListener{
                    clickChat(content.chat)
                }
            }
        }

        private fun dpToPx(dp: Int): Int {
            val scale = itemView.context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }
}