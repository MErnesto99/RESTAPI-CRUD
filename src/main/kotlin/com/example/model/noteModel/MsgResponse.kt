package com.example.model.noteModel

import kotlinx.serialization.Serializable

@Serializable
data class MsgResponse <T>(
    val data: T,
    val success: Boolean
)