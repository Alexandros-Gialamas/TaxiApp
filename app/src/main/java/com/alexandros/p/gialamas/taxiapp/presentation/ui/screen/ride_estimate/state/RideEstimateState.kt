package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.state

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ViewState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.UiText


data class RideEstimateState(
    val rideEstimate: Result<RideEstimate, RideEstimateError> = Result.Idle,
    val isLoading: Boolean = false,
//    val canRequestAgain: Boolean = true,
    val customerId: String = "",
    val origin: String = "",
    val destination: String = "",
    val error: UiText? = null,
    val isCustomerIdValid: Boolean = true,
    val isOriginValid: Boolean = true,
    val isDestinationValid: Boolean = true,
    val isRideEstimateCallReady: Boolean = true
) : ViewState

