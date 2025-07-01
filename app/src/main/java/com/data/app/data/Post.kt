package com.data.app.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Post (
    val profile:String,
    val id:String,
    val time:Int,
    val isFollowing:Boolean,
    val content:String,
    val images:List<Int>?,
    val like:Int,
    val comments:List<Comments>
):Parcelable{
    @Parcelize
    data class Comments(
        val profile:String,
        val name:String,
        val content:String,
        val like:Int,
    ):Parcelable
}