package com.carlosribeiro.reelcine.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.domain.usecase.movie.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val query: String = "",
    val isLoading: Boolean = false,
    val movies: List<Movie> = emptyList(),
    val error: String? = null,
    val isEmpty: Boolean = false
)

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchMoviesUseCase: SearchMoviesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _queryFlow = MutableStateFlow("")

    init {
        observeQuery()
    }

    @OptIn(FlowPreview::class)
    private fun observeQuery() {
        viewModelScope.launch {
            _queryFlow
                .debounce(400)
                .distinctUntilChanged()
                .filter { it.length >= 2 }
                .collect { query ->
                    search(query)
                }
        }
    }

    fun onQueryChange(query: String) {
        _uiState.value = _uiState.value.copy(query = query, movies = if (query.isBlank()) emptyList() else _uiState.value.movies)
        _queryFlow.value = query
        if (query.isBlank()) {
            _uiState.value = SearchUiState()
        }
    }

    private fun search(query: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            searchMoviesUseCase(query)
                .onSuccess { movies ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        movies = movies,
                        isEmpty = movies.isEmpty()
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message)
                }
        }
    }
}
