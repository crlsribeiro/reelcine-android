package com.carlosribeiro.reelcine.domain.repository

import com.carlosribeiro.reelcine.domain.model.User

interface AuthRepository {
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signUp(name: String, email: String, password: String): Result<User>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signOut()
    fun getCurrentUser(): User?
    fun isUserLoggedIn(): Boolean
}
