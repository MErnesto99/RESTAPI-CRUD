package com.example

import com.example.dbConnection.DatabaseConnection
import com.example.entity.NotesEntity
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.example.plugins.*
import com.example.routing.notesRoutes
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.ktorm.dsl.insert

fun main() = runBlocking(){
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)

    delay(0L)
}

fun Application.module() {
    configureAuthentication() // it has to come first
    configureSerialization()
    configureRouting()

}
