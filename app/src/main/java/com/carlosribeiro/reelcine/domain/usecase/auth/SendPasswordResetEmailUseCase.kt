package com.carlosribeiro.reelcine.domain.usecase.auth

import com.carlosribeiro.reelcine.domain.repository.AuthRepository
import javax.inject.Inject

class SendPasswordResetEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Result<Unit> {
        if (email.isBlank()) return Result.failure(IllegalArgumentException("Email cannot be empty"))
        return authRepository.sendPasswordResetEmail(email)
    }
}
