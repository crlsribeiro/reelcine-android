package com.carlosribeiro.reelcine.domain.usecase.recommendation

import com.carlosribeiro.reelcine.domain.model.Recommendation
import com.carlosribeiro.reelcine.domain.repository.RecommendationRepository
import javax.inject.Inject

class AddRecommendationUseCase @Inject constructor(
    private val recommendationRepository: RecommendationRepository
) {
    suspend operator fun invoke(recommendation: Recommendation): Result<Unit> {
        if (recommendation.movieTitle.isBlank()) return Result.failure(IllegalArgumentException("Movie title cannot be empty"))
        return recommendationRepository.addRecommendation(recommendation)
    }
}
