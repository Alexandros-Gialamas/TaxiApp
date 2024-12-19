package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_confirm

import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.ViewState

data class RideConfirmState(
    val rideEstimate: RideEstimate? = null,
    val isLoading: Boolean = false,
    val customerId: String = "",
    val origin: String = "",
    val destination: String = ""
) : ViewState