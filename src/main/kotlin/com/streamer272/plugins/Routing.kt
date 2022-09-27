package com.streamer272.plugins

import com.streamer272.routes.configureMemberRouting
import io.ktor.server.application.*

fun Application.configureRouting() {
    configureMemberRouting()
}
