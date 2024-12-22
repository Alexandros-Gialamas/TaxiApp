package com.alexandros.p.gialamas.taxiapp.data.model


import com.alexandros.p.gialamas.taxiapp.domain.model.Location
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


sealed class RideEstimateResponse{

    @Serializable
    data class EstimateResponse(
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
    ): RideEstimateResponse()


    data class Error(val response: HttpResponse) : RideEstimateResponse()

    @Serializable
    data object RouteResponse
}





