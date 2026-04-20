package com.crlsribeiro.reelcine.domain.usecase.group

import com.crlsribeiro.reelcine.domain.model.Group
import com.crlsribeiro.reelcine.domain.repository.GroupRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserGroupsUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    operator fun invoke(userId: String): Flow<List<Group>> =
        groupRepository.getUserGroups(userId)
}
