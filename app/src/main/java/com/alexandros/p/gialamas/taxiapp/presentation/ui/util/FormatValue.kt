package com.alexandros.p.gialamas.taxiapp.presentation.ui.util

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun formatValue(value: Double): String {
    val formattedValue = String.format("%.2f", value)
    return formattedValue
}