package com.example.model.userModel

import kotlinx.serialization.Serializable
import org.mindrot.jbcrypt.BCrypt

@Serializable
data class UserCredentials(val username:String, val password:String){
    fun hashedPassword():String{
        return BCrypt.hashpw(
            password,
            BCrypt.gensalt())
    }

    fun isValidCredentials():Boolean{
        return username.length >=5 && password.length >=8
    }
}