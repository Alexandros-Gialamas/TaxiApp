package com.alexandros.p.gialamas.taxiapp.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.RideConfirmScreen
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideEstimateScreen
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.RideHistoryScreen
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf



inline fun <reified T : Any> navigationCustomArgument(isNullable: Boolean = false): Pair<KType, CustomNavType<T>> {
    val serializer: KSerializer<T> = serializer()
    return typeOf<T>() to CustomNavType(serializer, isNullable)
}

@Composable
fun NavigationController() {

    val navController = rememberNavController()
    navController.currentBackStackEntryAsState().value

    NavHost(
        navController = navController,
        startDestination = Screens.RideEstimateScreen,
    ) {


        composable<Screens.RideEstimateScreen> {
            RideEstimateScreen(
                onRideSelected = { result: RideEstimate, customerId: String, origin: String, destination: String ->
                    navController.navigate(
                        Screens.RideConfirmScreen(
                            result,
                            customerId,
                            origin,
                            destination
                        )
                    )
                }
            )
        }

        composable<Screens.RideConfirmScreen>(
            typeMap = mapOf(navigationCustomArgument<RideEstimate>())
        ) {
            val args = it.toRoute<Screens.RideConfirmScreen>()
            RideConfirmScreen(
                result = args.rideEstimate,
                customerId = args.customerId,
                origin = args.origin,
                destination = args.destination,
                onRideConfirmed = { rideOption ->
                    navController.navigate(Screens.RideHistoryScreen(rideOption))
                }
            )
        }


        composable<Screens.RideHistoryScreen>(
            typeMap = mapOf(navigationCustomArgument<RideOption>())
        ) {
            val args = it.toRoute<Screens.RideHistoryScreen>()
            RideHistoryScreen(
                rideOption = args.rideOption
            )
        }

    }

}

@Serializable
sealed class Screens {

    @Serializable
    data object RideEstimateScreen : Screens()

    @Serializable
    data class RideConfirmScreen(
        val rideEstimate: RideEstimate,
        val customerId: String,
        val origin: String,
        val destination: String,
    ) : Screens()

    @Serializable
    data class RideHistoryScreen(val rideOption: RideOption) : Screens()

}