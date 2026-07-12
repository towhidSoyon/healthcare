package com.towhid.healthcare.app

import com.towhid.healthcare.core.model.HealthResponse
import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respond(mapOf("hello" to "world"))
        }
        get("/health") {
            call.respond(HealthResponse())
        }
    }
}