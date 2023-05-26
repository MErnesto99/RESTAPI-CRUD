package com.example.routing

import com.example.dbConnection.DatabaseConnection
import com.example.entity.NotesEntity
import com.example.model.noteModel.Note
import com.example.model.noteModel.NoteRequest
import com.example.model.noteModel.MsgResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.ktorm.dsl.*

fun Application.notesRoutes(){
    routing {

        val db=DatabaseConnection.database

        get("/notes"){
            val notes = db.from(NotesEntity)
                .select()
                .map {
                    val id = it[NotesEntity.id]
                    val note = it[NotesEntity.note]
                    Note(id?:-1,note?:"")
                }

            call.respond(notes)
        }

        post ("/notes"){

            val request = call.receive<NoteRequest>()
            val result = db.insert(NotesEntity){
                set(it.note,request.note)
            }
            if (result==1){
                //Send Successful response to the client
                call.respond(HttpStatusCode.OK, MsgResponse(
                    success = true,
                    data = "Note has been added successfully"
                )
                )
            }else{
                //Send a failure response
                call.respond(HttpStatusCode.BadRequest, MsgResponse(
                    success = false,
                    data = "Failed to insert"
                )
                )
            }
        }

        get("/notes/{id}"){
            val idParam = call.parameters["id"]!!.toInt()

            val note = db.from(NotesEntity).select().where{
                NotesEntity.id eq idParam
            }.map {
                val id = it[NotesEntity.id]!!
                val note =it[NotesEntity.note]!!
                Note(id=id,note=note)
            }.firstOrNull()

            if(note==null){
                call.respond(
                HttpStatusCode.NotFound, MsgResponse(
                  success = false,
                    data = "Could not find note with id = $idParam"
                )
                )
            }else{
                call.respond(HttpStatusCode.OK, MsgResponse(
                    success = true,
                    data = note
                )
                )
            }
        }

        put("/notes/{id}"){
            val id = call.parameters["id"]?.toInt()?:-1 //if its not an integer we will change it to -1
            val updatedNote = call.receive<NoteRequest>()

           val rowsAffected = db.update(NotesEntity){
                set(it.note,updatedNote.note)
                where {
                    it.id eq id
                }
            }

            if(rowsAffected==1){
                call.respond(HttpStatusCode.OK,
                    MsgResponse(
                        success = true,
                        data = updatedNote.note
                    )
                )
            }else{
                call.respond(HttpStatusCode.BadRequest, MsgResponse(
                    success = false,
                    data = "Could not update note with id = $id"
                )
                )
            }

        }

        delete ("/notes/{id}"){
            val idParam=call.parameters["id"]?.toInt()?:-1

            val rowsAffected = db.delete(NotesEntity){
                NotesEntity.id eq idParam
            }

            if(rowsAffected==1){
                call.respond(HttpStatusCode.OK, MsgResponse(
                    success = true,
                    data = "Note with id $idParam was deleted"
                )
                )
            }else{
                call.respond(HttpStatusCode.NotFound, MsgResponse(
                    success = false,
                    data = "Oh, looks like something went wrong"
                )
                )
            }

        }

    }
}