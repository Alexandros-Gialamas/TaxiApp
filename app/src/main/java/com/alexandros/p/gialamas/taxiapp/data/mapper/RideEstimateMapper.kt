package com.alexandros.p.gialamas.taxiapp.data.mapper

import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.Location as DomainLocation
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption as DomainRideOption
import com.alexandros.p.gialamas.taxiapp.domain.model.Review as DomainReview

fun RideEstimateResponse.toRideEstimate(): RideEstimate {
    return RideEstimate(
        origin = this.origin.toDomainLocation(),
        destination = this.destination.toDomainLocation(),
        distance = this.distance,
        duration = this.duration,
        options = this.options.map { it.toDomainRideOption() }
    )
}

private fun com.alexandros.p.gialamas.taxiapp.data.model.Location.toDomainLocation(): DomainLocation {
    return DomainLocation(
        latitude = this.latitude,
        longitude = this.longitude
    )
}

private fun com.alexandros.p.gialamas.taxiapp.data.model.RideOption.toDomainRideOption(): DomainRideOption {
    return DomainRideOption(
        id = this.id,
        name = this.name,
        description = this.description,
        vehicle = this.vehicle,
        review = this.review.toDomainReview(),
        value = this.value
    )
}

private fun com.alexandros.p.gialamas.taxiapp.data.model.Review.toDomainReview(): DomainReview {
    return DomainReview(
        rating = this.rating,
        comment = this.comment
    )
}