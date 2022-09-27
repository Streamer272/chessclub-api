package com.streamer272.plugins

import com.streamer272.dtos.APIException
import com.streamer272.dtos.ErrorResponseDTO
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import java.time.LocalDateTime

fun Application.configureExceptionHandling() {
    install(StatusPages) {
        exception<APIException> { call, cause ->
            call.respond(
                HttpStatusCode.fromValue(cause.code),
                ErrorResponseDTO(
                    cause.code,
                    call.request.path(),
                    LocalDateTime.now().toString(),
                    cause.message,
                    cause.description
                )
            )
        }

        exception<Throwable> { call, cause ->
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponseDTO(
                    HttpStatusCode.InternalServerError.value,
                    call.request.path(),
                    LocalDateTime.now().toString(),
                    cause.message ?: "Unknown error",
                    ""
                )
            )
        }
    }
}
