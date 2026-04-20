package com.crlsribeiro.reelcine.domain.usecase.movie

import com.crlsribeiro.reelcine.domain.repository.MovieRepository
import javax.inject.Inject

class GetUpcomingMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke() = movieRepository.getUpcomingMovies()
}
