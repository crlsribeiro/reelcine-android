package com.crlsribeiro.reelcine.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.crlsribeiro.reelcine.domain.usecase.recommendation.GetFeedRecommendationsUseCase
import com.crlsribeiro.reelcine.domain.usecase.recommendation.LikeRecommendationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FeedUiState(
    val isLoading: Boolean = true,
    val recommendations: List<Recommendation> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedRecommendationsUseCase: GetFeedRecommendationsUseCase,
    private val likeRecommendationUseCase: LikeRecommendationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    val currentUserId: String get() = getCurrentUserUseCase()?.uid ?: ""

    init {
        loadFeed()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            getFeedRecommendationsUseCase().collect { recommendations ->
                _uiState.value = FeedUiState(isLoading = false, recommendations = recommendations)
            }
        }
    }

    fun toggleLike(recommendationId: String) {
        val userId = currentUserId
        if (userId.isBlank()) return
        val recommendation = _uiState.value.recommendations.find { it.id == recommendationId } ?: return
        viewModelScope.launch {
            if (recommendation.likes.contains(userId)) {
                likeRecommendationUseCase(recommendationId, userId)
            } else {
                likeRecommendationUseCase(recommendationId, userId)
            }
        }
    }
}
