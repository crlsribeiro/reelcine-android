package com.crlsribeiro.reelcine.presentation.screens.feed

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.crlsribeiro.reelcine.domain.usecase.recommendation.GetFeedRecommendationsUseCase
import com.crlsribeiro.reelcine.domain.usecase.recommendation.LikeRecommendationUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class FeedUiState(
    val isLoading: Boolean = true,
    val recommendations: List<Recommendation> = emptyList(),
    val error: String? = null,
    val blockedUserIds: Set<String> = emptySet() // FALTAVA ESTE CAMPO
)

@HiltViewModel
class FeedViewModel @Inject constructor(
    private val getFeedRecommendationsUseCase: GetFeedRecommendationsUseCase,
    private val likeRecommendationUseCase: LikeRecommendationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(FeedUiState())
    val uiState: StateFlow<FeedUiState> = _uiState.asStateFlow()

    val currentUserId: String get() = getCurrentUserUseCase()?.uid ?: ""

    init {
        loadFeed()
        loadBlockedUsers()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            getFeedRecommendationsUseCase().collect { recommendations ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    recommendations = recommendations
                )
            }
        }
    }

    private fun loadBlockedUsers() {
        val userId = currentUserId
        if (userId.isBlank()) return

        viewModelScope.launch {
            try {
                val snapshot = firestore.collection("users")
                    .document(userId)
                    .collection("blockedUsers")
                    .get()
                    .await()

                val blockedIds = snapshot.documents.map { it.id }.toSet()
                _uiState.value = _uiState.value.copy(blockedUserIds = blockedIds)
            } catch (e: Exception) {
                // Erro silencioso para não travar o feed
            }
        }
    }

    fun toggleLike(recommendationId: String) {
        val userId = currentUserId
        if (userId.isBlank()) return
        viewModelScope.launch {
            likeRecommendationUseCase(recommendationId, userId)
        }
    }

    // FUNÇÃO QUE FALTAVA (Erro 81:63 no log)
    fun reportRecommendation(recommendation: Recommendation, reason: String) {
        val reporterId = currentUserId
        if (reporterId.isBlank()) return

        viewModelScope.launch {
            val report = hashMapOf(
                "reporterId" to reporterId,
                "contentId" to recommendation.id,
                "authorId" to recommendation.userId,
                "movieTitle" to recommendation.movieTitle,
                "reason" to reason,
                "timestamp" to com.google.firebase.Timestamp.now()
            )
            runCatching { firestore.collection("reports").add(report).await() }
        }
    }

    // FUNÇÃO QUE FALTAVA (Erro 82:52 no log)
    fun blockUser(targetUserId: String) {
        val userId = currentUserId
        if (userId.isBlank() || userId == targetUserId) return

        viewModelScope.launch {
            runCatching {
                firestore.collection("users")
                    .document(userId)
                    .collection("blockedUsers")
                    .document(targetUserId)
                    .set(mapOf("blockedAt" to com.google.firebase.Timestamp.now()))
                    .await()

                val currentBlocked = _uiState.value.blockedUserIds.toMutableSet()
                currentBlocked.add(targetUserId)
                _uiState.value = _uiState.value.copy(blockedUserIds = currentBlocked)
            }
        }
    }
}