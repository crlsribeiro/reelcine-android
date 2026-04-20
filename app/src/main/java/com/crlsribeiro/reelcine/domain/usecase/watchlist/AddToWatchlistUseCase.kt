package com.crlsribeiro.reelcine.domain.usecase.watchlist

import com.crlsribeiro.reelcine.domain.model.WatchlistItem
import com.crlsribeiro.reelcine.domain.repository.WatchlistRepository
import javax.inject.Inject

class AddToWatchlistUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) {
    suspend operator fun invoke(item: WatchlistItem) = watchlistRepository.addToWatchlist(item)
}
