package com.carlosribeiro.reelcine.presentation.screens.editprofile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class EditProfileUiState(
    val name: String = "",
    val email: String = "",
    val bio: String = "",
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditProfileUiState())
    val uiState: StateFlow<EditProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            val uid = user?.uid ?: return@launch
            try {
                val doc = firestore.collection("users").document(uid).get().await()
                val bio = doc.getString("bio") ?: ""
                _uiState.value = EditProfileUiState(
                    name = user.name.ifBlank { doc.getString("name") ?: "" },
                    email = user.email,
                    bio = bio
                )
            } catch (e: Exception) {
                _uiState.value = EditProfileUiState(name = user.name, email = user.email)
            }
        }
    }

    fun onNameChange(name: String) { _uiState.value = _uiState.value.copy(name = name) }
    fun onBioChange(bio: String) { _uiState.value = _uiState.value.copy(bio = bio) }

    fun save(onSuccess: () -> Unit) {
        val uid = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(_uiState.value.name)
                    .build()
                auth.currentUser?.updateProfile(profileUpdates)?.await()
                firestore.collection("users").document(uid).set(
                    mapOf(
                        "name" to _uiState.value.name,
                        "email" to _uiState.value.email,
                        "bio" to _uiState.value.bio
                    )
                ).await()
                _uiState.value = _uiState.value.copy(isLoading = false, isSaved = true)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }
}
