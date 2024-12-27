package com.alexandros.p.gialamas.taxiapp.data.mapper

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDistanceForRideEntity
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDurationForRideEntity
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDurationTimeToString
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDurationToReadableString
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun Ride.toEntity(): RideEntity {

    val currentDate =
        SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss",
            Locale.getDefault()
        ).format(
            Date()
        )
    val distance = formatDistance(this.distance).toDouble()
    val duration = formatDurationToReadableString(this.duration.toInt())
    return RideEntity(
//        id = this.id ?: 0,
        date = currentDate,
        customerId = this.customerId,
        origin = this.origin,
        destination = this.destination,
        distance = distance,
        duration = duration,
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

    return Ride(
        customerId = this.customerId,
        origin = this.origin,
        destination = this.destination,
        distance = this.distance,
        duration = this.duration,
        driver = Driver(id = this.driver.id, name = this.driver.name),
        value = this.value
    )
}



