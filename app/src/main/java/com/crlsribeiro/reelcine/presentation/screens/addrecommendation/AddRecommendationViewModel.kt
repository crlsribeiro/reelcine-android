package com.crlsribeiro.reelcine.presentation.screens.addrecommendation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.Group
import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.crlsribeiro.reelcine.domain.usecase.group.GetUserGroupsUseCase
import com.crlsribeiro.reelcine.domain.usecase.recommendation.AddRecommendationUseCase
import com.google.firebase.Timestamp // Importe necessário adicionado aqui
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AddRecommendationUiState(
    val isLoading: Boolean = false,
    val groups: List<Group> = emptyList(),
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class AddRecommendationViewModel @Inject constructor(
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
    private val addRecommendationUseCase: AddRecommendationUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddRecommendationUiState())
    val uiState: StateFlow<AddRecommendationUiState> = _uiState.asStateFlow()

    init {
        loadGroups()
    }

    private fun loadGroups() {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            getUserGroupsUseCase(userId).collect { groups ->
                _uiState.value = _uiState.value.copy(groups = groups)
            }
        }
    }

    fun addRecommendation(
        movieId: Int,
        movieTitle: String,
        posterPath: String,
        backdropPath: String,
        groupId: String,
        comment: String,
        rating: Float
    ) {
        val user = getCurrentUserUseCase() ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val recommendation = Recommendation(
                movieId = movieId,
                movieTitle = movieTitle,
                posterPath = posterPath,
                backdropPath = backdropPath,
                comment = comment,
                rating = rating,
                userId = user.uid,
                userName = user.name,
                groupId = groupId,
                // AJUSTE: Trocado de System.currentTimeMillis() para Timestamp.now()
                timestamp = Timestamp.now()
            )
            addRecommendationUseCase(recommendation)
                .onSuccess { _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = true) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }
}