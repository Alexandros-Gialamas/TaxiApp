package com.alexandros.p.gialamas.taxiapp.data.mapper

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Ride.toEntity(): RideEntity {
    return RideEntity(
//        id = this.id ?: 0,
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
        driver = Driver(
            id = this.driverId,
            name = this.driverName
        ),
        value = this.value
    )
}

fun ConfirmRideRequest.toRide(
): Ride {
    val currentDate =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(
            Date()
        )
    return Ride(
        customerId = this.customerId,
        date = currentDate,
        origin = this.origin,
        destination = this.destination,
        distance = this.distance,
        duration = this.duration,
        driver = Driver(id = this.driver.id, name = this.driver.name),
        value = this.value
    )
}



