package com.streamer272.routes

import com.streamer272.dtos.APIBadRequestException
import com.streamer272.dtos.APINotFoundException
import com.streamer272.entities.Member
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.*

fun Application.configureMemberRouting() {
    routing {
        route("/member") {
            authenticate("auth") {
                get("/") {
                    val members = transaction {
                        Member.all().toList().map { it.toDTO() }
                    }
                    call.respond(members)
                }

                get("/{memberId}") {
                    val memberId = call.parameters["memberId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: throw APIBadRequestException("Invalid memberId")

                    val member = transaction {
                        Member.findById(memberId)?.toDTO()
                    } ?: throw APINotFoundException("Member not found")
                    call.respond(member)
                }
            }
        }
    }
}
