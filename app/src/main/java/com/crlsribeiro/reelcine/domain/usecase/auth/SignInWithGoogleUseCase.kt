package com.crlsribeiro.reelcine.domain.usecase.auth

import com.crlsribeiro.reelcine.domain.model.User
import com.crlsribeiro.reelcine.domain.repository.AuthRepository
import javax.inject.Inject

class SignInWithGoogleUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Result<User> {
        if (idToken.isBlank()) {
            return Result.failure(IllegalArgumentException("Google ID token cannot be empty"))
        }
        return authRepository.signInWithGoogle(idToken)
    }
}
