package com.data.app.presentation.main.home.ai_practice.ai_chat

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.R
import com.data.app.data.response_dto.home.ai.ResponseChatAiMessageDto
import com.data.app.data.response_dto.home.ai.ResponseChatStartDto
import com.data.app.databinding.ItemChatAiBinding
import com.data.app.databinding.ItemChatMyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class AIChatAdapter(
    private val clickChat:(String)->Unit,
):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_MY_CHAT = 0
        private const val TYPE_AI_CHAT = 1
    }


    private val chatList = mutableListOf<ResponseChatAiMessageDto.Message>()



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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentItem = chatList[position]
        val isFirstOfType = position == 0 || currentItem.isUser != chatList[position - 1].isUser

        when (holder) {
            is ChatMyViewHolder -> holder.bind(currentItem, isFirstOfType)
            is ChatAiViewHolder -> holder.bind(currentItem, isFirstOfType)
        }
    }


    override fun getItemCount(): Int = chatList.size

    override fun getItemViewType(position: Int): Int {
        return if (chatList[position].isUser) TYPE_MY_CHAT else TYPE_AI_CHAT
    }

    inner class ChatMyViewHolder(private val binding: ItemChatMyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: ResponseChatAiMessageDto.Message, isFirstOfType: Boolean) {


            binding.tvChat.text = content.text
            binding.tvTime.text = content.storedAt
            binding.ivFirst.visibility = if (isFirstOfType) View.VISIBLE else View.INVISIBLE

            val screenWidth = itemView.context.resources.displayMetrics.widthPixels
            binding.tvChat.maxWidth = (screenWidth * 0.57).toInt()

            val originalText = binding.tvChat.text
            setCustomTouchListener(
                targetView = binding.itemChatMy,
                onLongPress = {binding.tvChat.text = "This is translated chat text !!"},
                onLongPressEnd = {binding.tvChat.text = originalText},
                onClick = {clickChat(content.text)}
            )
        }
    }

    inner class ChatAiViewHolder(private val binding: ItemChatAiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(content: ResponseChatAiMessageDto.Message, isFirstOfType: Boolean) {
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

                val originalText = binding.tvChat.text
                setCustomTouchListener(
                    targetView = binding.itemChatAi,
                    onLongPress = {binding.tvChat.text = "This is translated chat text !!"},
                    onLongPressEnd = {binding.tvChat.text = originalText},
                    onClick = {clickChat(content.text)}
                )
            }
        }
        private fun dpToPx(dp: Int): Int {
            val scale = itemView.context.resources.displayMetrics.density
            return (dp * scale + 0.5f).toInt()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setCustomTouchListener(
        targetView: View,
        longPressTime: Long = 1000L, // 1초
        holdDuration: Long = 5000L,  // 5초
        onLongPress: () -> Unit,
        onLongPressEnd: () -> Unit,
        onClick: () -> Unit
    ){
        var job: Job? = null
        var isLongPressed = false

        targetView.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isLongPressed = false
                    job = CoroutineScope(Dispatchers.Main).launch {
                        delay(longPressTime)
                        isLongPressed = true
                        onLongPress()                   // 1초 이상 눌렀을 때 실행

                        delay(holdDuration)
                        onLongPressEnd()                // 5초 후 원래로 복구
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    if (!isLongPressed) {
                        job?.cancel()                   // 짧게 눌렀으면 취소
                        onClick()                       // 클릭 동작 실행
                    }
                    true
                }
                else -> false
            }
        }
    }

   /* fun getList(list: List<PreviousPractice.ChatItem>) {
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }*/

    fun addUserMessage(item: String, time:String,) {
        val userChat=ResponseChatAiMessageDto.Message(0, item, true, time)
        chatList.add(userChat)
        notifyItemInserted(chatList.size-1)
    }

    fun startAiMessage(message:ResponseChatStartDto){
        val aiChat = ResponseChatAiMessageDto.Message(message.chatRoomId, message.message, false, message.createdAt)
        chatList.add(aiChat)
        notifyItemInserted(chatList.size-1)
    }

    fun addAiMessage(message: ResponseChatAiMessageDto.Message){
       chatList.add(message)
        notifyItemInserted(chatList.size-1)
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