package com.king250

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/background/mobile") {
            val result = getRandomImage("mobile")
            if (result == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                call.respondRedirect(result)
            }
        }
        get("/background/desktop") {
            val result = getRandomImage("desktop")
            if (result == null) {
                call.respond(HttpStatusCode.BadRequest)
            } else {
                call.respondRedirect(result)
            }
        }
    }
}