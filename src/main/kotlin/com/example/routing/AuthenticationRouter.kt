package com.example.routing

import com.example.Utils.TokenManager
import com.example.dbConnection.DatabaseConnection
import com.example.entity.UserEntity
import com.example.model.noteModel.MsgResponse
import com.example.model.userModel.User
import com.example.model.userModel.UserCredentials
import com.typesafe.config.ConfigFactory
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*
import org.mindrot.jbcrypt.BCrypt

fun Application.authenticationRoutes(){

    val db = DatabaseConnection.database
    var tokenManager=TokenManager(HoconApplicationConfig(ConfigFactory.load()))

    routing {
        post ("/register"){

            val userCredentials = call.receive<UserCredentials>()// reads username and pass from the body

            if (!userCredentials.isValidCredentials()){
                call.respond(HttpStatusCode.BadRequest,
                    MsgResponse(success = false,
                            data ="username should be greater than 5 and " +
                            "password should be greater than 8" )
                )
                return@post
            }
            val username= userCredentials.username.lowercase()
            val password = userCredentials.hashedPassword()

            //check if username exists
            val userCheck= db
                .from(UserEntity)
                .select()
                .where{UserEntity.username eq username}
                .map {
                    it[UserEntity.username]
                }.firstOrNull()

            if(userCheck != null){
                call.respond(HttpStatusCode.BadRequest
                    ,MsgResponse(
                        success = false,
                        data = "The user already exists, please try a different username"
                    ))
                return@post //When we use return post,
            }

          val user=  db.insert(UserEntity){
            set(it.username,username)
            set(it.password,password)
          }

        call.respond(HttpStatusCode.Created,MsgResponse(
            success = true,
            data = "User has been successfully created"))

        }

        post("/login"){
            val userCredentials = call.receive<UserCredentials>()// reads username and pass from the body

            if (!userCredentials.isValidCredentials()){
                call.respond(HttpStatusCode.BadRequest,
                    MsgResponse(success = false,
                        data ="username should be greater than 5 and " +
                                "password should be greater than 8" ))
                return@post
            }
            val username= userCredentials.username.lowercase()
            val password = userCredentials.password

            //Check if user exists

            val user = db.from(UserEntity)
                .select()
                .where {
                    UserEntity.username eq username
                }.map {
                    val id = it[UserEntity.id]!!
                    val username = it[UserEntity.username]!!
                    val password = it[UserEntity.password]!!

                     User(id,username,password)
                }.firstOrNull()

            if(user == null){
                call.respond(HttpStatusCode.BadRequest
                    ,MsgResponse(
                        success = false,
                        data = "Invalid username or password"
                    ))
                return@post //When we use return post,
            }

            val doesPassMach = BCrypt.checkpw(password,user?.password)

            if(!doesPassMach){
                call.respond(HttpStatusCode.BadRequest
                    ,MsgResponse(
                        success = false,
                        data = "Invalid username or password"
                    ))
                return@post
            }

            val token = tokenManager.generateJWTToken(user)
            call.respond(HttpStatusCode.OK,
                MsgResponse(success = true,
                            data = token))

        }

        authenticate( ){
            get("/me"){
                val principal=call.principal<JWTPrincipal>()
                val username =principal!!.payload.getClaim("username").asString()
                val userId = principal!!.payload.getClaim("userId").asInt()
                call.respondText("hello $username with $userId")
            }
        }

    }

}