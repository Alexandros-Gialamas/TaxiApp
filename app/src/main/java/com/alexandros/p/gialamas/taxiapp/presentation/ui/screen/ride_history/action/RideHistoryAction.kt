package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.action

import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.Action
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Driver

sealed interface RideHistoryAction : Action {

    data object DelayedErrorClearance : RideHistoryAction
    data class DriverSelectorOnExpandChange(val onExpandChange: Boolean) : RideHistoryAction
    data class DriverSelectorOnDismiss(val onDismiss: Boolean) : RideHistoryAction
    data class DriverSelectorOnDriverSelected(val driver: Driver) : RideHistoryAction
    data object CancelRequest : RideHistoryAction
    data object ConfirmRequest : RideHistoryAction


}