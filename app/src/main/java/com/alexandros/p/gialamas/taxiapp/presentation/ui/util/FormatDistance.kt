package com.alexandros.p.gialamas.taxiapp.presentation.ui.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatDistance(distance: Double): String {
    val formattedDistance = if (distance % 1 == 0.0) {
        String.format("%.3f", distance)
    } else {
        String.format("%.0f", distance)
    }
    return "$formattedDistance km"
}