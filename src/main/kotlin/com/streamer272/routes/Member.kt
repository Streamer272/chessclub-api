package com.streamer272.routes

import com.streamer272.dtos.*
import com.streamer272.entities.Member
import com.streamer272.entities.MemberRole
import com.streamer272.entities.MemberTable
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
                /**
                 * Get all members
                 *
                 * @return List<MemberDTO>
                 */
                get("/") {
                    val members = transaction {
                        Member.all().toList().map { it.toDTO() }
                    }
                    call.respond(members)
                }

                get("/admins") {
                    val members = transaction {
                        Member.find { MemberTable.role neq MemberRole.MEMBER }.toList().map { it.toDTO() }
                    }
                    call.respond(members)
                }

                /**
                 * Get all roles
                 *
                 * @return List<String>
                 */
                get("/roles") {
                    call.respond(MemberRole.values().toList())
                }

                /**
                 * Get a member by id
                 *
                 * @property memberId The id of the member to get
                 * @throws APIBadRequestException If the member id is not a valid UUID
                 * @throws APINotFoundException If the member does not exist
                 * @return MemberDTO
                 */
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

                /**
                 * Edit member role
                 *
                 * @property memberId The id of the member to edit
                 * @property role The new role of the member
                 * @throws APIBadRequestException If the member id is not a valid UUID
                 * @throws APINotFoundException If the member does not exist
                 * @return MemberDTO
                 */
                post("/{memberId}") {
                    val data = call.receive<UpdateMemberRoleDTO>()
                    val memberId = try {
                        UUID.fromString(call.parameters["memberId"] ?: "")
                    } catch (e: Exception) {
                        throw APIBadRequestException("Invalid memberId")
                    }
                    val memberRole = try {
                        MemberRole.valueOf(data.role.uppercase())
                    } catch (e: Exception) {
                        throw APIBadRequestException("Invalid role")
                    }
                    println("Updating role for $memberId to $memberRole")

                    val member = transaction {
                        val member = Member.findById(memberId) ?: throw APINotFoundException("Member not found")
                        member.role = memberRole
                        member
                    }

                    call.respond(member.toDTO())
                }

                /**
                 * Create a new member
                 *
                 * @property name The name of the new member
                 * @property email The email of the new member
                 * @property grade The grade of the new member
                 * @throws APIBadRequestException If the member id is not a valid UUID
                 * @throws APINotFoundException If the member does not exist
                 * @return MemberDTO
                 */
                put("/") {
                    val data = call.receive<CreateMemberDTO>()
                    val newMember = transaction {
                        Member.new {
                            name = data.name
                            email = data.email
                            grade = data.grade
                            role = MemberRole.MEMBER
                        }
                    }
                    call.respond(newMember.toDTO())
                }

                /**
                 * Delete a member
                 *
                 * @property memberId The id of the member to delete
                 * @throws APIBadRequestException If the member id is not a valid UUID
                 * @throws APINotFoundException If the member does not exist
                 * @return MemberDTO
                 */
                delete("/{memberId}") {
                    val memberId = call.parameters["memberId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: throw APIBadRequestException("Invalid memberId")

                    transaction {
                        val member = Member.findById(memberId) ?: throw APINotFoundException("Member not found")
                        member.delete()
                    }
                    call.respond(GenericResponseDTO("Member deleted"))
                }
            }
        }
    }
}
