package com.carlosribeiro.reelcine.data.remote.api

import com.carlosribeiro.reelcine.data.remote.dto.MovieDto
import com.carlosribeiro.reelcine.data.remote.dto.MovieListResponse
import com.carlosribeiro.reelcine.data.remote.dto.VideoListResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(): MovieListResponse

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovies(): MovieListResponse

    @GET("movie/top_rated")
    suspend fun getTopRatedMovies(): MovieListResponse

    @GET("movie/upcoming")
    suspend fun getUpcomingMovies(): MovieListResponse

    @GET("search/movie")
    suspend fun searchMovies(@Query("query") query: String): MovieListResponse

    @GET("movie/{movieId}")
    suspend fun getMovieDetail(@Path("movieId") movieId: Int): MovieDto

    @GET("movie/{movieId}/videos")
    suspend fun getMovieVideos(@Path("movieId") movieId: Int): VideoListResponse
}
