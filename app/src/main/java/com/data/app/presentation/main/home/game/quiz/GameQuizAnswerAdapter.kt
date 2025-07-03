package com.data.app.presentation.main.home.game.quiz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.data.app.data.Quiz
import com.data.app.data.response_dto.home.quiz.ResponseQuizDto
import com.data.app.databinding.ItemGameAnswerBinding

class GameQuizAnswerAdapter(
    val clickAnswer:(Int)->Unit,
):RecyclerView.Adapter<GameQuizAnswerAdapter.GameQuizAnswerViewHolder>() {
    private val answers = mutableListOf<ResponseQuizDto.QuizDto.ChoiceDto>()
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameQuizAnswerViewHolder {
       val binding=ItemGameAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameQuizAnswerViewHolder(binding)
    }

    override fun getItemCount(): Int = answers.size
    override fun onBindViewHolder(holder: GameQuizAnswerViewHolder, position: Int) {
        holder.bind(answers[position])
    }

    fun getList(list:List<ResponseQuizDto.QuizDto.ChoiceDto>){
        answers.clear()
        answers.addAll(list)
        notifyDataSetChanged()
    }

    inner class GameQuizAnswerViewHolder(private val binding: ItemGameAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: ResponseQuizDto.QuizDto.ChoiceDto) {
            binding.btnAnswer.text = answer.word

            // 버튼의 선택 상태를 position과 selectedPosition을 비교해서 설정
            binding.btnAnswer.isSelected = adapterPosition == selectedPosition

            binding.btnAnswer.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                clickAnswer(position+1)

                // 선택이 바뀌었으니까 두 위치를 갱신해줘야 함
                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition)
                }
                notifyItemChanged(selectedPosition)
            }
        }
    }
}