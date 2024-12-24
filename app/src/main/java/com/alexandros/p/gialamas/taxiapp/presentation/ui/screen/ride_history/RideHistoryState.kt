package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Driver
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.UiText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ViewState

data class RideHistoryState(
    val rideHistory: Result<List<Any>, RideHistoryError> = Result.Idle,
    val localRides: Result<List<Ride>, RideHistoryError> = Result.Idle,
    val isLocalLoading: Boolean = false,
    val isNetworkLoading: Boolean = false,
    val canRequestAgain: Boolean = true,
    val customerId: String = "",
    val driverId: Int? = Driver.DRIVER_ALL.driverId,
    val driverName: String = Driver.DRIVER_ALL.driverName,
    val isDriverMenuExpanded: Boolean = false,
    val error: UiText? = null,
    val isCustomerIdValid: Boolean = true
) : ViewState