package com.carlosribeiro.reelcine.domain.model

data class Movie(
    val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val backdropPath: String,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val genres: List<String> = emptyList(),
    val runtime: Int = 0
)
