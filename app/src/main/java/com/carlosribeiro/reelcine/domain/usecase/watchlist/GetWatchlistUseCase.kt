package com.carlosribeiro.reelcine.domain.usecase.watchlist

import com.carlosribeiro.reelcine.domain.repository.WatchlistRepository
import javax.inject.Inject

class GetWatchlistUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) {
    operator fun invoke(userId: String) = watchlistRepository.getWatchlist(userId)
}
