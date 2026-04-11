package com.carlosribeiro.reelcine.presentation.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.domain.usecase.movie.GetNowPlayingMoviesUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetPopularMoviesUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetTopRatedMoviesUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetTrendingMoviesUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetTrendingTodayMoviesUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val heroMovie: Movie? = null,
    val trendingToday: List<Movie> = emptyList(),
    val nowPlaying: List<Movie> = emptyList(),
    val upcoming: List<Movie> = emptyList(),
    val popular: List<Movie> = emptyList(),
    val topRated: List<Movie> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getTrendingTodayMoviesUseCase: GetTrendingTodayMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
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
            val trendingDeferred = async { getTrendingMoviesUseCase().getOrElse { emptyList() } }
            val trendingTodayDeferred = async { getTrendingTodayMoviesUseCase().getOrElse { emptyList() } }
            val nowPlayingDeferred = async { getNowPlayingMoviesUseCase().getOrElse { emptyList() } }
            val upcomingDeferred = async { getUpcomingMoviesUseCase().getOrElse { emptyList() } }
            val popularDeferred = async { getPopularMoviesUseCase().getOrElse { emptyList() } }
            val topRatedDeferred = async { getTopRatedMoviesUseCase().getOrElse { emptyList() } }

            val trending = trendingDeferred.await()
            _uiState.value = HomeUiState(
                isLoading = false,
                heroMovie = trending.firstOrNull(),
                trendingToday = trendingTodayDeferred.await(),
                nowPlaying = nowPlayingDeferred.await(),
                upcoming = upcomingDeferred.await(),
                popular = popularDeferred.await(),
                topRated = topRatedDeferred.await()
            )
        }
    }
}
