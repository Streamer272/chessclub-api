package com.streamer272.dtos

import io.ktor.http.*
import kotlinx.serialization.Serializable

data class APIException(
    val code: Int,
    override val message: String,
    val description: String? = null
) : Exception(message)

fun APIBadRequestException(message: String, description: String? = null) =
    APIException(HttpStatusCode.BadRequest.value, message, description)

fun APINotFoundException(
    message: String,
    description: String? = null
) = APIException(HttpStatusCode.NotFound.value, message, description)

@Serializable
data class ErrorResponseDTO(
    val statusCode: Int,
    val path: String,
    val timestamp: String,
    val message: String,
    val description: String? = null
)
