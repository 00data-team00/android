package com.data.app.presentation.home.ai_practice

import androidx.lifecycle.ViewModel
import com.data.app.R
import com.data.app.data.AIPractice

class AIPracticeViewModel:ViewModel() {
    val mockDailyList = listOf(
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title daily", "subtitle"),
    )

    val mockCultureList =  listOf(
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title culture", "subtitle"),
    )

    val mockJobList =  listOf(
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
        AIPractice(R.drawable.bg_gray, "title job", "subtitle"),
    )
}