package com.carlosribeiro.reelcine.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.domain.usecase.movie.GetNowPlayingMoviesUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetTopRatedMoviesUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetTrendingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val trendingMovies: List<Movie> = emptyList(),
    val nowPlayingMovies: List<Movie> = emptyList(),
    val topRatedMovies: List<Movie> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadMovies()
    }

    fun loadMovies() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            val trending = getTrendingMoviesUseCase().getOrElse { emptyList() }
            val nowPlaying = getNowPlayingMoviesUseCase().getOrElse { emptyList() }
            val topRated = getTopRatedMoviesUseCase().getOrElse { emptyList() }
            _uiState.value = HomeUiState(
                trendingMovies = trending,
                nowPlayingMovies = nowPlaying,
                topRatedMovies = topRated
            )
        }
    }
}
