package com.data.app.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken:LiveData<String> get()=_accessToken

    private var userId:Int?=null

    fun saveToken(token:String){
        _accessToken.value=token
    }

    fun saveUserId(id:Int){
        userId=id
    }
}