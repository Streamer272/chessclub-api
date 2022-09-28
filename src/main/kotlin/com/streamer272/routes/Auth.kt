package com.streamer272.routes

import com.streamer272.dtos.GenericResponseDTO
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthRouting() {
    routing {
        authenticate("auth") {
            get("/test") {
                call.respond(GenericResponseDTO("Ok"))
            }
        }
    }
}
