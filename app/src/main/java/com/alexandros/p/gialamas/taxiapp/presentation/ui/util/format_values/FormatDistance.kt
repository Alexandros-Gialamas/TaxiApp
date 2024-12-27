package com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values

import android.annotation.SuppressLint
import java.text.DecimalFormat

@SuppressLint("DefaultLocale")
fun formatDistance(distance: Double): String {

    val distanceInKm = distance / 1000

    val formattedDistance = if (distance % 1 == 0.0) {
        String.format("%.3f", distanceInKm)
    } else {
        String.format("%.0f", distanceInKm)
    }
    return formattedDistance
}

@SuppressLint("DefaultLocale")
fun formatHistoryDistance(distance: Double): String {
    val distanceInKm = String.format("%.2f", distance)
    return "$distanceInKm Km"
}

fun formatDistanceForRideEntity(distanceInMeters: Double): Double {
    val distanceInKilometers = distanceInMeters / 1000.0
    val df = DecimalFormat("#.##########")
    val formattedDistanceString = df.format(distanceInKilometers)
    return formattedDistanceString.toDoubleOrNull() ?: distanceInKilometers
}
