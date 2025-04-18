package com.data.app.presentation.home.ai_practice.previous_practice

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.data.app.data.PreviousPractice
import com.data.app.databinding.ItemPreviousPracticeBinding
import timber.log.Timber

class PreviousPracticeAdapter(
    private val clickChat:(String)->Unit
):RecyclerView.Adapter<PreviousPracticeAdapter.PracticeRecordsViewHolder>(){

    private val practiceRecordsList = mutableListOf<PreviousPractice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PracticeRecordsViewHolder {
       val binding=ItemPreviousPracticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PracticeRecordsViewHolder(binding)
    }

    override fun getItemCount(): Int = practiceRecordsList.size

    override fun onBindViewHolder(holder: PracticeRecordsViewHolder, position: Int) {
        holder.bind(practiceRecordsList[position])
    }

    fun getRecordsList(list: List<PreviousPractice>){
        Timber.d("getRecordsList")
        practiceRecordsList.clear()
        practiceRecordsList.addAll(list)
        notifyDataSetChanged()
    }

    inner class PracticeRecordsViewHolder(private val binding:ItemPreviousPracticeBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data:PreviousPractice){
            with(binding){
                tvTitle.text=data.title
                tvSummation.text=data.summation
                tvDate.text=data.date
                tvType.text=data.type

                val chatAdapter=PreviousPracticeChatAdapter{
                    chat->clickChat(chat)
                }
                rvChat.adapter=chatAdapter
                chatAdapter.getList(data.chatList)

                clChatContent.visibility = View.GONE
                btnArrow.isSelected = false
            }

            showChats()
        }

        private fun showChats(){
            binding.itemPreviousPractice.setOnClickListener {
                val isExpanding = !binding.btnArrow.isSelected
                binding.btnArrow.isSelected = isExpanding

                with(binding){
                    if (isExpanding) {
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
    }
}