package com.carlosribeiro.reelcine.domain.usecase

import com.carlosribeiro.reelcine.domain.model.Movie
import com.carlosribeiro.reelcine.domain.repository.MovieRepository
import com.carlosribeiro.reelcine.domain.usecase.movie.GetTrendingMoviesUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetTrendingMoviesUseCaseTest {

    private lateinit var repository: MovieRepository
    private lateinit var useCase: GetTrendingMoviesUseCase

    @Before
    fun setUp() {
        repository = mockk()
        useCase = GetTrendingMoviesUseCase(repository)
    }

    @Test
    fun `invoke returns success with movies`() = runTest {
        val movies = listOf(
            Movie(id = 1, title = "Movie 1", overview = "", posterPath = "",
                backdropPath = "", releaseDate = "", voteAverage = 8.0,
                voteCount = 100, genres = emptyList(), runtime = 120)
        )
        coEvery { repository.getTrendingMovies() } returns Result.success(movies)

        val result = useCase()

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Movie 1", result.getOrNull()?.first()?.title)
    }

    @Test
    fun `invoke returns failure when repository fails`() = runTest {
        val error = Exception("Network error")
        coEvery { repository.getTrendingMovies() } returns Result.failure(error)

        val result = useCase()

        assertTrue(result.isFailure)
        assertEquals("Network error", result.exceptionOrNull()?.message)
    }

    @Test
    fun `invoke returns empty list when no movies`() = runTest {
        coEvery { repository.getTrendingMovies() } returns Result.success(emptyList())

        val result = useCase()

        assertTrue(result.isSuccess)
        assertTrue(result.getOrNull()?.isEmpty() == true)
    }
}
