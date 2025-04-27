package com.data.app.presentation.home.game

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.data.app.R
import com.data.app.data.Week
import com.data.app.databinding.ItemGameWeekBinding

class GameTabWeekAdapter:RecyclerView.Adapter<GameTabWeekAdapter.GameTabWeekViewHolder>() {
    private val weeks = mutableListOf<Week>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameTabWeekViewHolder {
        val binding=ItemGameWeekBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameTabWeekViewHolder(binding)
    }

    override fun getItemCount(): Int = weeks.size
    override fun onBindViewHolder(holder: GameTabWeekViewHolder, position: Int) {
        holder.bind(weeks[position])
    }

    fun getList(list: List<Week>){
        weeks.clear()
        weeks.addAll(list)
        notifyDataSetChanged()
    }

    inner class GameTabWeekViewHolder(private val binding:ItemGameWeekBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(week:Week){
            binding.tvDay.text=week.day
            binding.ivFlag.isSelected=week.flag

            if (week.flag) {
                binding.tvDay.setTextColor(ContextCompat.getColor(binding.root.context, R.color.game_flag_red))
            } else {
                binding.tvDay.setTextColor(ContextCompat.getColor(binding.root.context, R.color.game_flag_black))
            }
        }
    }
}