package com.crlsribeiro.reelcine.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.User
import com.crlsribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.crlsribeiro.reelcine.domain.usecase.auth.SignOutUseCase
import com.google.firebase.auth.FirebaseAuth
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
    val isSignedOut: Boolean = false,
    val isAccountDeleted: Boolean = false,
    val isDeletingAccount: Boolean = false,
    val deleteError: String? = null,
    val movieCount: Int = 0,
    val reviewCount: Int = 0,
    val groupCount: Int = 0
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
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
                } else authUser

                val watchlistCount = firestore.collection("watchlist")
                    .whereEqualTo("userId", authUser.uid).get().await().size()
                val recommendationCount = firestore.collection("recommendations")
                    .whereEqualTo("userId", authUser.uid).get().await().size()
                val groupCount = firestore.collection("groups")
                    .whereArrayContains("members", authUser.uid).get().await().size()

                _uiState.value = ProfileUiState(
                    user = user,
                    movieCount = watchlistCount,
                    reviewCount = recommendationCount,
                    groupCount = groupCount
                )
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

    fun deleteAccount() {
        val userId = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDeletingAccount = true, deleteError = null)

            runCatching {
                val batch = firestore.batch()

                // 1. Marcar recomendações para deletar
                val recommendations = firestore.collection("recommendations")
                    .whereEqualTo("userId", userId).get().await()
                recommendations.documents.forEach { batch.delete(it.reference) }

                // 2. Marcar watchlist para deletar
                val watchlist = firestore.collection("watchlist")
                    .whereEqualTo("userId", userId).get().await()
                watchlist.documents.forEach { batch.delete(it.reference) }

                // 3. Remover usuário dos grupos
                val groups = firestore.collection("groups")
                    .whereArrayContains("members", userId).get().await()
                groups.documents.forEach { doc ->
                    batch.update(doc.reference,
                        "members", com.google.firebase.firestore.FieldValue.arrayRemove(userId),
                        "memberCount", (doc.getLong("memberCount") ?: 1) - 1
                    )
                }

                // 4. Deletar documento do perfil
                batch.delete(firestore.collection("users").document(userId))

                // Executa as exclusões no Firestore
                batch.commit().await()

                // 5. Tenta apagar do Firebase Auth
                auth.currentUser?.delete()?.await()

            }.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isDeletingAccount = false,
                    isAccountDeleted = true
                )
            }.onFailure { e ->
                // TRATAMENTO PARA O ERRO DE "RECENT AUTHENTICATION"
                if (e.message?.contains("recent authentication", ignoreCase = true) == true) {
                    signOutUseCase() // Força o logout no usecase
                    _uiState.value = _uiState.value.copy(
                        isDeletingAccount = false,
                        isSignedOut = true // Redireciona para o login
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isDeletingAccount = false,
                        deleteError = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    fun clearDeleteError() {
        _uiState.value = _uiState.value.copy(deleteError = null)
    }
}