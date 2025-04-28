package com.data.app.presentation.home.game.word

import androidx.lifecycle.ViewModel
import com.data.app.R
import com.data.app.data.Quiz

class GameQuizViewModel:ViewModel() {
    val quiz = listOf(
        Quiz.Word(
            "문제 1", "그림에 맞는 단어를 선택해주세요!", R.drawable.ic_question, listOf(
                Quiz.Word.Answer("보이스피싱", true),
                Quiz.Word.Answer("로그인", false),
                Quiz.Word.Answer("리모컨", false),
                Quiz.Word.Answer("계약", false),
            )
        ),
        Quiz.Listening(
            "문제 2", "음성을 듣고 해당하는 단어의\n뜻을 선택해주세요.", "보이스피싱", listOf(
                Quiz.Word.Answer("보이스피싱", true),
                Quiz.Word.Answer("로그인", false),
                Quiz.Word.Answer("리모컨", false),
                Quiz.Word.Answer("계약", false),
            )
        ),
        Quiz.Word(
            "문제 3", "그림에 맞는 단어를 선택해주세요!", R.drawable.ic_question, listOf(
                Quiz.Word.Answer("보이스피싱", true),
                Quiz.Word.Answer("로그인", false),
                Quiz.Word.Answer("리모컨", false),
                Quiz.Word.Answer("계약", false),
            )
        ),
        Quiz.Listening(
            "문제 4", "음성을 듣고 해당하는 단어의\n뜻을 선택해주세요.", "로그인", listOf(
                Quiz.Word.Answer("보이스피싱", false),
                Quiz.Word.Answer("로그인", true),
                Quiz.Word.Answer("리모컨", false),
                Quiz.Word.Answer("계약", false),
            )
        ),
    )
}