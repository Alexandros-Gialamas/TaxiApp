package com.alexandros.p.gialamas.taxiapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.alexandros.p.gialamas.taxiapp.presentation.ui.navigation.NavigationController
import com.alexandros.p.gialamas.taxiapp.presentation.ui.theme.TaxiAppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TaxiAppTheme {
                NavigationController()
            }
        }
    }
}

