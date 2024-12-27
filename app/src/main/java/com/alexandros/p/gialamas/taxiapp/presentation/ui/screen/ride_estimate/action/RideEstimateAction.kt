package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.action

import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.Action

sealed interface RideEstimateAction : Action {

    data object CancelApiRequest : RideEstimateAction
    data object LoadingStateToFalse : RideEstimateAction
    data object DelayedErrorClearance : RideEstimateAction
    data object GetRideEstimate : RideEstimateAction

}