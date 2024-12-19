package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.ViewState

data class RideEstimateState(
    val rideEstimate: Result<RideEstimate, RideEstimateError> = Result.Idle,
    val isLoading: Boolean = false,
    val debounceTimeMillis: Long = 60000,
    val customerId: String = "",
    val origin: String = "",
    val destination: String = "",
    val error: UiText? = null
) : ViewState

