package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ViewState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.UiText

data class RideConfirmState(
    val rideEstimate: RideEstimate? = null,
    val rideOption: RideOption? = null,
    val isRideConfirmed: Boolean = false,
    val isLoading: Boolean = false,
    val customerId: String = "",
    val origin: String = "",
    val destination: String = "",
    val distance: Double = 0.0,
    val duration: String = "",
    val options: List<RideOption> = emptyList(),
    val error: UiText? = null,
) : ViewState