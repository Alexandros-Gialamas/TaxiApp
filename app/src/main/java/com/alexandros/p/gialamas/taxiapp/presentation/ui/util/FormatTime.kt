package com.alexandros.p.gialamas.taxiapp.presentation.ui.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat

@SuppressLint("SimpleDateFormat")
fun extractDate(dateTime: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val date = dateFormat.parse(dateTime) ?: return ""

    val dateFormatter = SimpleDateFormat("yyyy-MM-dd")
    return dateFormatter.format(date)
}

@SuppressLint("SimpleDateFormat")
fun extractTime(dateTime: String): String {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
    val date = dateFormat.parse(dateTime) ?: return ""

    val timeFormatter = SimpleDateFormat("HH:mm:ss")
    return timeFormatter.format(date).replace("T", " ")
}