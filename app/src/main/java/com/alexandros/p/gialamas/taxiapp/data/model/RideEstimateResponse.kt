package com.alexandros.p.gialamas.taxiapp.data.model


import com.alexandros.p.gialamas.taxiapp.domain.model.Location
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RideEstimateResponse(
    @SerialName("origin")
    val origin: Location,
    @SerialName("destination")
    val destination: Location,
    @SerialName("distance")
    val distance: Double,
    @SerialName("duration")
    val duration: String,
    @SerialName("options")
    val options: List<RideOption>,
    @SerialName("routeResponse")
    val routeResponse: RouteResponse? = null

)

@Serializable
data object RouteResponse

