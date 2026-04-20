package com.crlsribeiro.reelcine.data.repository

import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.domain.repository.RecommendationRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class RecommendationRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : RecommendationRepository {

    override fun getFeedRecommendations(): Flow<List<Recommendation>> = callbackFlow {
        val listener = firestore.collection("recommendations")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(50)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val recommendations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Recommendation::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(recommendations)
            }
        awaitClose { listener.remove() }
    }

    override fun getGroupRecommendations(groupId: String): Flow<List<Recommendation>> = callbackFlow {
        val listener = firestore.collection("recommendations")
            .whereEqualTo("groupId", groupId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val recommendations = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Recommendation::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(recommendations)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun addRecommendation(recommendation: Recommendation): Result<Unit> {
        return try {
            val data = hashMapOf(
                "movieId" to recommendation.movieId,
                "movieTitle" to recommendation.movieTitle,
                "posterPath" to recommendation.posterPath,
                "backdropPath" to recommendation.backdropPath,
                "comment" to recommendation.comment,
                "rating" to recommendation.rating,
                "userId" to recommendation.userId,
                "userName" to recommendation.userName,
                "userAvatar" to recommendation.userAvatar,
                "groupId" to recommendation.groupId,
                "timestamp" to System.currentTimeMillis(),
                "likes" to emptyList<String>(),
                "likesCount" to 0
            )
            firestore.collection("recommendations").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun likeRecommendation(recommendationId: String, userId: String): Result<Unit> {
        return try {
            val ref = firestore.collection("recommendations").document(recommendationId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(ref)
                val likes = snapshot.get("likes") as? List<String> ?: emptyList()
                if (!likes.contains(userId)) {
                    transaction.update(ref, "likes", likes + userId)
                    transaction.update(ref, "likesCount", likes.size + 1)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun unlikeRecommendation(recommendationId: String, userId: String): Result<Unit> {
        return try {
            val ref = firestore.collection("recommendations").document(recommendationId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(ref)
                val likes = snapshot.get("likes") as? List<String> ?: emptyList()
                val newLikes = likes.filter { it != userId }
                transaction.update(ref, "likes", newLikes)
                transaction.update(ref, "likesCount", newLikes.size)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
