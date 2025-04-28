package com.data.app.presentation.home.game.word

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.data.app.data.Quiz
import com.data.app.databinding.ItemGameAnswerBinding

class GameQuizAnswerAdapter(
    val clickAnswer:(Boolean)->Unit,
):RecyclerView.Adapter<GameQuizAnswerAdapter.GameQuizAnswerViewHolder>() {
    private val answers = mutableListOf<Quiz.Word.Answer>()
    private var selectedPosition: Int = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameQuizAnswerViewHolder {
       val binding=ItemGameAnswerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameQuizAnswerViewHolder(binding)
    }

    override fun getItemCount(): Int = answers.size
    override fun onBindViewHolder(holder: GameQuizAnswerViewHolder, position: Int) {
        holder.bind(answers[position])
    }

    fun getList(list:List< Quiz.Word.Answer>){
        answers.clear()
        answers.addAll(list)
        notifyDataSetChanged()
    }

    inner class GameQuizAnswerViewHolder(private val binding: ItemGameAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer:  Quiz.Word.Answer) {
            binding.btnAnswer.text = answer.answer

            // 버튼의 선택 상태를 position과 selectedPosition을 비교해서 설정
            binding.btnAnswer.isSelected = adapterPosition == selectedPosition

            binding.btnAnswer.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = adapterPosition
                clickAnswer(answer.isCorrect)

                // 선택이 바뀌었으니까 두 위치를 갱신해줘야 함
                if (previousPosition != RecyclerView.NO_POSITION) {
                    notifyItemChanged(previousPosition)
                }
                notifyItemChanged(selectedPosition)
            }
        }
    }
}