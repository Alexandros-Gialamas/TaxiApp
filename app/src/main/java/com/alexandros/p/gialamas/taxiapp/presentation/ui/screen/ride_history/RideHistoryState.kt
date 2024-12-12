package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.ViewState

data class RideHistoryState(
    val rideHistory: Result<List<Ride>, RideHistoryError> = Result.Loading,
    val isLoading: Boolean = false,
    val customerId: String = "CT01",
    val driverId: Int = 1
) : ViewState