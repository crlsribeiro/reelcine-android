package com.crlsribeiro.reelcine.data.repository

import com.crlsribeiro.reelcine.domain.model.Group
import com.crlsribeiro.reelcine.domain.repository.GroupRepository
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : GroupRepository {

    private val groupsCollection get() = firestore.collection("groups")

    // ─── Read ────────────────────────────────────────────────────────────────

    override fun getUserGroups(userId: String): Flow<List<Group>> = callbackFlow {
        val listener = groupsCollection
            .whereArrayContains("members", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val groups = snapshot?.documents
                    ?.mapNotNull { doc -> doc.toObject(Group::class.java)?.copy(id = doc.id) }
                    ?: emptyList()
                trySend(groups)
            }
        awaitClose { listener.remove() }
    }

    override suspend fun getGroupById(groupId: String): Result<Group> = runCatching {
        val doc = groupsCollection.document(groupId).get().await()
        doc.toObject(Group::class.java)?.copy(id = doc.id)
            ?: error("Group not found: $groupId")
    }

    // ─── Write ───────────────────────────────────────────────────────────────

    override suspend fun createGroup(
        name: String,
        description: String,
        adminId: String
    ): Result<Group> = runCatching {
        val inviteCode = generateInviteCode()
        val data = mapOf(
            "name" to name,
            "description" to description,
            "adminId" to adminId,
            "members" to listOf(adminId),
            "memberCount" to 1,
            "inviteCode" to inviteCode,
            "createdAt" to FieldValue.serverTimestamp()
        )
        val doc = groupsCollection.add(data).await()
        Group(
            id = doc.id,
            name = name,
            description = description,
            adminId = adminId,
            members = listOf(adminId),
            memberCount = 1,
            inviteCode = inviteCode
        )
    }

    override suspend fun joinGroupByCode(
        inviteCode: String,
        userId: String
    ): Result<Group> = runCatching {
        val snapshot = groupsCollection
            .whereEqualTo("inviteCode", inviteCode.uppercase().trim())
            .get()
            .await()

        val doc = snapshot.documents.firstOrNull()
            ?: error("Código inválido ou grupo não encontrado")

        val members = doc.getStringList("members")

        if (!members.contains(userId)) {
            groupsCollection.document(doc.id)
                .update(
                    "members", FieldValue.arrayUnion(userId),
                    "memberCount", members.size + 1
                ).await()
        }

        doc.toObject(Group::class.java)?.copy(id = doc.id)
            ?: error("Erro ao carregar grupo")
    }

    override suspend fun joinGroup(groupId: String, userId: String): Result<Unit> = runCatching {
        groupsCollection.document(groupId)
            .update(
                "members", FieldValue.arrayUnion(userId),
                "memberCount", FieldValue.increment(1)
            ).await()
    }

    override suspend fun leaveGroup(groupId: String, userId: String): Result<Unit> = runCatching {
        firestore.runTransaction { transaction ->
            val ref = groupsCollection.document(groupId)
            val members = transaction.get(ref).getStringList("members")
            val newMembers = members.filter { it != userId }
            transaction.update(ref, "members", newMembers)
            transaction.update(ref, "memberCount", newMembers.size)
        }.await()
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    @Suppress("UNCHECKED_CAST")
    private fun com.google.firebase.firestore.DocumentSnapshot.getStringList(
        field: String
    ): List<String> = get(field) as? List<String> ?: emptyList()

    private fun generateInviteCode(): String =
        ('A'..'Z').plus('0'..'9')
            .let { chars -> (1..6).map { chars.random() }.joinToString("") }
}