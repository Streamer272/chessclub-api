package com.streamer272

import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.streamer272.plugins.*
import io.github.cdimascio.dotenv.dotenv

val dotenv = dotenv()

fun main() {
    embeddedServer(Netty, port = 5001, host = "0.0.0.0") {
        configureHTTP()
        configureRouting()
        configureSecurity()
        configureDatabase()
        configureMonitoring()
        configureSerialization()
        configureExceptionHandling()
    }.start(wait = true)
}
