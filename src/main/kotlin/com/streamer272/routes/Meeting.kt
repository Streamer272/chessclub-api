package com.streamer272.routes

import com.streamer272.dtos.*
import com.streamer272.entities.Meeting
import com.streamer272.entities.Member
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.decodeFromString
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
            authenticate("auth") {
                /**
                 * Get all meetings
                 *
                 * @return List<MeetingDTO>
                 */
                get("/") {
                    val meetings = transaction {
                        Meeting.all().map { it.toDTO() }
                    }
                    call.respond(meetings)
                }

                /**
                 * Get a meeting by id
                 *
                 * @property meetingId The id of the meeting to get
                 * @throws APIBadRequestException If the meeting id is not a valid UUID
                 * @throws APINotFoundException If the meeting does not exist
                 * @return MeetingDTO
                 */
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

                /**
                 * Edit meeting attendance
                 *
                 * @property meetingId The id of the meeting to edit
                 * @property member The member whose attendance is being edited
                 * @property present The new attendance status
                 * @throws APIBadRequestException if the meetingId is invalid
                 * @throws APINotFoundException if the meeting is not found
                 * @return The new attendance
                 */
                post("/{meetingId}/attendance") {
                    val data = call.receive<EditAttendanceDTO>()
                    val meetingId = call.parameters["meetingId"]?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: Exception) {
                            null
                        }
                    } ?: throw APIBadRequestException("Invalid meetingId")

                    val attendance = transaction {
                        val meeting = Meeting.findById(meetingId) ?: throw APINotFoundException("Meeting not found")
                        val attendance = Json.decodeFromString<MutableMap<String, Boolean>>(meeting.attendance)
                        if (attendance[data.member] == null) throw APINotFoundException("Member not found")
                        attendance[data.member] = data.present
                        meeting.attendance = Json.encodeToString(attendance)
                        attendance
                    }
                    call.respond(attendance)
                }

                /**
                 * End a meeting, saving its endTime
                 *
                 * @property meetingId The id of the meeting
                 * @property endTime The time the meeting ended
                 * @throws APINotFoundException If the meeting is not found
                 * @throws APIBadRequestException If the meeting has already ended
                 * @return Text saying the meeting was ended
                 */
                post("/{meetingId}/end") {
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

                /**
                 * Create a new meeting, saving its date and startTime
                 *
                 * @property date The date of the meeting
                 * @property location The location of the meeting
                 * @property orderedBy The member who ordered the meeting
                 * @property startTime The time the meeting started
                 * @throws APINotFoundException If the member who ordered the meeting is not found
                 * @return The new meeting
                 */
                put("/") {
                    val data = call.receive<StartMeetingDTO>()
                    val emptyAttendance = mutableMapOf<String, Boolean>()
                    val orderedById = data.orderedBy?.let {
                        try {
                            UUID.fromString(it)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    val meeting = transaction {
                        Member.all().forEach { member -> emptyAttendance[member.id.value.toString()] = false }

                        Meeting.new {
                            date = LocalDate.parse(data.date)
                            location = data.location
                            orderedBy = orderedById?.let {
                                Member.findById(it)?.id?.value?.toString()
                                    ?: throw APINotFoundException("Member not found")
                            }
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
