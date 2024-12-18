package com.alexandros.p.gialamas.taxiapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideHistoryRequest(
    @SerialName("customer_id")
    val customerId: String,
    @SerialName("driver_id")
    val driverId: Int? = null
)
