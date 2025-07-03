package com.data.app.presentation.main.home.ai_practice

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.R
import com.data.app.data.response_dto.home.ai.ResponseAITopicsDto
import com.data.app.databinding.ItemAiPracticeBinding
import timber.log.Timber

class AIPracticeAdapter(
    private val context: Context,
    private val clickPractice:(ResponseAITopicsDto.TopicDto)->Unit
) : RecyclerView.Adapter<AIPracticeAdapter.AIPracticeViewHolder>() {

    private var category:String = "essential"
    private val aiPracticeList = mutableListOf<ResponseAITopicsDto.TopicDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AIPracticeViewHolder {
        val binding=ItemAiPracticeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AIPracticeViewHolder(binding)
    }

    override fun getItemCount(): Int = aiPracticeList.size

    override fun onBindViewHolder(holder: AIPracticeViewHolder, position: Int) {
        holder.bind(aiPracticeList[position])
    }

    fun getList(list:List<ResponseAITopicsDto.TopicDto>){
        Timber.d("list: $aiPracticeList")
        aiPracticeList.clear()
        aiPracticeList.addAll(list)
        notifyDataSetChanged()
    }

    fun changeCategory(category:String){
        this.category=category
        notifyDataSetChanged()
    }

    inner class AIPracticeViewHolder(private val binding: ItemAiPracticeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ResponseAITopicsDto.TopicDto) {
            with(binding) {
                tvTitle.text ="| ${item.title}"
                tvSubTitle.text = item.description
                tvAiRole.text=item.aiRole
                tvUserRole.text=item.userRole

                when(category){
                    "essential"->{
                        ivBackground.load(R.drawable.bg_necessity_daily)
                        tvAi.setBackgroundResource(R.drawable.bg_text_ai_light_green)
                        tvAi.setTextColor(ContextCompat.getColor(context, R.color.ai_practice_essential_green))
                        tvUser.setBackgroundResource(R.drawable.bg_text_user_green)
                    }
                    "work" -> {
                        ivBackground.load(R.drawable.bg_employment_work)
                        tvAi.setBackgroundResource(R.drawable.bg_text_ai_sky)
                        tvAi.setTextColor(ContextCompat.getColor(context, R.color.ai_practice_employment_blue))
                        tvUser.setBackgroundResource(R.drawable.bg_text_user_blue)
                    }
                    "culture" ->{
                        ivBackground.load( R.drawable.bg_cultural_leisure)
                        tvAi.setBackgroundResource(R.drawable.bg_text_ai_light_orange)
                        tvAi.setTextColor(ContextCompat.getColor(context, R.color.ai_practice_culture_orange))
                        tvUser.setBackgroundResource(R.drawable.bg_text_user_orange)
                    }
                    else -> R.drawable.bg_employment_work
                }

                ivBackground.setOnClickListener{
                    clickPractice(item)
                }
            }
        }
    }
}