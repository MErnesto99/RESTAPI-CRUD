package com.example.plugins

import com.example.routing.authenticationRoutes
import com.example.routing.notesRoutes
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.application.*

fun Application.configureRouting() {

    notesRoutes()
    authenticationRoutes()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
