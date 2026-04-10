package com.carlosribeiro.reelcine.data.remote.dto

data class VideoDto(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val type: String
)

data class VideoListResponse(
    val results: List<VideoDto>
)
