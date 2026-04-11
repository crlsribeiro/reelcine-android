package com.carlosribeiro.reelcine.domain.usecase.movie

import com.carlosribeiro.reelcine.domain.repository.MovieRepository
import javax.inject.Inject

class GetTrendingTodayMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke() = movieRepository.getTrendingTodayMovies()
}
