package com.carlosribeiro.reelcine.domain.usecase.auth

import com.carlosribeiro.reelcine.domain.model.User
import com.carlosribeiro.reelcine.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}
