package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.Driver
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.ViewState

data class RideHistoryState(
    val rideHistory: Result<List<RideHistory>, RideHistoryError> = Result.Idle,
    val localRides: Result<List<Ride>, RideHistoryError> = Result.Idle,
    val isLocalLoading: Boolean = false,
    val isNetworkLoading: Boolean = false,
    val customerId: String = "",
    val driverId: Int? = Driver.DRIVER_ALL.driverId,
    val driverName: String = Driver.DRIVER_ALL.driverName,
    val isDriverMenuExpanded: Boolean = false,
    val error: UiText? = null,
    val isCustomerIdValid: Boolean = true
) : ViewState