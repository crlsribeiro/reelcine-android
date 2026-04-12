package com.carlosribeiro.reelcine.presentation.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.WatchlistItem
import com.carlosribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.carlosribeiro.reelcine.domain.usecase.watchlist.GetWatchlistUseCase
import com.carlosribeiro.reelcine.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WatchlistUiState(
    val isLoading: Boolean = true,
    val items: List<WatchlistItem> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    private val getWatchlistUseCase: GetWatchlistUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(WatchlistUiState())
    val uiState: StateFlow<WatchlistUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = getCurrentUserUseCase()?.uid ?: ""
            if (userId.isBlank()) {
                _uiState.value = WatchlistUiState(isLoading = false)
                return@launch
            }
            getWatchlistUseCase(userId)
                .catch { e ->
                    _uiState.value = WatchlistUiState(isLoading = false, error = e.message)
                }
                .collect { items ->
                    _uiState.value = WatchlistUiState(isLoading = false, items = items)
                }
        }
    }

    fun removeFromWatchlist(movieId: Int) {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            removeFromWatchlistUseCase(movieId, userId)
        }
    }
}
