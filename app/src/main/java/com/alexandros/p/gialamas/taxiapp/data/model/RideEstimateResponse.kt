package com.alexandros.p.gialamas.taxiapp.data.model

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

@Serializable
data class Location(
    @SerialName("latitude")
    val latitude: Double,
    @SerialName("longitude")
    val longitude: Double
)

@Serializable
data class RideOption(
    @SerialName("id")
    val id: Int,
    @SerialName("name")
    val name: String,
    @SerialName("description")
    val description: String,
    @SerialName("vehicle")
    val vehicle: String,
    @SerialName("review")
    val review: Review,
    @SerialName("value")
    val value: Double
)

@Serializable
data class Review(
    @SerialName("rating")
    val rating: Double,
    @SerialName("comment")
    val comment: String
)