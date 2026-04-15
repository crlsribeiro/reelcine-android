package com.carlosribeiro.reelcine.presentation.screens.groupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.Group
import com.carlosribeiro.reelcine.domain.model.Recommendation
import com.carlosribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

data class GroupDetailUiState(
    val isLoading: Boolean = true,
    val group: Group? = null,
    val recommendations: List<Recommendation> = emptyList(),
    val isAdmin: Boolean = false,
    val isMember: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class GroupDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val groupId: String = savedStateHandle["groupId"] ?: ""
    private val _uiState = MutableStateFlow(GroupDetailUiState())
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    init {
        loadGroupDetail()
        listenToRecommendations()
    }

    fun loadGroupDetail() {
        if (groupId.isBlank()) {
            _uiState.value = GroupDetailUiState(isLoading = false, error = "Grupo não encontrado")
            return
        }
        viewModelScope.launch {
            try {
                val userId = getCurrentUserUseCase()?.uid ?: ""
                val doc = firestore.collection("groups").document(groupId).get().await()
                val members = doc.get("members") as? List<String> ?: emptyList()
                val group = Group(
                    id = groupId,
                    name = doc.getString("name") ?: "",
                    description = doc.getString("description") ?: "",
                    adminId = doc.getString("adminId") ?: "",
                    members = members,
                    memberCount = members.size,
                    inviteCode = doc.getString("inviteCode") ?: ""
                )
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    group = group,
                    isAdmin = group.adminId == userId,
                    isMember = members.contains(userId)
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    private fun listenToRecommendations() {
        if (groupId.isBlank()) return
        firestore.collection("recommendations")
            .whereEqualTo("groupId", groupId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    _uiState.value = _uiState.value.copy(error = error.message)
                    return@addSnapshotListener
                }
                val recommendations = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Recommendation(
                            id = doc.id,
                            movieId = (doc.getLong("movieId") ?: 0L).toInt(),
                            movieTitle = doc.getString("movieTitle") ?: "",
                            posterPath = doc.getString("posterPath") ?: "",
                            backdropPath = doc.getString("backdropPath") ?: "",
                            comment = doc.getString("comment") ?: "",
                            rating = (doc.getDouble("rating") ?: 0.0).toFloat(),
                            userId = doc.getString("userId") ?: "",
                            userName = doc.getString("userName") ?: "",
                            groupId = groupId,
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    } catch (e: Exception) { null }
                } ?: emptyList()
                _uiState.value = _uiState.value.copy(recommendations = recommendations)
            }
    }

    fun joinGroup() {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("groups").document(groupId)
                    .update("members", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
                    .await()
                loadGroupDetail()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun leaveGroup() {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            try {
                firestore.collection("groups").document(groupId)
                    .update("members", com.google.firebase.firestore.FieldValue.arrayRemove(userId))
                    .await()
                loadGroupDetail()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }
}
