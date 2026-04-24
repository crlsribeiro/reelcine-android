package com.crlsribeiro.reelcine.presentation.viewmodel

import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.domain.usecase.movie.GetNowPlayingMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetPopularMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetTopRatedMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetTrendingMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetTrendingTodayMoviesUseCase
import com.crlsribeiro.reelcine.domain.usecase.movie.GetUpcomingMoviesUseCase
import com.crlsribeiro.reelcine.presentation.screens.home.HomeViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var getTrendingMoviesUseCase: GetTrendingMoviesUseCase
    private lateinit var getTrendingTodayMoviesUseCase: GetTrendingTodayMoviesUseCase
    private lateinit var getNowPlayingMoviesUseCase: GetNowPlayingMoviesUseCase
    private lateinit var getUpcomingMoviesUseCase: GetUpcomingMoviesUseCase
    private lateinit var getPopularMoviesUseCase: GetPopularMoviesUseCase
    private lateinit var getTopRatedMoviesUseCase: GetTopRatedMoviesUseCase

    private val fakeMovies = listOf(
        Movie(id = 1, title = "Test Movie", overview = "Overview", posterPath = "",
            backdropPath = "", releaseDate = "2024-01-01", voteAverage = 8.0,
            voteCount = 100, genres = listOf("Action"), runtime = 120)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getTrendingMoviesUseCase = mockk()
        getTrendingTodayMoviesUseCase = mockk()
        getNowPlayingMoviesUseCase = mockk()
        getUpcomingMoviesUseCase = mockk()
        getPopularMoviesUseCase = mockk()
        getTopRatedMoviesUseCase = mockk()

        coEvery { getTrendingMoviesUseCase() } returns Result.success(fakeMovies)
        coEvery { getTrendingTodayMoviesUseCase() } returns Result.success(fakeMovies)
        coEvery { getNowPlayingMoviesUseCase() } returns Result.success(fakeMovies)
        coEvery { getUpcomingMoviesUseCase() } returns Result.success(fakeMovies)
        coEvery { getPopularMoviesUseCase() } returns Result.success(fakeMovies)
        coEvery { getTopRatedMoviesUseCase() } returns Result.success(fakeMovies)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = HomeViewModel(
        getTrendingMoviesUseCase, getTrendingTodayMoviesUseCase,
        getNowPlayingMoviesUseCase, getUpcomingMoviesUseCase,
        getPopularMoviesUseCase, getTopRatedMoviesUseCase
    )

    @Test
    fun `initial state has no hero movie before load`() = runTest {
        val viewModel = createViewModel()
        val initial = viewModel.uiState.value
        assertNull(initial.heroMovie)
    }

    @Test
    fun `loads movies successfully`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertNotNull(state.heroMovie)
    }

    @Test
    fun `hero movie is set from trending`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals("Test Movie", viewModel.uiState.value.heroMovie?.title)
    }

    @Test
    fun `trendingToday is populated after load`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.trendingToday.isEmpty())
    }

    @Test
    fun `nowPlaying is populated after load`() = runTest {
        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()
        assertFalse(viewModel.uiState.value.nowPlaying.isEmpty())
    }

    @Test
    fun `error state when all use cases fail`() = runTest {
        coEvery { getTrendingMoviesUseCase() } returns Result.failure(Exception("error"))
        coEvery { getTrendingTodayMoviesUseCase() } returns Result.failure(Exception("error"))
        coEvery { getNowPlayingMoviesUseCase() } returns Result.failure(Exception("error"))
        coEvery { getUpcomingMoviesUseCase() } returns Result.failure(Exception("error"))
        coEvery { getPopularMoviesUseCase() } returns Result.failure(Exception("error"))
        coEvery { getTopRatedMoviesUseCase() } returns Result.failure(Exception("error"))

        val viewModel = createViewModel()
        testDispatcher.scheduler.advanceUntilIdle()

        assertNull(viewModel.uiState.value.heroMovie)
    }
}
