package com.alexandros.p.gialamas.taxiapp.data.mapper

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride

fun Ride.toEntity(): RideEntity {
    return RideEntity(
        id = this.id ?: 0,
        date = this.date,
        customerId = this.customerId,
        origin = this.origin,
        destination = this.destination,
        distance = this.distance,
        duration = this.duration,
        driverId = this.driver.id,
        driverName = this.driver.name,
        value = this.value
    )
}

fun RideEntity.toRide(): Ride {
    return Ride(
        id = this.id,
        date = this.date,
        customerId = this.customerId,
        origin = this.origin,
        destination = this.destination,
        distance = this.distance,
        duration = this.duration,
        driver = Driver(this.driverId, this.driverName),
        value = this.value
    )
}

fun ConfirmRideRequest.toRide(
    id: Int? = null,
    date: String? = null,
    customerId: String,
    driver: Driver
): Ride {
    return Ride(
        id = id,
        date = date,
        customerId = customerId,
        origin = this.origin,
        destination = this.destination,
        distance = this.distance,
        duration = this.duration,
        driver = Driver(id = driver.id, name = driver.name),
        value = this.value
    )
}



