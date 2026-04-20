package com.crlsribeiro.reelcine.domain.repository

import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.domain.model.Video

interface MovieRepository {
    suspend fun getTrendingMovies(): Result<List<Movie>>
    suspend fun getTrendingTodayMovies(): Result<List<Movie>>
    suspend fun getNowPlayingMovies(): Result<List<Movie>>
    suspend fun getUpcomingMovies(): Result<List<Movie>>
    suspend fun getPopularMovies(): Result<List<Movie>>
    suspend fun getTopRatedMovies(): Result<List<Movie>>
    suspend fun searchMovies(query: String): Result<List<Movie>>
    suspend fun getMovieDetail(movieId: Int): Result<Movie>
    suspend fun getMovieVideos(movieId: Int): Result<List<Video>>
}
