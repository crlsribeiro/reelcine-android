package com.crlsribeiro.reelcine.domain.usecase.auth

import com.crlsribeiro.reelcine.domain.model.User
import com.crlsribeiro.reelcine.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<User> {
        if (email.isBlank() || password.isBlank()) {
            return Result.failure(IllegalArgumentException("Email and password cannot be empty"))
        }
        return authRepository.signInWithEmail(email, password)
    }
}
