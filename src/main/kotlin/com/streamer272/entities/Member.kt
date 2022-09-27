package com.streamer272.entities

import com.streamer272.dtos.MemberDTO
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.Column
import java.util.*

enum class MemberRole {
    PRESIDENT,
    VICE_PRESIDENT,
    SECRETARY,
    TREASURER,
    MEMBER
}

object MemberTable : UUIDTable() {
    val name: Column<String> = varchar("name", 64)
    val email: Column<String> = varchar("email", 64)
    val grade: Column<Int> = integer("grade")
    val role: Column<MemberRole> = enumeration("role", MemberRole::class)
}

class Member(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<Member>(MemberTable)

    var name by MemberTable.name
    var email by MemberTable.email
    var grade by MemberTable.grade
    var role by MemberTable.role

    fun toDTO() = MemberDTO(
        id.value.toString(),
        name,
        email,
        grade
    )
}
