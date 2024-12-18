package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.Driver
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.ViewState

data class RideHistoryState(
    val rideHistory: Result<List<Ride>, RideHistoryError> = Result.Idle,
    val localRides: Result<List<Ride>, RideHistoryError> = Result.Idle,
    val isLoading: Boolean = false,
    val customerId: String = "",
    val driver: com.alexandros.p.gialamas.taxiapp.domain.model.Driver = com.alexandros.p.gialamas.taxiapp.domain.model.Driver(
        id = 1,
        name = Driver.DRIVER_1.driverName
    ),
    val driverId: Int? = 2,
    val driverName: String = Driver.DRIVER_2.driverName,
    val isDriverMenuExpanded: Boolean = false
) : ViewState