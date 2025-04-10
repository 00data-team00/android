package com.data.app.presentation.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.data.app.data.Language
import com.data.app.databinding.ItemLanguageBinding

class LanguageAdapter(
    private val onItemClick: (Language) -> Unit
) : RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    private val languageList = mutableListOf<Language>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemLanguageBinding.inflate(inflater, parent, false)
        return LanguageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        holder.bind(languageList[position])
    }

    override fun getItemCount(): Int = languageList.size

    fun getList(list:List<Language>){
        languageList.clear()
        languageList.addAll(list)
        notifyDataSetChanged()
    }

    inner class LanguageViewHolder(val binding: ItemLanguageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Language) {
            binding.ivFlag.setImageResource(item.iconRes)
            binding.tvLanguage.text = item.name
            binding.root.setOnClickListener { onItemClick(item) }
        }
    }
}
