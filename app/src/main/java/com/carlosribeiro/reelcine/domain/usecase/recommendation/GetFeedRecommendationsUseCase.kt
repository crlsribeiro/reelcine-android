package com.carlosribeiro.reelcine.domain.usecase.recommendation

import com.carlosribeiro.reelcine.domain.model.Recommendation
import com.carlosribeiro.reelcine.domain.repository.RecommendationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFeedRecommendationsUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) {
    operator fun invoke(): Flow<List<Recommendation>> =
        recommendationRepository.getFeedRecommendations()
}
