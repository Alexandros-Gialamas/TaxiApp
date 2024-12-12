package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.ViewState

data class RideEstimateState(
    val rideEstimate: Result<RideEstimate, RideEstimateError> = Result.Loading,
    val isLoading: Boolean = false,
    val customerId: String = "CT01",
    val origin: String = "Av. Pres. Kennedy, 2385 - Remédios, Osasco - SP, 02675-031",
    val destination: String = "Av. Paulista, 1538 - Bela Vista, São Paulo - SP, 01310-200"
) : ViewState

