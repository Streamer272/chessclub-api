package com.streamer272.dtos

import kotlinx.serialization.Serializable

@Serializable
data class MeetingDTO(
    val id: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val attendance: String
)

@Serializable
data class StartMeetingDTO(
    val date: String,
    val startTime: String
)

@Serializable
data class EndMeetingDTO(
    val endTime: String
)
