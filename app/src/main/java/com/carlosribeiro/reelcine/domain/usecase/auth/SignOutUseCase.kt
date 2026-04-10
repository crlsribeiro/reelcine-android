package com.carlosribeiro.reelcine.domain.usecase.auth

import com.carlosribeiro.reelcine.domain.repository.AuthRepository
import javax.inject.Inject

class SignOutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.signOut()
}
