package com.alexandros.p.gialamas.taxiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.alexandros.p.gialamas.taxiapp.presentation.ui.navigation.NavigationController
import com.alexandros.p.gialamas.taxiapp.presentation.ui.theme.HideSystemBars
import com.alexandros.p.gialamas.taxiapp.presentation.ui.theme.SetSystemBarColors
import com.alexandros.p.gialamas.taxiapp.presentation.ui.theme.TaxiAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaxiAppTheme {
                HideSystemBars()
                SetSystemBarColors()
                NavigationController()
            }
        }
    }
}

