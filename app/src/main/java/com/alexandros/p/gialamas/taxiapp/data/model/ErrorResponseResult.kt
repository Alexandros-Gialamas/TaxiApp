package com.alexandros.p.gialamas.taxiapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponseResult(
    @SerialName("error_code")
    val errorCode: String,
    @SerialName("error_description")
    val errorDescription: String
)

