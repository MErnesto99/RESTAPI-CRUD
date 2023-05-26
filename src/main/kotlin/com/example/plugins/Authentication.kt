package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.Utils.TokenManager
import com.typesafe.config.ConfigFactory
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.config.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureAuthentication() {

    val config = HoconApplicationConfig(ConfigFactory.load())
    val tokenManager = TokenManager(config)
    install(Authentication) {
        jwt {
            verifier(tokenManager.verifyJWTToken())
            realm = config.property("realm").getString()

        validate {jwtCredential->
            if(jwtCredential.payload.getClaim("username").toString() !=""){
                JWTPrincipal(jwtCredential.payload)
            }else{
                null
            }
        }
        }
    }
}