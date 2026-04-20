package com.crlsribeiro.reelcine.domain.usecase.movie

import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<Movie> = movieRepository.getMovieDetail(movieId)
}
