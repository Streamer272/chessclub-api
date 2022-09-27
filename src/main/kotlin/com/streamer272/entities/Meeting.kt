package com.streamer272.entities

import com.streamer272.dtos.MeetingDTO
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.javatime.date
import org.jetbrains.exposed.sql.javatime.time
import java.time.LocalDate
import java.time.LocalTime
import java.util.*

object MeetingTable : UUIDTable() {
    val date: Column<LocalDate> = date("date")
    val startTime: Column<LocalTime> = time("start_time")
    val endTime: Column<LocalTime?> = time("end_time").nullable()
    val attendance: Column<String> = text("attendance")
}

class Meeting(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Meeting>(MeetingTable)

    var date by MeetingTable.date
    var startTime by MeetingTable.startTime
    var endTime by MeetingTable.endTime
    var attendance by MeetingTable.attendance

    fun toDTO() = MeetingDTO(
        id.value.toString(),
        date.toString(),
        startTime.toString(),
        endTime.toString(),
        attendance
    )
}
