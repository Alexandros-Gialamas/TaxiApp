package com.alexandros.p.gialamas.taxiapp.presentation.ui.theme

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.app.ComponentActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

@SuppressLint("RestrictedApi")
@Composable
fun HideSystemBars(){

    val view = LocalView.current
    val window = (view.context as? ComponentActivity)?.window
        ?: throw IllegalStateException("Not in an activity")

    val insetsController = WindowCompat.getInsetsController(window, view)

    LaunchedEffect (Unit){
        hideSystemBars(insetsController)
    }

}

private fun hideSystemBars(insetsController: WindowInsetsControllerCompat) {
    // Hide both status bar and navigation bar
    insetsController.hide(WindowInsetsCompat.Type.systemBars())
    insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

}