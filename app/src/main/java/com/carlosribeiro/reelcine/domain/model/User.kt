package com.carlosribeiro.reelcine.domain.model

data class User(
    val uid: String,
    val name: String,
    val email: String,
    val avatarUrl: String = "",
    val bio: String = ""
)
