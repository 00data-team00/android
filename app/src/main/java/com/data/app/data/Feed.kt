package com.data.app.data

data class Feed (
    val profile:Int,
    val name:String,
    val time:Int,
    val isFollowing:Boolean,
    val content:String,
    val image:Int,
    val like:Int,
    val comments:List<Comments>
){
    data class Comments(
        val profile:Int,
        val name:String,
        val content:String,
        val like:Int,
    )
}