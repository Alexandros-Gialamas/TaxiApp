package com.alexandros.p.gialamas.taxiapp.presentation.ui.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatDuration(duration: Int): String {
    val hours = duration / 60
    val minutes = duration % 60
    val seconds = (duration * 60) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}

fun formatDurationToReadableString(duration: Int): String {
    val hours = duration / 60
    val minutes = duration % 60
    val seconds = (duration * 60) % 60

    val result = StringBuilder()
    if (hours > 0) {
        result.append("$hours hour${if (hours > 1) "s" else ""} ")
    }
    if (minutes > 0) {
        result.append("$minutes minute${if (minutes > 1) "s" else ""} ")
    }
    if (seconds > 0) {
        result.append("$seconds second${if (seconds > 1) "s" else ""}")
    }

    return result.toString().trim()
}