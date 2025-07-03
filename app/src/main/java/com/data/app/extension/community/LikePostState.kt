package com.data.app.extension.community

sealed class LikePostState {
    object Loading : LikePostState()
    data class Success(val message: String) : LikePostState()
    data class Error(val message: String) : LikePostState()
}