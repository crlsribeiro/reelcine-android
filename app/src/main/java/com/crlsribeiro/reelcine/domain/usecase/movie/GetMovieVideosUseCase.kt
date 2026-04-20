package com.crlsribeiro.reelcine.domain.usecase.movie

import com.crlsribeiro.reelcine.domain.model.Video
import com.crlsribeiro.reelcine.domain.repository.MovieRepository
import javax.inject.Inject

class GetMovieVideosUseCase @Inject constructor(
    private val movieRepository: MovieRepository
) {
    suspend operator fun invoke(movieId: Int): Result<List<Video>> = movieRepository.getMovieVideos(movieId)
}
