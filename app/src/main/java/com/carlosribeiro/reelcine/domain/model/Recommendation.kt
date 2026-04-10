package com.carlosribeiro.reelcine.domain.model

data class Recommendation(
    val id: String = "",
    val movieId: Int = 0,
    val movieTitle: String = "",
    val posterPath: String = "",
    val backdropPath: String = "",
    val comment: String = "",
    val rating: Float = 0f,
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val groupId: String = "",
    val timestamp: Long = 0L,
    val likes: List<String> = emptyList(),
    val likesCount: Int = 0
)
