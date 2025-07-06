package com.data.app.presentation.main.home.ai_practice.previous_practice

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
    private val clickChat: (String) -> Unit
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


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType==VIEW_TYPE_SHIMMER){
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_previous_practice_shimmer, parent, false)
            ShimmerViewHolder(view)
        }else{
            val binding = ItemPreviousPracticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            PreviousPracticeViewHolder(binding)
        }
    }

    override fun getItemCount(): Int = if(isListLoading) 5 else practiceRecordsList.size

    override fun getItemViewType(position: Int): Int {
        return if (isListLoading) VIEW_TYPE_SHIMMER else VIEW_TYPE_NORMAL
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is PreviousPracticeViewHolder){
            holder.bind(practiceRecordsList[position])
        }
    }

    fun setListLoading(loading:Boolean){
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

                val chatAdapter = PreviousPracticeChatAdapter { chat ->
                    clickChat(chat)
                }
                rvChat.adapter = chatAdapter

                // chatRoomId로 메시지 있는지 확인
                val messageList = practiceChatMessagesMap[data.chatRoomId]
                if (messageList != null) {
                    chatAdapter.getList(messageList)
                    if(messageList.isNotEmpty()) chatAdapter.setChatLoading()
                    clChatContent.visibility = View.VISIBLE
                    clChatContent.alpha = 0f
                    clChatContent.translationY = -30f
                    clChatContent.animate()
                        .alpha(1f)
                        .translationY(0f)
                        .setDuration(300)
                        .start()
                } else {
                    clChatContent.visibility = View.GONE
                    btnArrow.isSelected = false
                }

                /* val chatAdapter=PreviousPracticeChatAdapter{
                     chat->clickChat(chat)
                 }
                 rvChat.adapter=chatAdapter
                 chatAdapter.getList(data.chatList)

                 clChatContent.visibility = View.GONE
                 btnArrow.isSelected = false*/
            }

            showChats(data.chatRoomId)
        }

        private fun showChats(chatRoomId: Int) {
            binding.itemPreviousPractice.setOnClickListener {
                val isExpanding = !binding.btnArrow.isSelected
                binding.btnArrow.isSelected = isExpanding

                with(binding) {
                    if (isExpanding) {
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
                    } else {
                        // 위로 슬라이드 (사라지게)
                        clChatContent.animate()
                            .alpha(0f)
                            .translationY(-30f)
                            .setDuration(300)
                            .withEndAction {
                                clChatContent.visibility = View.GONE
                            }
                            .start()
                    }
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
}