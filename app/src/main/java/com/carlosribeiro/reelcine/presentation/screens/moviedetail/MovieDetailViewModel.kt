package com.carlosribeiro.reelcine.presentation.screens.moviedetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.domain.model.Video
import com.carlosribeiro.reelcine.domain.usecase.movie.GetMovieDetailUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetMovieVideosUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movie: Movie? = null,
    val trailer: Video? = null,
    val error: String? = null
)

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieDetailUseCase: GetMovieDetailUseCase,
    private val getMovieVideosUseCase: GetMovieVideosUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val movieId: Int = checkNotNull(savedStateHandle["movieId"])

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
            _uiState.value = MovieDetailUiState(movie = movie, trailer = trailer)
        }
    }
}
