package com.data.app.extension

import com.data.app.data.Post

sealed class MyState {
    data object Loading:MyState()
    data class Success(val response:List<Post>):MyState()
    data class Error(val message:String):MyState()
}