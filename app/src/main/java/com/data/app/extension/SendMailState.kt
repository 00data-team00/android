package com.data.app.extension

import com.data.app.data.response_dto.ResponseRegisterDto

sealed class SendMailState {
    data object Loading:SendMailState()
    data class Success(val response:ResponseRegisterDto):SendMailState()
    data class Error(val message:String):SendMailState()
}