package com.crlsribeiro.reelcine.data.repository

import com.crlsribeiro.reelcine.domain.model.WatchlistItem
import com.crlsribeiro.reelcine.domain.repository.WatchlistRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class WatchlistRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : WatchlistRepository {

    override fun getWatchlist(userId: String): Flow<List<WatchlistItem>> = callbackFlow {
        val listener = firestore.collection("watchlist")
            .whereEqualTo("userId", userId)
            .orderBy("addedAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val items = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(WatchlistItem::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(items)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addToWatchlist(item: WatchlistItem): Result<Unit> {
        return try {
            val data = hashMapOf(
                "movieId" to item.movieId,
                "movieTitle" to item.movieTitle,
                "posterPath" to item.posterPath,
                "backdropPath" to item.backdropPath,
                "voteAverage" to item.voteAverage,
                "userId" to item.userId,
                "addedAt" to System.currentTimeMillis()
            )
            firestore.collection("watchlist").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromWatchlist(movieId: Int, userId: String): Result<Unit> {
        return try {
            val docs = firestore.collection("watchlist")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("userId", userId)
                .get().await()
            docs.documents.forEach { it.reference.delete().await() }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isInWatchlist(movieId: Int, userId: String): Boolean {
        return try {
            val docs = firestore.collection("watchlist")
                .whereEqualTo("movieId", movieId)
                .whereEqualTo("userId", userId)
                .get().await()
            !docs.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}
