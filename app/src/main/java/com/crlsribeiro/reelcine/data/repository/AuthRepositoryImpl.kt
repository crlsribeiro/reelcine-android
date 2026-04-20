package com.crlsribeiro.reelcine.data.repository

import com.crlsribeiro.reelcine.domain.model.User
import com.crlsribeiro.reelcine.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not found"))
            val user = getUserFromFirestore(firebaseUser.uid) ?: User(
                uid = firebaseUser.uid,
                name = firebaseUser.displayName ?: "",
                email = firebaseUser.email ?: ""
            )
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not found"))

            // ✅ Verifica se usuário já existe no Firestore
            val existingUser = getUserFromFirestore(firebaseUser.uid)

            val user = if (existingUser != null) {
                // ✅ Já existe — preserva nome e bio editados, só atualiza email e avatar
                existingUser.copy(
                    email = firebaseUser.email ?: existingUser.email,
                    avatarUrl = firebaseUser.photoUrl?.toString() ?: existingUser.avatarUrl
                )
            } else {
                // Primeiro login — cria com dados do Google
                User(
                    uid = firebaseUser.uid,
                    name = firebaseUser.displayName ?: "",
                    email = firebaseUser.email ?: "",
                    avatarUrl = firebaseUser.photoUrl?.toString() ?: ""
                )
            }

            // ✅ Salva preservando dados existentes com merge
            saveUserToFirestoreWithMerge(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUp(name: String, email: String, password: String): Result<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not found"))
            val user = User(
                uid = firebaseUser.uid,
                name = name,
                email = email
            )
            saveUserToFirestore(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            uid = firebaseUser.uid,
            name = firebaseUser.displayName ?: "",
            email = firebaseUser.email ?: "",
            avatarUrl = firebaseUser.photoUrl?.toString() ?: ""
        )
    }

    override fun isUserLoggedIn(): Boolean = auth.currentUser != null

    private suspend fun getUserFromFirestore(uid: String): User? {
        return try {
            val doc = firestore.collection("users").document(uid).get().await()
            if (doc.exists()) {
                User(
                    uid = uid,
                    name = doc.getString("name") ?: "",
                    email = doc.getString("email") ?: "",
                    avatarUrl = doc.getString("avatarUrl") ?: "",
                    bio = doc.getString("bio") ?: ""
                )
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        val userMap = mapOf(
            "uid" to user.uid,
            "name" to user.name,
            "email" to user.email,
            "avatarUrl" to user.avatarUrl,
            "bio" to user.bio
        )
        firestore.collection("users").document(user.uid).set(userMap).await()
    }

    // ✅ Salva apenas os campos informados sem apagar os demais
    private suspend fun saveUserToFirestoreWithMerge(user: User) {
        val userMap = mapOf(
            "uid" to user.uid,
            "name" to user.name,
            "email" to user.email,
            "avatarUrl" to user.avatarUrl,
            "bio" to user.bio
        )
        firestore.collection("users").document(user.uid)
            .set(userMap, com.google.firebase.firestore.SetOptions.merge())
            .await()
    }
}