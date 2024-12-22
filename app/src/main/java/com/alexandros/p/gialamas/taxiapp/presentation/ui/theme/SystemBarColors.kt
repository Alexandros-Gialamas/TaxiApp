package com.alexandros.p.gialamas.taxiapp.presentation.ui.theme

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import android.graphics.Color
import androidx.core.view.WindowCompat

@Composable
fun ComponentActivity.SetSystemBarColors() {

    val isSystemInDarkTheme = isSystemInDarkTheme()
    WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightNavigationBars = isSystemInDarkTheme

    DisposableEffect(!isSystemInDarkTheme) {
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = Color.TRANSPARENT,
                darkScrim = Color.TRANSPARENT,
                detectDarkMode = { !isSystemInDarkTheme }
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = lightScrim,
                darkScrim = darkScrim,
                detectDarkMode = { !isSystemInDarkTheme }
            ),

        )
        onDispose {}
    }
}

val lightScrim = android.graphics.Color.argb(0xe6, 0xFF, 0xFF, 0xFF)

val darkScrim = android.graphics.Color.argb(0x80, 0x1b, 0x1b, 0x1b)