package com.data.app.presentation.main.home.game

import androidx.lifecycle.ViewModel
import com.data.app.data.GameLevel
import com.data.app.data.Week

class GameTabViewModel:ViewModel() {
    val weeks = listOf(
        Week("월", false),
        Week("화", false),
        Week("수", false),
        Week("목", true),
        Week("금", true),
        Week("토", false),
        Week("일", false),
    )

    val levels= listOf(
        GameLevel("LV.1", true),
        GameLevel("LV.2", false),
        GameLevel("LV.3", false),
        GameLevel("LV.4", false),
        GameLevel("LV.5", false),
        GameLevel("LV.6", false),
        GameLevel("LV.7", false),
        GameLevel("LV.8", false),
        GameLevel("LV.9", false),
        GameLevel("LV.10", false),
    )
}