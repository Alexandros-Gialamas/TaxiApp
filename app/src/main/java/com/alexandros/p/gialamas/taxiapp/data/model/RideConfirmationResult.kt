package com.alexandros.p.gialamas.taxiapp.data.model

import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideConfirmationResult (
    @SerialName("success")
    val success: Boolean
)