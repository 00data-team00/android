package com.data.app.presentation.main.home.ai_practice.previous_practice

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.R
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousChatMessagesDto
import com.data.app.databinding.ItemChatAiBinding
import com.data.app.databinding.ItemChatMyBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreviousPracticeChatAdapter(
    private val clickChat:(String)->Unit,
    private val request:(Int)->Unit,
    private val change:(Int, Int)->Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    companion object {
        private const val TYPE_SHIMMER = 0
        private const val TYPE_MY_CHAT = 1
        private const val TYPE_AI_CHAT = 2

    }

    private var isChatLoading = true
    private val chatList = mutableListOf<ResponseAIPreviousChatMessagesDto.Message>()

    private val originalTextMap: MutableMap<Int, String> = mutableMapOf()
    private val longPressCheck = mutableListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_SHIMMER->{
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_shimmer, parent, false)
                ShimmerViewHolder(view)
            }

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

    override fun getItemCount(): Int = if(isChatLoading) 3 else chatList.size

    override fun getItemViewType(position: Int): Int {
        return if (isChatLoading) TYPE_SHIMMER else if(chatList[position].isUser) TYPE_MY_CHAT else TYPE_AI_CHAT
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (isChatLoading || position >= chatList.size) return

        val currentItem = chatList[position]
        val isFirstOfType = position == 0 || currentItem.isUser != chatList[position - 1].isUser

        when (holder) {
            is ChatMyViewHolder -> holder.bind(currentItem, isFirstOfType)
            is ChatAiViewHolder -> holder.bind(currentItem, isFirstOfType)
        }
    }

    fun setChatLoading(){
        isChatLoading=false
        notifyDataSetChanged()
    }

    fun getList(list: List<ResponseAIPreviousChatMessagesDto.Message>) {
        chatList.clear()
        chatList.addAll(list)
        notifyDataSetChanged()
    }

    fun translatePosition(position: Int, translated: String){
        Log.d("TRANSLATE", "translating position $position with $translated")
        chatList[position].text = translated
        notifyItemChanged(position)
    }

    inner class ShimmerViewHolder(view: View) : RecyclerView.ViewHolder(view)

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

                if (content.messageId !in longPressCheck){
                    originalTextMap[content.messageId] = binding.tvChat.text.toString()
                }

                val pos = bindingAdapterPosition

                setCustomTouchListener(
                    targetView = binding.itemChatAi,
                    onLongPress = {
                        Timber.d("ai chat long pressing~")
                        if (content.messageId !in longPressCheck) {
                            longPressCheck.add(content.messageId)
                            change(pos, content.messageId)
                            request(content.messageId)
                        }
                    },
                    onLongPressEnd = {
                        Log.d("AICHAT", chatList.toString())
                        longPressCheck.remove(content.messageId)
                        chatList[pos].text = originalTextMap[content.messageId].toString()
                        notifyItemChanged(pos)
                    },
                    onClick = {clickChat(originalTextMap[content.messageId].toString())}
                )
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

}