package com.crlsribeiro.reelcine.presentation.screens.movielist

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.domain.usecase.movie.GetNowPlayingMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetPopularMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetTopRatedMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetTrendingMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetTrendingTodayMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetUpcomingMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MovieListUiState(
    val isLoading: Boolean = true,
    val movies: List<Movie> = emptyList(),
    val title: String = "",
    val error: String? = null
)

@HiltViewModel
class MovieListViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getTrendingMoviesUseCase: GetTrendingMoviesUseCase,
    private val getTrendingTodayMoviesUseCase: GetTrendingTodayMoviesUseCase,
    private val getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase,
    private val getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase,
    private val getPopularMoviesUseCase: GetPopularMoviesUseCase,
    private val getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState: StateFlow<MovieListUiState> = _uiState.asStateFlow()

    init {
        val category = savedStateHandle.get<String>("category") ?: "popular"
        loadMovies(category)
    }

    private fun loadMovies(category: String) {
        viewModelScope.launch {
            _uiState.value = MovieListUiState(isLoading = true, title = getCategoryTitle(category))
            val movies = when (category) {
                "trending_today" -> getTrendingTodayMoviesUseCase().getOrElse { emptyList() }
                "trending_week" -> getTrendingMoviesUseCase().getOrElse { emptyList() }
                "now_playing" -> getNowPlayingMoviesUseCase().getOrElse { emptyList() }
                "upcoming" -> getUpcomingMoviesUseCase().getOrElse { emptyList() }
                "popular" -> getPopularMoviesUseCase().getOrElse { emptyList() }
                "top_rated" -> getTopRatedMoviesUseCase().getOrElse { emptyList() }
                else -> emptyList()
            }
            _uiState.value = MovieListUiState(isLoading = false, movies = movies, title = getCategoryTitle(category))
        }
    }

    private fun getCategoryTitle(category: String) = when (category) {
        "trending_today" -> "🔥 Em Alta Hoje"
        "trending_week" -> "📈 Em Alta na Semana"
        "now_playing" -> "🎭 Em Cartaz"
        "upcoming" -> "🚀 Em Breve"
        "popular" -> "⭐ Mais Populares"
        "top_rated" -> "🏆 Melhor Avaliados"
        else -> "Filmes"
    }
}
