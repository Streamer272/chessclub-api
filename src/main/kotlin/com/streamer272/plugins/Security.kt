package com.streamer272.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*

fun Application.configureSecurity() {
    authentication {
        basic("auth") {
            realm = "Admin"
            validate { credentials ->
                if (credentials.name == "administrator" && credentials.password == "k6YBM8Wv") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}
