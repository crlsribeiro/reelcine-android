package com.crlsribeiro.reelcine.data.remote.dto

import com.google.gson.annotations.SerializedName

data class MovieDto(
    val id: Int,
    val title: String,
    val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("vote_average") val voteAverage: Double,
    @SerializedName("vote_count") val voteCount: Int,
    val runtime: Int? = null,
    val genres: List<GenreDto>? = null
)

data class GenreDto(
    val id: Int,
    val name: String
)

data class MovieListResponse(
    val results: List<MovieDto>
)
