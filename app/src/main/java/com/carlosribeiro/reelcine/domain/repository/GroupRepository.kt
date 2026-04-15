package com.carlosribeiro.reelcine.domain.repository

import com.carlosribeiro.reelcine.domain.model.Group
import kotlinx.coroutines.flow.Flow

interface GroupRepository {
    fun getUserGroups(userId: String): Flow<List<Group>>
    suspend fun createGroup(name: String, description: String, adminId: String): Result<Group>
    suspend fun joinGroup(groupId: String, userId: String): Result<Unit>
    suspend fun joinGroupByCode(inviteCode: String, userId: String): Result<Group>
    suspend fun leaveGroup(groupId: String, userId: String): Result<Unit>
    suspend fun getGroupById(groupId: String): Result<Group>
}
