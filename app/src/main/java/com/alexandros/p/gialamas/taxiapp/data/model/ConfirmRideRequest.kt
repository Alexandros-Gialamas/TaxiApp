package com.alexandros.p.gialamas.taxiapp.data.model

import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConfirmRideRequest(
   @SerialName("customer_id")
   val customerId: String,
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
