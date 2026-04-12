package com.carlosribeiro.reelcine.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.User
import com.carlosribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.carlosribeiro.reelcine.domain.usecase.auth.SignOutUseCase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class ProfileUiState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isSignedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = ProfileUiState(isLoading = true)
            val authUser = getCurrentUserUseCase() ?: run {
                _uiState.value = ProfileUiState()
                return@launch
            }
            try {
                val doc = firestore.collection("users").document(authUser.uid).get().await()
                val user = if (doc.exists()) {
                    User(
                        uid = authUser.uid,
                        name = doc.getString("name") ?: authUser.name,
                        email = doc.getString("email") ?: authUser.email,
                        avatarUrl = doc.getString("avatarUrl") ?: authUser.avatarUrl,
                        bio = doc.getString("bio") ?: ""
                    )
                } else {
                    authUser
                }
                _uiState.value = ProfileUiState(user = user)
            } catch (e: Exception) {
                _uiState.value = ProfileUiState(user = authUser)
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            signOutUseCase()
            _uiState.value = _uiState.value.copy(isSignedOut = true)
        }
    }
}