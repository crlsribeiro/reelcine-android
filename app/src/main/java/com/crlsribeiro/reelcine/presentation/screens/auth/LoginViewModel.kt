package com.crlsribeiro.reelcine.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.usecase.auth.SignInWithEmailUseCase
import com.crlsribeiro.reelcine.domain.usecase.auth.SignInWithGoogleUseCase
import com.crlsribeiro.reelcine.service.FcmTokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInWithEmailUseCase: SignInWithEmailUseCase,
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun signInWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            signInWithEmailUseCase(email, password)
                .onSuccess {
                    FcmTokenManager.saveToken()
                    _uiState.value = LoginUiState(isSuccess = true)
                }
                .onFailure { _uiState.value = LoginUiState(error = it.message) }
        }
    }

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = LoginUiState(isLoading = true)
            signInWithGoogleUseCase(idToken)
                .onSuccess {
                    FcmTokenManager.saveToken()
                    _uiState.value = LoginUiState(isSuccess = true)
                }
                .onFailure { _uiState.value = LoginUiState(error = it.message) }
        }
    }

    fun onGoogleSignInError(message: String) {
        _uiState.value = LoginUiState(error = message)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}
