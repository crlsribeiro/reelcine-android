package com.carlosribeiro.reelcine.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.User
import com.carlosribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.carlosribeiro.reelcine.domain.usecase.auth.SignOutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSignedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        val user = getCurrentUserUseCase()
        _uiState.value = ProfileUiState(user = user)
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _uiState.value = _uiState.value.copy(isSignedOut = true)
        }
    }
}
