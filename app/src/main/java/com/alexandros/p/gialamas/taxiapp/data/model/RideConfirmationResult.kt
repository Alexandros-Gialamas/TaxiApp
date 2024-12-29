package com.alexandros.p.gialamas.taxiapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideConfirmationResult (
    @SerialName("success")
    val success: Boolean
)