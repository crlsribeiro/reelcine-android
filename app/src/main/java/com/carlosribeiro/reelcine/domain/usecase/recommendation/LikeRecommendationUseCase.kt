package com.carlosribeiro.reelcine.domain.usecase.recommendation

import com.carlosribeiro.reelcine.domain.repository.RecommendationRepository
import javax.inject.Inject

class LikeRecommendationUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) {
    suspend operator fun invoke(recommendationId: String, userId: String): Result<Unit> =
        recommendationRepository.likeRecommendation(recommendationId, userId)
}
