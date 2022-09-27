package com.streamer272.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MemberDTO(
    val id: String,
    val name: String,
    val email: String,
    val grade: Int
)
