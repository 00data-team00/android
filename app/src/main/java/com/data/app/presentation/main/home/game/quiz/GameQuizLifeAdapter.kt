package com.data.app.presentation.main.home.game.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.R
import com.data.app.databinding.ItemLifeBinding

class GameQuizLifeAdapter:RecyclerView.Adapter<GameQuizLifeAdapter.GameQuizLifeViewHolder>() {
    private var lifeCount = 3

    fun updateLifeCount(lifeCount: Int) {
        this.lifeCount = lifeCount
        notifyDataSetChanged() // 간단하게 전체 새로고침 (애니메이션 등은 추후 개선 가능)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameQuizLifeViewHolder {
        val binding = ItemLifeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameQuizLifeViewHolder(binding)
    }

    override fun getItemCount(): Int = lifeCount

    override fun onBindViewHolder(holder: GameQuizLifeViewHolder, position: Int) {
        holder.bind()
    }

    inner class GameQuizLifeViewHolder(private val binding:ItemLifeBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(){
            binding.ivLife.load(R.drawable.ic_life_red_19)
        }
    }
}