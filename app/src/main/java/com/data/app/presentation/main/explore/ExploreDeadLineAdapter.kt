package com.data.app.presentation.main.explore

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.data.DeadLine
import com.data.app.databinding.ItemDeadlineBinding

class ExploreDeadLineAdapter:RecyclerView.Adapter<ExploreDeadLineAdapter.ExploreDeadLineViewHolder>() {
    private val deadlineList = mutableListOf<DeadLine>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreDeadLineViewHolder {
        val binding=ItemDeadlineBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreDeadLineViewHolder(binding)
    }

    override fun getItemCount(): Int = deadlineList.size

    override fun onBindViewHolder(holder: ExploreDeadLineViewHolder, position: Int) {
        holder.bind(deadlineList[position])

        val layoutParams = holder.itemView.layoutParams as ViewGroup.MarginLayoutParams
        val defaultMarginStart = 5.dp(holder.itemView.context)
        val defaultMarginEnd = 5.dp(holder.itemView.context)

        layoutParams.marginStart = if (position == 0) 13.dp(holder.itemView.context) else defaultMarginStart
        layoutParams.marginEnd = if (position == deadlineList.lastIndex) 13.dp(holder.itemView.context) else defaultMarginEnd

        holder.itemView.layoutParams = layoutParams
    }

    fun getList(list:List<DeadLine>){
        deadlineList.clear()
        deadlineList.addAll(list)
        notifyDataSetChanged()
    }

    private fun Int.dp(context: android.content.Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    inner class ExploreDeadLineViewHolder(private val binding:ItemDeadlineBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data:DeadLine){
            with(binding){
                ivImage.load(data.image)
                tvDeadlineDate.text=data.deadline
                tvPrice.text=data.price
                tvTitle.text=data.title
                tvAddress.text=data.address
            }
        }
    }
}