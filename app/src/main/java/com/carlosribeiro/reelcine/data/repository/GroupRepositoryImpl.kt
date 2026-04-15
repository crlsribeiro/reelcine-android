package com.carlosribeiro.reelcine.data.repository

import com.carlosribeiro.reelcine.domain.model.Group
import com.carlosribeiro.reelcine.domain.repository.GroupRepository
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : GroupRepository {

    override fun getUserGroups(userId: String): Flow<List<Group>> = callbackFlow {
        val listener = firestore.collection("groups")
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) { close(error); return@addSnapshotListener }
                val groups = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Group::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                trySend(groups)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun createGroup(name: String, description: String, adminId: String): Result<Group> {
        return try {
            val inviteCode = generateInviteCode()
            val group = hashMapOf(
                "name" to name,
                "description" to description,
                "adminId" to adminId,
                "members" to listOf(adminId),
                "memberCount" to 1,
                "inviteCode" to inviteCode
            )
            val doc = firestore.collection("groups").add(group).await()
            Result.success(Group(
                id = doc.id, name = name, description = description,
                adminId = adminId, members = listOf(adminId),
                memberCount = 1, inviteCode = inviteCode
            ))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinGroupByCode(inviteCode: String, userId: String): Result<Group> {
        return try {
            val snapshot = firestore.collection("groups")
                .whereEqualTo("inviteCode", inviteCode.uppercase().trim())
                .get().await()
            val doc = snapshot.documents.firstOrNull()
                ?: return Result.failure(Exception("Código inválido ou grupo não encontrado"))
            val groupId = doc.id
            val members = doc.get("members") as? List<String> ?: emptyList()
            if (!members.contains(userId)) {
                firestore.collection("groups").document(groupId)
                    .update("members", members + userId, "memberCount", members.size + 1)
                    .await()
            }
            val group = doc.toObject(Group::class.java)?.copy(id = groupId)
                ?: return Result.failure(Exception("Erro ao carregar grupo"))
            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun joinGroup(groupId: String, userId: String): Result<Unit> {
        return try {
            val groupRef = firestore.collection("groups").document(groupId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(groupRef)
                val members = snapshot.get("members") as? List<String> ?: emptyList()
                if (!members.contains(userId)) {
                    transaction.update(groupRef, "members", members + userId)
                    transaction.update(groupRef, "memberCount", members.size + 1)
                }
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun leaveGroup(groupId: String, userId: String): Result<Unit> {
        return try {
            val groupRef = firestore.collection("groups").document(groupId)
            firestore.runTransaction { transaction ->
                val snapshot = transaction.get(groupRef)
                val members = snapshot.get("members") as? List<String> ?: emptyList()
                val newMembers = members.filter { it != userId }
                transaction.update(groupRef, "members", newMembers)
                transaction.update(groupRef, "memberCount", newMembers.size)
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getGroupById(groupId: String): Result<Group> {
        return try {
            val doc = firestore.collection("groups").document(groupId).get().await()
            val group = doc.toObject(Group::class.java)?.copy(id = doc.id)
                ?: return Result.failure(Exception("Group not found"))
            Result.success(group)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun generateInviteCode(): String {
        val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return (1..6).map { chars.random() }.joinToString("")
    }
}
