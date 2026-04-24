package com.crlsribeiro.reelcine.data.repository

import com.crlsribeiro.reelcine.data.remote.api.TmdbApi
import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.domain.model.Video
import com.crlsribeiro.reelcine.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val tmdbApi: TmdbApi
) : MovieRepository {

    override suspend fun getTrendingMovies(): Result<List<Movie>> = runCatching {
        tmdbApi.getTrendingWeek().results.map { it.toMovie() }
    }

    override suspend fun getTrendingTodayMovies(): Result<List<Movie>> = runCatching {
        tmdbApi.getTrendingToday().results.map { it.toMovie() }
    }

    override suspend fun getNowPlayingMovies(): Result<List<Movie>> = runCatching {
        tmdbApi.getNowPlayingMovies().results.map { it.toMovie() }
    }

    override suspend fun getUpcomingMovies(): Result<List<Movie>> = runCatching {
        tmdbApi.getUpcomingMovies().results.map { it.toMovie() }
    }

    override suspend fun getPopularMovies(): Result<List<Movie>> = runCatching {
        tmdbApi.getPopularMovies().results.map { it.toMovie() }
    }

    override suspend fun getTopRatedMovies(): Result<List<Movie>> = runCatching {
        tmdbApi.getTopRatedMovies().results.map { it.toMovie() }
    }

    override suspend fun searchMovies(query: String): Result<List<Movie>> = runCatching {
        tmdbApi.searchMovies(query).results.map { it.toMovie() }
    }

    override suspend fun getMovieDetail(movieId: Int): Result<Movie> = runCatching {
        tmdbApi.getMovieDetail(movieId).toMovie()
    }

    override suspend fun getMovieVideos(movieId: Int): Result<List<Video>> = runCatching {
        tmdbApi.getMovieVideos(movieId).results.map {
            Video(id = it.id, key = it.key, name = it.name, site = it.site, type = it.type)
        }
    }

    private fun com.crlsribeiro.reelcine.data.remote.dto.MovieDto.toMovie() = Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" } ?: "",
        backdropPath = backdropPath?.let { "https://image.tmdb.org/t/p/w1280$it" } ?: "",
        releaseDate = releaseDate ?: "",
        voteAverage = voteAverage,
        voteCount = voteCount,
        genres = genres?.map { it.name } ?: emptyList(),
        runtime = runtime ?: 0
    )
}
