package com.streamer272.plugins

import com.streamer272.dotenv
import com.streamer272.entities.MeetingTable
import com.streamer272.entities.MemberTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val dbHost = dotenv["DB_HOST"]
    val dbPort = dotenv["DB_PORT"]
    val dbName = dotenv["DB_NAME"]
    val dbUser = dotenv["DB_USER"]
    val dbPassword = dotenv["DB_PASSWORD"]

    Database.connect(
        "jdbc:postgresql://$dbHost:$dbPort/$dbName",
        "org.postgresql.Driver",
        dbUser,
        dbPassword
    )

    transaction {
        SchemaUtils.create(MeetingTable)
        SchemaUtils.create(MemberTable)
    }
}
