package com.towhid.healthcare.infrastructure.database

import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection

@Serializable
data class DatabaseHealthResponse(
    val success: Boolean,
    val database: String
)

fun Application.configureDatabaseHealth() {
    routing {
        get("/health/db") {
            var dbStatus = "DOWN"
            var isSuccess = false
            try {
                transaction {
                    // Execute a simple query to check the database connection
                    exec("SELECT 1")
                    dbStatus = "UP"
                    isSuccess = true
                }
            } catch (e: Exception) {
                dbStatus = "DOWN"
            }
            call.respond(DatabaseHealthResponse(isSuccess, dbStatus))
        }
    }
}
