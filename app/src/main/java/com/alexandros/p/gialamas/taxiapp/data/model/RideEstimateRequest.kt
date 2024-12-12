package com.alexandros.p.gialamas.taxiapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideEstimateRequest(
    @SerialName("customer_id")
    val customerId: String,
    @SerialName("origin")
    val origin: String,
    @SerialName("destination")
    val destination: String
)