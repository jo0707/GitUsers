package com.example.gitusers.utility

class FullUser (
    val id:             Int,
    val name:           String,
    val username:       String,
    val location:       String,
    val repository:     Int,
    val company:        String,
    val followersUrl:   String,
    val followingUrl:   String,
    val followers:      Int,
    val following:      Int,
    val imageUrl:       String
) {
    companion object {
        val dummy   = FullUser(
            id          = 0 ,
            name        = "",
            username    = "",
            location    = "",
            repository  = 0,
            company     = "",
            followersUrl= "",
            followingUrl= "",
            followers   = 0,
            following   = 0,
            imageUrl    = ""
        )
    }
}