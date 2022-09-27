package com.streamer272.routes

import com.streamer272.dtos.*
import com.streamer272.entities.Meeting
import com.streamer272.entities.Member
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.transactions.transaction
import java.lang.Exception
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

fun Application.configureMeetingRouting() {
    routing {
        route("/meeting") {
            authenticate("basic") {
                get("/") {
                    val meetings = transaction {
                        Meeting.all().map { it.toDTO() }
                    }
                    call.respond(meetings)
                }

                get("/{meetingId}") {
                    val meetingId = call.parameters["meetingId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: throw APIBadRequestException("Invalid meetingId")

                    val meeting = transaction {
                        Meeting.findById(meetingId)?.toDTO()
                    } ?: throw APINotFoundException("Meeting not found")
                    call.respond(meeting)
                }

                post("/{meetingId}") {
                    val data = call.receive<EndMeetingDTO>()
                    val meetingId = call.parameters["meetingId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: throw APIBadRequestException("Invalid meetingId")

                    transaction {
                        val meeting = Meeting.findById(meetingId) ?: throw APINotFoundException("Meeting not found")
                        meeting.endTime = LocalTime.parse(data.endTime)
                    }
                    call.respond(GenericResponseDTO("Meeting ended"))
                }

                put("/") {
                    val data = call.receive<StartMeetingDTO>()
                    val emptyAttendance = mutableMapOf<String, Boolean>()

                    val meeting = transaction {
                        Member.all().forEach { member -> emptyAttendance[member.id.value.toString()] = false }

                        Meeting.new {
                            date = LocalDate.parse(data.date)
                            startTime = LocalTime.parse(data.startTime)
                            endTime = null
                            attendance = Json.encodeToString(emptyAttendance)
                        }
                    }
                    call.respond(meeting.toDTO())
                }
            }
        }
    }
}
