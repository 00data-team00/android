package com.data.app.presentation.home.game

import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.data.app.data.GameLevel
import com.data.app.databinding.ItemGamePrograssBinding

class GameTabLevelAdapter:RecyclerView.Adapter<GameTabLevelAdapter.GameTabLevelViewHolder>() {
    private val levels = mutableListOf<GameLevel>()
    private var recyclerViewWidth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameTabLevelViewHolder {
        val binding=ItemGamePrograssBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameTabLevelViewHolder(binding)
    }

    override fun getItemCount(): Int = levels.size

    override fun onBindViewHolder(holder: GameTabLevelViewHolder, position: Int) {
       holder.bind(levels[position])
    }

    fun getList(list: List<GameLevel>){
        levels.clear()
        levels.addAll(list)
        notifyDataSetChanged()
    }

    fun getRecyclerViewWidth(width:Int){
        recyclerViewWidth=width
        notifyDataSetChanged()
    }

    inner class GameTabLevelViewHolder(private val binding:ItemGamePrograssBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(level:GameLevel){
            binding.tvLevel.text=level.level
            binding.btnLevel.isSelected=level.isComplete

            val params = binding.root.layoutParams as ViewGroup.MarginLayoutParams

            // 한 아이템의 가로 크기 (RecyclerView 전체의 1/3정도로 본다)
            val itemWidth = recyclerViewWidth / 3

            // 먼저 아이템 자체 width 조정
            params.width = itemWidth

            when (position % 4) {
                0 -> { // 0, 4, 8, 12번째: 오른쪽
                    binding.root.updateLayoutParams<RecyclerView.LayoutParams> {
                        marginStart = recyclerViewWidth * 2 / 3
                    }
                }
                1, 3 -> { // 1, 3, 5, 7번째: 가운데
                    binding.root.updateLayoutParams<RecyclerView.LayoutParams> {
                        marginStart = recyclerViewWidth / 3
                    }
                }
                2 -> { // 2, 6, 10번째: 왼쪽
                    binding.root.updateLayoutParams<RecyclerView.LayoutParams> {
                        marginStart = 0
                    }
                }
            }

            binding.root.layoutParams = params
        }
    }
}