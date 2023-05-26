package com.example.dbConnection

import io.ktor.server.application.*
import org.ktorm.database.Database

object DatabaseConnection{

    val database = Database.connect(
        url = "jdbc:mysql://localhost:3306/notes",
        driver = "com.mysql.cj.jdbc.Driver",
        user = "root",
        password = "_Drip@2023"
    )


}