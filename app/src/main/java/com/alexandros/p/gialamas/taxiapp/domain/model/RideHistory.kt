package com.alexandros.p.gialamas.taxiapp.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideHistory (
    @SerialName("id")
    val id: Int? = null,
    @SerialName("date")
    val date: String? = null,
    @SerialName("origin")
    val origin: String,
    @SerialName("destination")
    val destination: String,
    @SerialName("distance")
    val distance: Double,
    @SerialName("duration")
    val duration: String,
    @SerialName("driver")
    val driver: Driver,
    @SerialName("value")
    val value: Double
)




