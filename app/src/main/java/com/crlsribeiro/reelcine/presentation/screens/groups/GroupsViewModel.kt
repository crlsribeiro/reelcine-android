package com.crlsribeiro.reelcine.presentation.screens.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.crlsribeiro.reelcine.domain.model.Group
import com.crlsribeiro.reelcine.domain.usecase.auth.GetCurrentUserUseCase
import com.crlsribeiro.reelcine.domain.usecase.group.CreateGroupUseCase
import com.crlsribeiro.reelcine.domain.usecase.group.GetUserGroupsUseCase
import com.crlsribeiro.reelcine.domain.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class GroupsUiState(
    val isLoading: Boolean = false,
    val groups: List<Group> = emptyList(),
    val error: String? = null,
    val showCreateDialog: Boolean = false,
    val showJoinDialog: Boolean = false,
    val joinSuccess: Group? = null
)

@HiltViewModel
class GroupsViewModel @Inject constructor(
    private val getUserGroupsUseCase: GetUserGroupsUseCase,
    private val createGroupUseCase: CreateGroupUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GroupsUiState())
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init { loadGroups() }

    private fun loadGroups() {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            getUserGroupsUseCase(userId).collect { groups ->
                _uiState.value = _uiState.value.copy(groups = groups, isLoading = false)
            }
        }
    }

    fun createGroup(name: String, description: String) {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            createGroupUseCase(name, description, userId)
                .onSuccess { _uiState.value = _uiState.value.copy(showCreateDialog = false) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }

    fun joinGroupByCode(code: String) {
        val userId = getCurrentUserUseCase()?.uid ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            groupRepository.joinGroupByCode(code, userId)
                .onSuccess { group ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        showJoinDialog = false,
                        joinSuccess = group
                    )
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = it.message
                    )
                }
        }
    }

    fun showCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = true) }
    fun hideCreateDialog() { _uiState.value = _uiState.value.copy(showCreateDialog = false) }
    fun showJoinDialog() { _uiState.value = _uiState.value.copy(showJoinDialog = true) }
    fun hideJoinDialog() { _uiState.value = _uiState.value.copy(showJoinDialog = false) }
    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }
    fun clearJoinSuccess() { _uiState.value = _uiState.value.copy(joinSuccess = null) }
}
