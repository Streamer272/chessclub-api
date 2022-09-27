package com.streamer272.entities

import com.streamer272.dtos.MemberDTO
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import java.util.*

object MemberTable : UUIDTable() {
    val name: Column<String> = varchar("name", 64)
    val email: Column<String> = varchar("email", 64)
    val grade: Column<Int> = integer("grade")
    val admin: Column<Boolean> = bool("admin")
}

class Member(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Member>(MemberTable)

    val name by MemberTable.name
    val email by MemberTable.email
    val grade by MemberTable.grade
    val admin by MemberTable.admin

    fun toDTO() = MemberDTO(
        id.value.toString(),
        name,
        email,
        grade
    )
}
