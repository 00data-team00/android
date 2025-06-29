package com.data.app.data

sealed class Quiz {
    data class Word (
        val question:String,
        val image:Int,
        val answer:List<Answer>
    ){
        data class Answer(
            val answer:String,
            val isCorrect:Boolean,
        )
    }

    data class Listening(
        val question:String,
        val text:String,
        val answer:List<Word.Answer>
    )
}