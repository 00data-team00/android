package com.data.app.presentation.main.home.ai_practice.previous_practice

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.data.app.R
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousChatMessagesDto
import com.data.app.data.response_dto.home.ai.ResponseAIPreviousRecordsDto
import com.data.app.databinding.ItemPreviousPracticeBinding
import com.data.app.presentation.main.community.PostsAdapter
import timber.log.Timber
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PreviousPracticeAdapter(
    private val showChatMessages: (Int) -> Unit,
    private val stopChat: () -> Unit,
    private val clickChat: (String) -> Unit,
    private val request: (Int) -> Unit,
    private val change: (Int, Int) -> Unit,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val VIEW_TYPE_SHIMMER = 0
        private const val VIEW_TYPE_NORMAL = 1
    }

    private var practiceRecordsList = mutableListOf<ResponseAIPreviousRecordsDto.ChatRoom>()
    private var selectedPosition: Int? = null
    private var isListLoading = true
    private var isChatLoading = true
    private val practiceChatMessagesMap =
        mutableMapOf<Int, List<ResponseAIPreviousChatMessagesDto.Message>>()

    private val childAdapterMap = mutableMapOf<Int, PreviousPracticeChatAdapter>() // key: 상위 아이템 ID
    private val messageToChatRoomMap = mutableMapOf<Int, Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SHIMMER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_previous_practice_shimmer, parent, false)
            ShimmerViewHolder(view)
        } else {
            val binding = ItemPreviousPracticeBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
            PreviousPracticeViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = if (isListLoading) 5 else practiceRecordsList.size

    override fun getItemViewType(position: Int): Int {
        return if (isListLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is PreviousPracticeViewHolder) {
            holder.bind(practiceRecordsList[position])
        }
    }

    fun setListLoading(loading: Boolean) {
        isListLoading = loading
        notifyDataSetChanged()
    }

    fun updateList(list: List<ResponseAIPreviousRecordsDto.ChatRoom>) {
        practiceRecordsList = list.toMutableList()
        notifyDataSetChanged()
    }

    fun getRecordsList(list: List<ResponseAIPreviousRecordsDto.ChatRoom>) {
        Timber.d("getRecordsList")
        practiceRecordsList.clear()
        practiceRecordsList.addAll(list)
        notifyDataSetChanged()
    }

    fun getMessages(chatRoomId: Int, list: List<ResponseAIPreviousChatMessagesDto.Message>) {
        practiceChatMessagesMap[chatRoomId] = list
        list.forEach { message ->
            messageToChatRoomMap[message.messageId] = chatRoomId
        }

        // chatRoomId에 해당하는 position 찾기
        val pos = practiceRecordsList.indexOfFirst { it.chatRoomId == chatRoomId }
        if (pos != -1) {
            notifyItemChanged(pos)
        }
        /* selectedPosition?.let { pos ->
             // 메시지 맵에 저장
             Timber.d("list: $list")
             practiceChatMessagesMap[chatRoomId] = list
             notifyItemChanged(pos)
         }*/
    }

    inner class ShimmerViewHolder(view: View) : RecyclerView.ViewHolder(view)

    inner class PreviousPracticeViewHolder(private val binding: ItemPreviousPracticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: ResponseAIPreviousRecordsDto.ChatRoom) {
            with(binding) {
                tvTitle.text = data.title
                tvSummation.text = data.description
                tvDate.text = formatToDateOnly(data.createdAt)
                tvType.text = "일상/대화"

                val chatAdapter = PreviousPracticeChatAdapter(
                    clickChat = { chat -> clickChat(chat) },
                    request = { id -> request(id) },
                    change = { pos, id -> change(pos, id) }
                )
                rvChat.adapter = chatAdapter

                // chatRoomId로 메시지 있는지 확인
                val messageList = practiceChatMessagesMap[data.chatRoomId]
                if (messageList != null) {
                    chatAdapter.getList(messageList)
                    if (messageList.isNotEmpty()) chatAdapter.setChatLoading()
                }

                btnArrow.isSelected = data.isExpanded
                /*if (messageList != null) {
                    chatAdapter.getList(messageList)
                    if (messageList.isNotEmpty()) chatAdapter.setChatLoading()
                    if(data.isExpanded){
                        clChatContent.visibility = View.VISIBLE
                        clChatContent.alpha = 0f
                        clChatContent.translationY = -30f
                        clChatContent.animate()
                            .alpha(1f)
                            .translationY(0f)
                            .setDuration(300)
                            .start()
                    }
                }*/

                childAdapterMap[data.chatRoomId] = chatAdapter
            }
            showChats(data.chatRoomId, data.isExpanded)

            binding.itemPreviousPractice.setOnClickListener {
                data.isExpanded = !data.isExpanded
                notifyItemChanged(adapterPosition)
                //binding.btnArrow.isSelected = data.isExpanded
            }

            //showChats(data.chatRoomId)
        }

        private fun showChats(chatRoomId: Int, isExpanding: Boolean) {
            Timber.d("isExpanding: ${isExpanding}")
            with(binding) {
                if (isExpanding) {
                    Timber.d("나타남")
                    selectedPosition = bindingAdapterPosition // 현재 position 저장
                    Timber.d("chatRoomid: $chatRoomId")
                    showChatMessages(chatRoomId) // ViewModel에게 요청

                    // 아래로 슬라이드 (보이게)
                    clChatContent.visibility = View.VISIBLE
                    clChatContent.alpha = 0f
                    clChatContent.translationY = -30f
                    clChatContent.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .start()
                    Timber.d("clchatcontent visibility: ${clChatContent.visibility}")
                } else {
                    // 위로 슬라이드 (사라지게)
                    Timber.d("사라짐")
                    clChatContent.animate().cancel()
                    clChatContent.animate()
                        .alpha(0f)
                        .translationY(-30f)
                        .setDuration(300)
                        .withEndAction {
                            clChatContent.visibility = View.GONE
                        }
                        .start()
                    stopChat()
                    Timber.d("clchatcontent visibility: ${clChatContent.visibility}")
                }
            }
        }

        private fun formatToDateOnly(isoString: String): String {
            return try {
                val localDateTime = LocalDateTime.parse(isoString)
                localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-ddt "))
            } catch (e: Exception) {
                "-"
            }
        }
    }

    fun translateChildChat(messageId: Int, position: Int, translated: String) {
        val childAdapter = childAdapterMap[messageToChatRoomMap[messageId]]
        Log.d("TRANSLATE", childAdapterMap[messageToChatRoomMap[messageId]].toString())
        childAdapter?.translatePosition(position, translated)
    }
}