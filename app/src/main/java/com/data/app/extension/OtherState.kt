package com.data.app.extension

import com.data.app.data.Post

sealed class OtherState {
    data object Loading:OtherState()
    data class Success(val response:List<Post>):OtherState()
    data class Error(val message:String):OtherState()
}