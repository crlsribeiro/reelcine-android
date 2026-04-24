package com.crlsribeiro.reelcine.domain.usecase

import com.crlsribeiro.reelcine.domain.model.WatchlistItem
import com.crlsribeiro.reelcine.domain.repository.WatchlistRepository
import com.crlsribeiro.reelcine.domain.usecase.watchlist.AddToWatchlistUseCase
import com.crlsribeiro.reelcine.domain.usecase.watchlist.IsInWatchlistUseCase
import com.crlsribeiro.reelcine.domain.usecase.watchlist.RemoveFromWatchlistUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WatchlistUseCaseTest {

    private lateinit var repository: WatchlistRepository
    private lateinit var addToWatchlistUseCase: AddToWatchlistUseCase
    private lateinit var removeFromWatchlistUseCase: RemoveFromWatchlistUseCase
    private lateinit var isInWatchlistUseCase: IsInWatchlistUseCase

    private val fakeItem = WatchlistItem(
        id = "1",
        movieId = 100,
        movieTitle = "Inception",
        posterPath = "",
        backdropPath = "",
        voteAverage = 8.8,
        userId = "user123",
        addedAt = 1000L
    )

    @Before
    fun setUp() {
        repository = mockk()
        addToWatchlistUseCase = AddToWatchlistUseCase(repository)
        removeFromWatchlistUseCase = RemoveFromWatchlistUseCase(repository)
        isInWatchlistUseCase = IsInWatchlistUseCase(repository)
    }

    @Test
    fun `addToWatchlist returns success`() = runTest {
        coEvery { repository.addToWatchlist(fakeItem) } returns Result.success(Unit)

        val result = addToWatchlistUseCase(fakeItem)

        assertTrue(result.isSuccess)
        coVerify { repository.addToWatchlist(fakeItem) }
    }

    @Test
    fun `addToWatchlist returns failure on error`() = runTest {
        coEvery { repository.addToWatchlist(fakeItem) } returns Result.failure(Exception("Firestore error"))

        val result = addToWatchlistUseCase(fakeItem)

        assertTrue(result.isFailure)
    }

    @Test
    fun `removeFromWatchlist returns success`() = runTest {
        coEvery { repository.removeFromWatchlist(100, "user123") } returns Result.success(Unit)

        val result = removeFromWatchlistUseCase(100, "user123")

        assertTrue(result.isSuccess)
        coVerify { repository.removeFromWatchlist(100, "user123") }
    }

    @Test
    fun `isInWatchlist returns true when movie is saved`() = runTest {
        coEvery { repository.isInWatchlist(100, "user123") } returns true

        val result = isInWatchlistUseCase(100, "user123")

        assertTrue(result)
    }

    @Test
    fun `isInWatchlist returns false when movie is not saved`() = runTest {
        coEvery { repository.isInWatchlist(999, "user123") } returns false

        val result = isInWatchlistUseCase(999, "user123")

        assertFalse(result)
    }
}
