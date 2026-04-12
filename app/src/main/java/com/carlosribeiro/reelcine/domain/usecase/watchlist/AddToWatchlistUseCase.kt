package com.carlosribeiro.reelcine.domain.usecase.watchlist

import com.carlosribeiro.reelcine.domain.model.WatchlistItem
import com.carlosribeiro.reelcine.domain.repository.WatchlistRepository
import javax.inject.Inject

class AddToWatchlistUseCase @Inject constructor(
    private val watchlistRepository: WatchlistRepository
) {
    suspend operator fun invoke(item: WatchlistItem) = watchlistRepository.addToWatchlist(item)
}
