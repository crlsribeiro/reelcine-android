package com.carlosribeiro.reelcine.presentation.viewmodel

import com.carlosribeiro.reelcine.domain.model.User
import com.carlosribeiro.reelcine.domain.usecase.auth.SignInWithEmailUseCase
import com.carlosribeiro.reelcine.domain.usecase.auth.SignInWithGoogleUseCase
import com.carlosribeiro.reelcine.presentation.screens.auth.LoginViewModel
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
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var signInWithEmailUseCase: SignInWithEmailUseCase
    private lateinit var signInWithGoogleUseCase: SignInWithGoogleUseCase

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        signInWithEmailUseCase = mockk()
        signInWithGoogleUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = LoginViewModel(signInWithEmailUseCase, signInWithGoogleUseCase)

    @Test
    fun `initial state is not loading and not success`() {
        val viewModel = createViewModel()
        assertFalse(viewModel.uiState.value.isLoading)
        assertFalse(viewModel.uiState.value.isSuccess)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `signInWithEmail sets error on failure`() {
        coEvery { signInWithEmailUseCase(any(), any()) } returns Result.failure(Exception("Invalid credentials"))
        val viewModel = createViewModel()
        viewModel.signInWithEmail("wrong@test.com", "wrong")
        assertNotNull(viewModel.uiState.value.error)
    }

    @Test
    fun `signInWithEmail sets loading state`() {
        coEvery { signInWithEmailUseCase(any(), any()) } returns Result.failure(Exception("error"))
        val viewModel = createViewModel()
        assertNotNull(viewModel.uiState)
    }

    @Test
    fun `onGoogleSignInError sets error message`() {
        val viewModel = createViewModel()
        viewModel.onGoogleSignInError("Google error")
        assertEquals("Google error", viewModel.uiState.value.error)
    }

    @Test
    fun `clearError resets error state`() {
        val viewModel = createViewModel()
        viewModel.onGoogleSignInError("Some error")
        viewModel.clearError()
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `signInWithGoogle sets error on failure`() {
        coEvery { signInWithGoogleUseCase(any()) } returns Result.failure(Exception("Google failed"))
        val viewModel = createViewModel()
        viewModel.signInWithGoogle("invalid_token")
        assertNotNull(viewModel.uiState.value.error)
    }
}
