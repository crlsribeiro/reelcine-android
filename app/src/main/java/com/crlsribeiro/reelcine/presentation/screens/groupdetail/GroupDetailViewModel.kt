package com.crlsribeiro.reelcine.presentation.screens.groupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.Group
import com.crlsribeiro.reelcine.domain.model.Recommendation
import com.crlsribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.google.firebase.firestore.FieldValue
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
    private val groupsCollection get() = firestore.collection("groups")

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
                val userId = getCurrentUserUseCase()?.uid.orEmpty()
                val doc = groupsCollection.document(groupId).get().await()
                val members = doc.getStringList("members") // ✅ sem unchecked cast
                val group = Group(
                    id = groupId,
                    name = doc.getString("name").orEmpty(),
                    description = doc.getString("description").orEmpty(),
                    adminId = doc.getString("adminId").orEmpty(),
                    members = members,
                    memberCount = members.size,
                    inviteCode = doc.getString("inviteCode").orEmpty()
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
                    runCatching {
                        Recommendation(
                            id = doc.id,
                            movieId = (doc.getLong("movieId") ?: 0L).toInt(),
                            movieTitle = doc.getString("movieTitle").orEmpty(),
                            posterPath = doc.getString("posterPath").orEmpty(),
                            backdropPath = doc.getString("backdropPath").orEmpty(),
                            comment = doc.getString("comment").orEmpty(),
                            rating = (doc.getDouble("rating") ?: 0.0).toFloat(),
                            userId = doc.getString("userId").orEmpty(),
                            userName = doc.getString("userName").orEmpty(),
                            groupId = groupId,
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    }.getOrNull()
                } ?: emptyList()
                _uiState.value = _uiState.value.copy(recommendations = recommendations)
            }
    }

    fun joinGroup() {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            runCatching {
                groupsCollection.document(groupId)
                    .update("members", FieldValue.arrayUnion(userId))
                    .await()
                loadGroupDetail()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    fun leaveGroup() {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            runCatching {
                groupsCollection.document(groupId)
                    .update("members", FieldValue.arrayRemove(userId))
                    .await()
                loadGroupDetail()
            }.onFailure { e ->
                _uiState.value = _uiState.value.copy(error = e.message)
            }
        }
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    @Suppress("UNCHECKED_CAST")
    private fun com.google.firebase.firestore.DocumentSnapshot.getStringList(
        field: String
    ): List<String> = get(field) as? List<String> ?: emptyList()
}