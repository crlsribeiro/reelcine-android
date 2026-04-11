package com.carlosribeiro.reelcine.domain.model

data class WatchlistItem(
    val id: String = "",
    val movieId: Int = 0,
    val movieTitle: String = "",
    val posterPath: String = "",
    val backdropPath: String = "",
    val voteAverage: Double = 0.0,
    val userId: String = "",
    val addedAt: Long = 0L
)
