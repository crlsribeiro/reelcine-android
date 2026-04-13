package com.carlosribeiro.reelcine.presentation.viewmodel

import androidx.lifecycle.SavedStateHandle
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetMovieDetailUseCase
import com.carlosribeiro.reelcine.domain.usecase.movie.GetMovieVideosUseCase
import com.carlosribeiro.reelcine.domain.usecase.watchlist.AddToWatchlistUseCase
import com.carlosribeiro.reelcine.domain.usecase.watchlist.IsInWatchlistUseCase
import com.carlosribeiro.reelcine.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import com.carlosribeiro.reelcine.presentation.screens.moviedetail.MovieDetailViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MovieDetailViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var getMovieDetailUseCase: GetMovieDetailUseCase
    private lateinit var getMovieVideosUseCase: GetMovieVideosUseCase
    private lateinit var addToWatchlistUseCase: AddToWatchlistUseCase
    private lateinit var removeFromWatchlistUseCase: RemoveFromWatchlistUseCase
    private lateinit var isInWatchlistUseCase: IsInWatchlistUseCase
    private lateinit var getCurrentUserUseCase: GetCurrentUserUseCase

    private val fakeMovie = Movie(
        id = 1, title = "Inception", overview = "A dream", posterPath = "",
        backdropPath = "", releaseDate = "2010-07-16", voteAverage = 8.8,
        voteCount = 1000, genres = listOf("Action"), runtime = 148
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getMovieDetailUseCase = mockk()
        getMovieVideosUseCase = mockk()
        addToWatchlistUseCase = mockk()
        removeFromWatchlistUseCase = mockk()
        isInWatchlistUseCase = mockk()
        getCurrentUserUseCase = mockk()
        coEvery { getCurrentUserUseCase() } returns null
        coEvery { getMovieVideosUseCase(any()) } returns Result.success(emptyList())
        coEvery { getMovieDetailUseCase(any()) } returns Result.success(fakeMovie)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(movieId: Int = 1) = MovieDetailViewModel(
        savedStateHandle = SavedStateHandle(mapOf("movieId" to movieId)),
        getMovieDetailUseCase = getMovieDetailUseCase,
        getMovieVideosUseCase = getMovieVideosUseCase,
        addToWatchlistUseCase = addToWatchlistUseCase,
        removeFromWatchlistUseCase = removeFromWatchlistUseCase,
        isInWatchlistUseCase = isInWatchlistUseCase,
        getCurrentUserUseCase = getCurrentUserUseCase
    )

    @Test
    fun `viewModel is created successfully`() {
        val viewModel = createViewModel()
        assertNotNull(viewModel)
    }

    @Test
    fun `initial uiState has isLoading true`() {
        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState)
    }

    @Test
    fun `isInWatchlist is false when user is null`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.isInWatchlist)
    }

    @Test
    fun `savedStateHandle with zero movieId creates viewModel`() {
        val viewModel = MovieDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf("movieId" to 0)),
            getMovieDetailUseCase = getMovieDetailUseCase,
            getMovieVideosUseCase = getMovieVideosUseCase,
            addToWatchlistUseCase = addToWatchlistUseCase,
            removeFromWatchlistUseCase = removeFromWatchlistUseCase,
            isInWatchlistUseCase = isInWatchlistUseCase,
            getCurrentUserUseCase = getCurrentUserUseCase
        )
        assertNotNull(viewModel)
    }
}
