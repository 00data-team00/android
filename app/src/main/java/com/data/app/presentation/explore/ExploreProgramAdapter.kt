package com.data.app.presentation.explore

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.data.Program
import com.data.app.databinding.ItemAllProgramBinding
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExploreProgramAdapter:RecyclerView.Adapter<ExploreProgramAdapter.ExploreProgramViewHolder>() {
    private val programList = mutableListOf<Program>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreProgramViewHolder {
        val binding=ItemAllProgramBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExploreProgramViewHolder(binding)
    }

    override fun getItemCount(): Int = programList.size

    override fun onBindViewHolder(holder: ExploreProgramViewHolder, position: Int) {
        holder.bind(programList[position])
    }

    fun getList(list: List<Program>){
        Timber.d("program get list")
        programList.clear()
        programList.addAll(list)
        notifyDataSetChanged()
    }

    inner class ExploreProgramViewHolder(private val binding:ItemAllProgramBinding):
        RecyclerView.ViewHolder(binding.root){
        fun bind(data:Program){
            binding.ivImage.load(data.image)
            binding.tvTitle.text=data.title
            binding.tvDate.text=data.date

            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                val programDate = sdf.parse(data.date)
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