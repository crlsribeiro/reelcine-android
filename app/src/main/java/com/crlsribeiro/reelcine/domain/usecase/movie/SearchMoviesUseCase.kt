package com.crlsribeiro.reelcine.domain.usecase.movie

import com.crlsribeiro.reelcine.domain.repository.MovieRepository
import javax.inject.Inject

class SearchMoviesUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(query: String) = movieRepository.searchMovies(query)
}
