package com.crlsribeiro.reelcine.presentation.screens.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.domain.model.Video
import com.crlsribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetMovieDetailUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetMovieVideosUseCase
import com.crlsribeiro.reelcine.domain.usecase.watchlist.AddToWatchlistUseCase
import com.crlsribeiro.reelcine.domain.usecase.watchlist.IsInWatchlistUseCase
import com.crlsribeiro.reelcine.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import com.crlsribeiro.reelcine.domain.model.WatchlistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val isLoading: Boolean = true,
    val movie: Movie? = null,
    val trailer: Video? = null,
    val isInWatchlist: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    private val addToWatchlistUseCase: AddToWatchlistUseCase,
    private val removeFromWatchlistUseCase: RemoveFromWatchlistUseCase,
    private val isInWatchlistUseCase: IsInWatchlistUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val movieId: Int = savedStateHandle["movieId"] ?: 0
    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState: StateFlow<MovieDetailUiState> = _uiState.asStateFlow()

    init {
        loadMovieDetail()
    }

    private fun loadMovieDetail() {
        viewModelScope.launch {
            _uiState.value = MovieDetailUiState(isLoading = true)

            val movie = getMovieDetailUseCase(movieId).getOrNull()
            val videos = getMovieVideosUseCase(movieId).getOrElse { emptyList() }
            val trailer = videos.firstOrNull { it.isYouTubeTrailer }
            val userId = getCurrentUserUseCase()?.uid ?: ""
            val inWatchlist = if (userId.isNotBlank()) isInWatchlistUseCase(movieId, userId) else false

            // ✅ isLoading = false explícito — sem isso a tela trava no spinner
            _uiState.value = MovieDetailUiState(
                isLoading = false,
                movie = movie,
                trailer = trailer,
                isInWatchlist = inWatchlist
            )
        }
    }

    fun toggleWatchlist() {
        val movie = _uiState.value.movie ?: return
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            if (_uiState.value.isInWatchlist) {
                removeFromWatchlistUseCase(movieId, userId)
                _uiState.value = _uiState.value.copy(isInWatchlist = false)
            } else {
                addToWatchlistUseCase(
                    WatchlistItem(
                        movieId = movie.id,
                        movieTitle = movie.title,
                        posterPath = movie.posterPath,
                        backdropPath = movie.backdropPath,
                        voteAverage = movie.voteAverage,
                        userId = userId
                    )
                )
                _uiState.value = _uiState.value.copy(isInWatchlist = true)
            }
        }
    }
}