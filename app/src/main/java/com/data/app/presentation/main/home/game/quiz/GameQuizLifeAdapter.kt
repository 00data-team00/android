package com.data.app.presentation.main.home.game.quiz

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.animation.addListener
import androidx.recyclerview.widget.RecyclerView
import coil3.load
import com.data.app.R
import com.data.app.databinding.ItemLifeBinding
import timber.log.Timber

class GameQuizLifeAdapter(private val recyclerView: RecyclerView) :
    RecyclerView.Adapter<GameQuizLifeAdapter.GameQuizLifeViewHolder>() {
    private var currentVisibleLifeCount = 3
    private val blinkDuration = 150L // 각 깜빡임(on/off) 지속 시간
    private val fadeOutDuration = 300L // 최종 fade-out 지속 시간
    private val blinkCount = 2

    /* fun updateLifeCount(lifeCount: Int) {
         this.lifeCount = lifeCount
         notifyDataSetChanged() // 간단하게 전체 새로고침 (애니메이션 등은 추후 개선 가능)
     }*/

    fun removeOneLife() {
        if (currentVisibleLifeCount > 0) {
            val positionToRemove = currentVisibleLifeCount - 1
            val viewHolder =
                recyclerView.findViewHolderForAdapterPosition(positionToRemove) as? GameQuizLifeViewHolder

            if (viewHolder != null) {
                // 중복 실행 방지
                if (viewHolder.itemView.tag is AnimatorSet && (viewHolder.itemView.tag as AnimatorSet).isRunning) {
                    Timber.w("Animation already running for position $positionToRemove. Ignoring new request.")
                    return
                }

                Timber.d("Animating blink and fade out for item at position $positionToRemove")

                val itemView = viewHolder.itemView
                val animatorList = mutableListOf<Animator>()

                // 1. 깜빡임 애니메이션 (alpha: 1 -> 0 -> 1 -> 0 ...)
                for (i in 0 until blinkCount) {
                    val blinkOff = ObjectAnimator.ofFloat(itemView, "alpha", 1f, 0f).apply {
                        duration = blinkDuration
                    }
                    val blinkOn = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 1f).apply {
                        duration = blinkDuration
                    }
                    animatorList.add(blinkOff)
                    animatorList.add(blinkOn)
                }

                // 2. 최종 Fade-out 애니메이션 (alpha: 1 -> 0)
                // 마지막 blinkOn 후에 alpha가 1이므로, 1에서 0으로 fadeOut
                val fadeOut = ObjectAnimator.ofFloat(itemView, "alpha", 1f, 0f).apply {
                    duration = fadeOutDuration
                }
                animatorList.add(fadeOut)

                // AnimatorSet으로 애니메이션들을 순차적으로 실행
                AnimatorSet().apply {
                    playSequentially(animatorList)
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            Timber.d("Blink and fade out animation ENDED for position $positionToRemove. Performing remove.")
                            itemView.tag=null
                            performRemoveItem(positionToRemove)
                        }

                        override fun onAnimationStart(animation: Animator) {
                            Timber.d("Blink and fade out animation STARTED for position $positionToRemove.")
                            itemView.tag = this // 현재 애니메이션을 태그로 저장하여 중복 실행 방지용으로 사용할 수 있음
                        }

                        override fun onAnimationCancel(animation: Animator) {
                            Timber.d("Blink and fade out animation CANCELED for position $positionToRemove.")
                            itemView.tag=null
                            // 애니메이션 취소 시 alpha 값을 원래대로 돌리거나, 즉시 제거 로직 실행
                            itemView.alpha = 1f // 예시: 취소 시 다시 보이게
                            // 또는 performRemoveItem(positionToRemove, true) // 즉시 제거 플래그 전달
                        }
                    })
                    start()
                }
            } else {
                Timber.w("ViewHolder not found at position $positionToRemove for blink/fade. Removing item directly.")
                performRemoveItem(positionToRemove)
            }
        } else {
            Timber.w("removeOneLifeWithBlinkFadeOut called but no lives left.")
        }
    }

    private fun performRemoveItem(positionToRemove: Int) {
        if (currentVisibleLifeCount > 0 && positionToRemove == currentVisibleLifeCount - 1) {
            currentVisibleLifeCount--
            notifyDataSetChanged() // 첫 번째 아이템 제거 알림
            Timber.d("Item removed from adapter. New count: $currentVisibleLifeCount")

            // 첫 번째 아이템이 제거된 후, 나머지 아이템들의 위치가 변경되었으므로
            // 이들의 뷰를 올바르게 갱신하기 위해 notifyItemRangeChanged를 호출해볼 수 있음.
            // 하지만 이것이 Inconsistency 오류를 유발할 수 있으므로 주의.
            // 만약 이 호출로 인해 다시 오류가 발생한다면, 이 줄은 제거해야 함.
           /* if (currentVisibleLifeCount > 0) {
                // RecyclerView가 이전 제거 작업을 처리할 시간을 주기 위해 post 사용 시도
                recyclerView.post {
                    if (itemCount == currentVisibleLifeCount) { // 어댑터의 itemCount와 내부 count가 일치할 때만
                        notifyItemRangeChanged(0, currentVisibleLifeCount)
                        Timber.d("Notified item range changed from 0 to $currentVisibleLifeCount after post")
                    } else {
                        Timber.w("performRemoveItem post: itemCount ($itemCount) != currentVisibleLifeCount ($currentVisibleLifeCount). Skipping notifyItemRangeChanged.")
                    }
                }
            }*/
        } else {
            Timber.w("performRemoveItem called when currentVisibleLifeCount is already 0.")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameQuizLifeViewHolder {
        val binding = ItemLifeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GameQuizLifeViewHolder(binding)
    }

    override fun getItemCount(): Int = currentVisibleLifeCount

    override fun onBindViewHolder(holder: GameQuizLifeViewHolder, position: Int) {
        holder.itemView.alpha = 1f
        holder.bind()
    }

    inner class GameQuizLifeViewHolder(private val binding: ItemLifeBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            (itemView.tag as? ObjectAnimator)?.cancel() // 이전 애니메이션이 있다면 취소
            itemView.alpha = 1f // ViewHolder가 바인딩될 때 alpha를 1로 설정

            binding.ivLife.load(R.drawable.ic_life_red_19)
        }
    }
}