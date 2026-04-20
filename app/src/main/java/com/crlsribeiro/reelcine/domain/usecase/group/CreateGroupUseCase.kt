package com.crlsribeiro.reelcine.domain.usecase.group

import com.crlsribeiro.reelcine.domain.model.Group
import com.crlsribeiro.reelcine.domain.repository.GroupRepository
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(
    private val groupRepository: GroupRepository
) {
    suspend operator fun invoke(name: String, description: String, adminId: String): Result<Group> {
        if (name.isBlank()) return Result.failure(IllegalArgumentException("Group name cannot be empty"))
        return groupRepository.createGroup(name, description, adminId)
    }
}
