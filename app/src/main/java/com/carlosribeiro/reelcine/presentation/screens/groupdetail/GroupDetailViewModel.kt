package com.carlosribeiro.reelcine.presentation.screens.groupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.carlosribeiro.reelcine.domain.model.Group
import com.carlosribeiro.reelcine.domain.model.Recommendation
import com.carlosribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.google.firebase.firestore.FirebaseFirestore
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
    }

    fun loadGroupDetail() {
        if (groupId.isBlank()) {
            _uiState.value = GroupDetailUiState(isLoading = false, error = "Grupo não encontrado")
            return
        }
        viewModelScope.launch {
            _uiState.value = GroupDetailUiState(isLoading = true)
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
                    memberCount = members.size
                )

                val recsSnapshot = firestore.collection("groups")
                    .document(groupId)
                    .collection("recommendations")
                    .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
                    .limit(20)
                    .get()
                    .await()

                val recommendations = recsSnapshot.documents.mapNotNull { recDoc ->
                    try {
                        Recommendation(
                            id = recDoc.id,
                            movieId = (recDoc.getLong("movieId") ?: 0L).toInt(),
                            movieTitle = recDoc.getString("movieTitle") ?: "",
                            posterPath = recDoc.getString("posterPath") ?: "",
                            backdropPath = recDoc.getString("backdropPath") ?: "",
                            comment = recDoc.getString("comment") ?: "",
                            userId = recDoc.getString("userId") ?: "",
                            userName = recDoc.getString("userName") ?: "",
                            groupId = groupId
                        )
                    } catch (e: Exception) { null }
                }

                _uiState.value = GroupDetailUiState(
                    isLoading = false,
                    group = group,
                    recommendations = recommendations,
                    isAdmin = group.adminId == userId,
                    isMember = members.contains(userId)
                )
            } catch (e: Exception) {
                _uiState.value = GroupDetailUiState(isLoading = false, error = e.message)
            }
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
