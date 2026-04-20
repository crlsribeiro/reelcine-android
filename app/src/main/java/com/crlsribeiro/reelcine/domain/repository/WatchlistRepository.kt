package com.crlsribeiro.reelcine.domain.repository

import com.crlsribeiro.reelcine.domain.model.WatchlistItem
import kotlinx.coroutines.flow.Flow

interface WatchlistRepository {
    fun getWatchlist(userId: String): Flow<List<WatchlistItem>>
    suspend fun addToWatchlist(item: WatchlistItem): Result<Unit>
    suspend fun removeFromWatchlist(movieId: Int, userId: String): Result<Unit>
    suspend fun isInWatchlist(movieId: Int, userId: String): Boolean
}
