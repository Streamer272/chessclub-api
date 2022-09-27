package com.streamer272.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MemberDTO(
    val id: String,
    val name: String,
    val email: String,
    val grade: Int,
    val role: String
)

@Serializable
data class CreateMemberDTO(
    val name: String,
    val email: String,
    val grade: Int
)

@Serializable
data class UpdateMemberRoleDTO(
    val role: String
)
