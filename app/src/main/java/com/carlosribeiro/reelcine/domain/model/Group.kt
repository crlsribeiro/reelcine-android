package com.carlosribeiro.reelcine.domain.model

data class Group(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val adminId: String = "",
    val members: List<String> = emptyList(),
    val memberCount: Int = 0,
    val lastRecommendation: Recommendation? = null
)
