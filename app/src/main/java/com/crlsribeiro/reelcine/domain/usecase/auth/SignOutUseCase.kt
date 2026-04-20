package com.crlsribeiro.reelcine.domain.usecase.auth

import com.crlsribeiro.reelcine.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.signOut()
}
