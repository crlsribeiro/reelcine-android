package com.carlosribeiro.reelcine.domain.repository

import com.carlosribeiro.reelcine.domain.model.Recommendation
import kotlinx.coroutines.flow.Flow

interface RecommendationRepository {
    fun getFeedRecommendations(): Flow<List<Recommendation>>
    fun getGroupRecommendations(groupId: String): Flow<List<Recommendation>>
    suspend fun addRecommendation(recommendation: Recommendation): Result<Unit>
    suspend fun likeRecommendation(recommendationId: String, userId: String): Result<Unit>
    suspend fun unlikeRecommendation(recommendationId: String, userId: String): Result<Unit>
}
