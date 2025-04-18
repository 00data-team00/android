package com.data.app.presentation.home.ai_practice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.data.AIPractice
import com.data.app.databinding.ItemAiPracticeBinding

class AIPracticeAdapter : RecyclerView.Adapter<AIPracticeAdapter.AIPracticeViewHolder>() {

    private val aiPracticeList = mutableListOf<AIPractice>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AIPracticeViewHolder {
        val binding=ItemAiPracticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AIPracticeViewHolder(binding)
    }

    override fun getItemCount(): Int = aiPracticeList.size

    override fun onBindViewHolder(holder: AIPracticeViewHolder, position: Int) {
        holder.bind(aiPracticeList[position])
    }

    fun getList(list:List<AIPractice>){
        aiPracticeList.clear()
        aiPracticeList.addAll(list)
        notifyDataSetChanged()
    }

    inner class AIPracticeViewHolder(private val binding: ItemAiPracticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: AIPractice) {
            with(binding) {
                ivBackground.load(item.image)
                tvTitle.text = item.title
                tvSubTitle.text = item.subTitle
            }
        }
    }
}