package com.crlsribeiro.reelcine.domain.usecase.watchlist

import com.crlsribeiro.reelcine.domain.repository.WatchlistRepository
import javax.inject.Inject

class IsInWatchlistUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) {
    suspend operator fun invoke(movieId: Int, userId: String) =
        watchlistRepository.isInWatchlist(movieId, userId)
}
