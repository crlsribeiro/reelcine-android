package com.crlsribeiro.reelcine.data.repository

import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.domain.repository.RecommendationRepository
import com.google.firebase.firestore.FieldValue
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

    private val recommendationsCollection get() = firestore.collection("recommendations")

    override fun getFeedRecommendations(): Flow<List<Recommendation>> = callbackFlow {
        val listener = recommendationsCollection
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
        val listener = recommendationsCollection
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

    override suspend fun addRecommendation(recommendation: Recommendation): Result<Unit> = runCatching {
        val data = mapOf(
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
            "timestamp" to FieldValue.serverTimestamp(), // ✅ server timestamp
            "likes" to emptyList<String>(),
            "likesCount" to 0
        )
        recommendationsCollection.add(data).await()
    }

    override suspend fun likeRecommendation(
        recommendationId: String,
        userId: String
    ): Result<Unit> = runCatching {
        recommendationsCollection.document(recommendationId)
            .update(
                "likes", FieldValue.arrayUnion(userId), // ✅ atômico, sem unchecked cast
                "likesCount", FieldValue.increment(1)
            ).await()
    }

    override suspend fun unlikeRecommendation(
        recommendationId: String,
        userId: String
    ): Result<Unit> = runCatching {
        val ref = recommendationsCollection.document(recommendationId)
        firestore.runTransaction { transaction ->
            val snapshot = transaction.get(ref)
            val newLikes = snapshot.getStringList("likes").filter { it != userId }
            transaction.update(ref, "likes", newLikes)
            transaction.update(ref, "likesCount", newLikes.size)
        }.await()
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    @Suppress("UNCHECKED_CAST")
    private fun com.google.firebase.firestore.DocumentSnapshot.getStringList(
        field: String
    ): List<String> = get(field) as? List<String> ?: emptyList()
}