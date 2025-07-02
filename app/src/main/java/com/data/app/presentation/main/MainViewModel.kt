package com.data.app.presentation.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel:ViewModel() {
    private val _accessToken = MutableLiveData<String>()
    val accessToken:LiveData<String> get()=_accessToken

    fun saveToken(token:String){
        _accessToken.value=token
    }
}