package com.alexandros.p.gialamas.taxiapp.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.RideConfirmScreen
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideEstimateScreen
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.RideHistoryScreen
import kotlinx.serialization.Serializable

@Composable
fun NavigationController(){

    val navController = rememberNavController()
    navController.currentBackStackEntryAsState().value

    NavHost(
        navController = navController,
        startDestination = Screens.RideEstimateScreen,
    ){

        composable<Screens.RideEstimateScreen>{
            RideEstimateScreen(
                onRideSelected = {
                    navController.navigate(Screens.RideConfirmScreen)
                }
            )
        }

        composable<Screens.RideConfirmScreen>{
            RideConfirmScreen(
                onRideConfirmed = {
                    navController.navigate(Screens.RideHistoryScreen)
                }
            )
        }


        composable<Screens.RideHistoryScreen>{
            RideHistoryScreen()
        }

    }

}

@Serializable
sealed class Screens {

    @Serializable
    data object RideEstimateScreen : Screens()

    @Serializable
    data object RideConfirmScreen : Screens()

    @Serializable
    data object RideHistoryScreen : Screens()

}