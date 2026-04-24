package com.crlsribeiro.reelcine.domain.usecase

import com.crlsribeiro.reelcine.domain.model.Movie
import com.crlsribeiro.reelcine.domain.repository.MovieRepository
import com.crlsribeiro.reelcine.domain.usecase.movie.SearchMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SearchMoviesUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: SearchMoviesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = SearchMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns movies matching query`() = runTest {
        val movies = listOf(
            Movie(id = 1, title = "Avatar", overview = "", posterPath = "",
                backdropPath = "", releaseDate = "", voteAverage = 7.5,
                voteCount = 500, genres = emptyList(), runtime = 162)
        )
        coEvery { repository.searchMovies("Avatar") } returns Result.success(movies)

        val result = useCase("Avatar")

        assertTrue(result.isSuccess)
        assertEquals("Avatar", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `invoke returns empty list when no results`() = runTest {
        coEvery { repository.searchMovies("xyz123") } returns Result.success(emptyList())

        val result = useCase("xyz123")

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }

    @Test
    fun `invoke returns failure on network error`() = runTest {
        coEvery { repository.searchMovies(any()) } returns Result.failure(Exception("timeout"))

        val result = useCase("test")

        assertTrue(result.isFailure)
    }
}
