package com.streamer272.routes

import com.streamer272.dtos.*
import com.streamer272.entities.Member
import com.streamer272.entities.MemberRole
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
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

                get("/roles") {
                    call.respond(MemberRole.values().toList())
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

                post("/role") {
                    val updateDTO = call.receive<UpdateMemberRoleDTO>()
                    val memberId = try {
                        UUID.fromString(updateDTO.memberId)
                    } catch (e: Exception) {
                        throw APIBadRequestException("Invalid memberId")
                    }
                    val memberRole = try {
                        MemberRole.valueOf(updateDTO.role)
                    } catch (e: Exception) {
                        throw APIBadRequestException("Invalid role")
                    }

                    val member = transaction {
                        val member = Member.findById(memberId) ?: throw APINotFoundException("Member not found")
                        member.role = memberRole
                        member
                    }

                    call.respond(member.toDTO())
                }

                put("/") {
                    val createMemberDTO = call.receive<CreateMemberDTO>()
                    val newMember = transaction {
                        Member.new {
                            name = createMemberDTO.name
                            email = createMemberDTO.email
                            grade = createMemberDTO.grade
                            role = MemberRole.MEMBER
                        }
                    }
                    call.respond(newMember)
                }

                delete("/{memberId}") {
                    val memberId = call.parameters["memberId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: throw APIBadRequestException("Invalid memberId")

                    transaction {
                        Member.findById(memberId)?.delete()
                    }
                    call.respond(GenericResponseDTO("Member deleted"))
                }
            }
        }
    }
}
