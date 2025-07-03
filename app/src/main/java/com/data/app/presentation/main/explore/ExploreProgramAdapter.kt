package com.data.app.presentation.main.explore

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.data.app.R
import com.data.app.data.response_dto.explore.ResponseAllProgramDto
import com.data.app.databinding.ItemAllProgramBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExploreProgramAdapter(
    val clickProgram:(String)->Unit,
):RecyclerView.Adapter<ExploreProgramAdapter.ExploreProgramViewHolder>() {
    private val programList = mutableListOf<ResponseAllProgramDto.ProgramDto>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreProgramViewHolder {
        val binding=ItemAllProgramBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreProgramViewHolder(binding)
    }

    override fun getItemCount(): Int = programList.size

    override fun onBindViewHolder(holder: ExploreProgramViewHolder, position: Int) {
        holder.bind(programList[position])
    }

    fun addPrograms(newList: List<ResponseAllProgramDto.ProgramDto>) {
        val startPos = programList.size
        programList.addAll(newList)
        notifyItemRangeInserted(startPos, newList.size)
    }

    fun replacePrograms(newList: List<ResponseAllProgramDto.ProgramDto>) {
        programList.clear()
        programList.addAll(newList)
        notifyDataSetChanged()
    }

    inner class ExploreProgramViewHolder(private val binding:ItemAllProgramBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data: ResponseAllProgramDto.ProgramDto){
            binding.ivImage.load(data.thumbnailUrl){
                placeholder(R.drawable.ic_all_program)
                error(R.drawable.ic_all_program)
                fallback(R.drawable.ic_all_program)
            }
            binding.tvTitle.text=data.titleNm
            binding.tvDate.text=data.appEndDate

            binding.itemProgram.setOnClickListener{
                clickProgram(data.appLink)
            }

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val programDate = sdf.parse(data.appEndDate)
                val today = Calendar.getInstance().time

                if (programDate != null && programDate.before(today)) {
                    // 흑백 필터 적용
                    val matrix = ColorMatrix()
                    matrix.setSaturation(0f) // 0 = 흑백
                    binding.cvProgram.alpha = 0.5f
                    val filter = ColorMatrixColorFilter(matrix)
                    binding.ivImage.colorFilter = filter
                }else{
                    binding.ivImage.clearColorFilter()
                    binding.cvProgram.alpha = 1f
                }
            } catch (e: Exception) {
                Timber.e("날짜 파싱 오류: ${e.message}")
            }
        }
    }
}