package com.alexandros.p.gialamas.taxiapp.presentation.ui.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatDistance(distance: Double): String {

    val distanceInKm = distance / 1000

    val formattedDistance = if (distance % 1 == 0.0) {
        String.format("%.3f", distanceInKm)
    } else {
        String.format("%.0f", distanceInKm)
    }
    return "$formattedDistance km"
}

@SuppressLint("DefaultLocale")
fun formatHistoryDistance(distance: Double): String {
    val distanceInKm = String.format("%.2f", distance)
    return "$distanceInKm Km"
}
