package com.streamer272.plugins

import com.streamer272.routes.configureAuthRouting
import com.streamer272.routes.configureMeetingRouting
import com.streamer272.routes.configureMemberRouting
import io.ktor.server.application.*

fun Application.configureRouting() {
    configureAuthRouting()
    configureMemberRouting()
    configureMeetingRouting()
}
