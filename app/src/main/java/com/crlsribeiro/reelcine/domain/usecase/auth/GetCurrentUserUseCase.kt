package com.crlsribeiro.reelcine.domain.usecase.auth

import com.crlsribeiro.reelcine.domain.model.User
import com.crlsribeiro.reelcine.domain.repository.AuthRepository
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): User? = authRepository.getCurrentUser()
}
