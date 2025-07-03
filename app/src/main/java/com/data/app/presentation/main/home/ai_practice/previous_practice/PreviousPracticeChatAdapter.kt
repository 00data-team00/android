package com.data.app.presentation.main.home.ai_practice.previous_practice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.R
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousChatMessagesDto
import com.data.app.databinding.ItemChatAiBinding
import com.data.app.databinding.ItemChatMyBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreviousPracticeChatAdapter(
    private val clickChat:(String)->Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_MY_CHAT = 0
        private const val TYPE_AI_CHAT = 1
    }

    private val chatList = mutableListOf<ResponseAIPreviousChatMessagesDto.Message>()

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
        return if (chatList[position].isUser) TYPE_MY_CHAT else TYPE_AI_CHAT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = chatList[position]
        val isFirstOfType = position == 0 || currentItem.isUser != chatList[position - 1].isUser

        when (holder) {
            is ChatMyViewHolder -> holder.bind(currentItem, isFirstOfType)
            is ChatAiViewHolder -> holder.bind(currentItem, isFirstOfType)
        }
    }

    fun getList(list: List<ResponseAIPreviousChatMessagesDto.Message>) {
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ChatMyViewHolder(private val binding: ItemChatMyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: ResponseAIPreviousChatMessagesDto.Message, isFirstOfType: Boolean) {
            binding.tvChat.text = content.text
            binding.tvTime.text = formatToTimeOnly(content.storedAt)
            binding.ivFirst.visibility = if (isFirstOfType) View.VISIBLE else View.INVISIBLE

            val screenWidth = itemView.context.resources.displayMetrics.widthPixels
            binding.tvChat.maxWidth = (screenWidth * 0.57).toInt()

            binding.itemChatMy.setOnClickListener{
                clickChat(content.text)
            }
        }
    }

    inner class ChatAiViewHolder(private val binding: ItemChatAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: ResponseAIPreviousChatMessagesDto.Message, isFirstOfType: Boolean) {
            with(binding) {
                ivProfile.load(R.drawable.ic_basic_profile)
                tvName.text = "AI"
                tvChat.text = content.text
                tvTime.text = formatToTimeOnly(content.storedAt)

                val screenWidth = itemView.context.resources.displayMetrics.widthPixels
                binding.tvChat.maxWidth = (screenWidth * 0.6).toInt()

                ivFirst.visibility = if (isFirstOfType) View.VISIBLE else View.INVISIBLE
                ivProfile.visibility = if (isFirstOfType) View.VISIBLE else View.INVISIBLE
                tvName.visibility = if (isFirstOfType) View.VISIBLE else View.GONE

                val layoutParams = tvChat.layoutParams as ConstraintLayout.LayoutParams
                layoutParams.topMargin = if (isFirstOfType) 0 else dpToPx(8)
                tvChat.layoutParams = layoutParams

                binding.itemChatAi.setOnClickListener{
                    clickChat(content.text)
                }
            }
        }

        private fun dpToPx(dp: Int): Int {
            val scale = itemView.context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }

    private fun formatToTimeOnly(isoString: String): String {
        return try {
            val localDateTime = LocalDateTime.parse(isoString)
            localDateTime.format(DateTimeFormatter.ofPattern("HH:mm"))
        } catch (e: Exception) {
            "-"
        }
    }
}