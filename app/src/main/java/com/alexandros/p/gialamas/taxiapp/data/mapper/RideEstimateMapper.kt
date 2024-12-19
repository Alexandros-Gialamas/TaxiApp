package com.alexandros.p.gialamas.taxiapp.data.mapper

import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.model.Location
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption

fun RideEstimateResponse.toRideEstimate(): RideEstimate {
    return RideEstimate(
        origin = Location(
            latitude = this.origin.latitude,
            longitude = this.origin.longitude
        ),
        destination = Location(
            latitude = this.destination.latitude,
            longitude = this.destination.longitude
        ),
        distance = this.distance,
        duration = this.duration,
        options = this.options.map { rideOption ->
            RideOption(
                id = rideOption.id,
                name = rideOption.name,
                description = rideOption.description,
                vehicle = rideOption.vehicle,
                review = rideOption.review,
                value = rideOption.value,
            )
        }
    )
}
