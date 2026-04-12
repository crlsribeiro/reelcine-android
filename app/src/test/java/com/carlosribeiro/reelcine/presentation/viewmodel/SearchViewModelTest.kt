package com.carlosribeiro.reelcine.presentation.viewmodel

import app.cash.turbine.test
import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.domain.usecase.movie.SearchMoviesUseCase
import com.carlosribeiro.reelcine.presentation.screens.search.SearchViewModel
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var searchMoviesUseCase: SearchMoviesUseCase
    private lateinit var viewModel: SearchViewModel

    private val fakeMovies = listOf(
        Movie(id = 1, title = "Avatar", overview = "", posterPath = "",
            backdropPath = "", releaseDate = "", voteAverage = 7.5,
            voteCount = 500, genres = emptyList(), runtime = 162)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        searchMoviesUseCase = mockk()
        viewModel = SearchViewModel(searchMoviesUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state has empty query and movies`() = runTest {
        viewModel.uiState.test {
            val state = awaitItem()
            assertEquals("", state.query)
            assertTrue(state.movies.isEmpty())
            assertFalse(state.isLoading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onQueryChange clears movies when query is blank`() = runTest {
        viewModel.onQueryChange("")
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals("", state.query)
        assertTrue(state.movies.isEmpty())
    }

    @Test
    fun `onQueryChange updates query in state`() = runTest {
        coEvery { searchMoviesUseCase(any()) } returns Result.success(fakeMovies)
        viewModel.onQueryChange("Avatar")
        assertEquals("Avatar", viewModel.uiState.value.query)
    }

    @Test
    fun `search returns movies after debounce`() = runTest {
        coEvery { searchMoviesUseCase("Avatar") } returns Result.success(fakeMovies)
        viewModel.onQueryChange("Avatar")
        testDispatcher.scheduler.advanceTimeBy(500)
        testDispatcher.scheduler.advanceUntilIdle()
        val state = viewModel.uiState.value
        assertEquals(1, state.movies.size)
        assertEquals("Avatar", state.movies.first().title)
    }
}
