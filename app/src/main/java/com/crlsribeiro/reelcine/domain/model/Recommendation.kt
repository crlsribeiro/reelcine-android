package com.crlsribeiro.reelcine.domain.model

// Certifique-se de que o import seja EXATAMENTE este:
import com.google.firebase.Timestamp

data class Recommendation(
    val id: String = "",
    val movieId: Int = 0,
    val movieTitle: String = "",
    val posterPath: String = "",
    val backdropPath: String = "",
    val comment: String = "",
    val rating: Float = 0f,
    val userId: String = "",
    val userName: String = "",
    val userAvatar: String = "",
    val groupId: String = "",
    // Mude para Timestamp e inicialize com o valor atual por padrão
    val timestamp: Timestamp = Timestamp.now(),
    val likes: List<String> = emptyList(),
    val likesCount: Int = 0
)