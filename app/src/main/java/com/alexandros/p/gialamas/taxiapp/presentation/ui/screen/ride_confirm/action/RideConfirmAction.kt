package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm.action

import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.Action

sealed interface RideConfirmAction : Action {

    data class OptionSelected(val rideOption: RideOption) : RideConfirmAction
    data object ConfirmRide: RideConfirmAction
    data class LoadData(
        val rideEstimate: RideEstimate,
        val customerId: String,
        val origin: String,
        val destination: String
    ) : RideConfirmAction
    data object Debounce: RideConfirmAction
    data object ClearError : RideConfirmAction
    data object Restart: RideConfirmAction

}