package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history

import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ViewState
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.UiText
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Driver

sealed class Rides {
    data class Network(val rideHistory: RideHistory) : Rides()
    data class Local(val rideEntity: RideEntity) : Rides()
}

data class RideHistoryState(
    val rideHistory: Result<List<RideHistory>, RideHistoryError> = Result.Idle,
    val localRides: Result<List<RideEntity>, RideHistoryError> = Result.Idle,
    val rides: List<Rides> = emptyList(),
    val isLocalLoading: Boolean = false,
    val isNetworkLoading: Boolean = false,
    val canRequestAgain: Boolean = true,
    val customerId: String = "",
    val driverId: Int? = Driver.DRIVER_ALL.driverId,
    val driverName: String = Driver.DRIVER_ALL.driverName,
    val isDriverMenuExpanded: Boolean = false,
    val networkError: UiText? = null,
    val localError: UiText? = null,
    val isCustomerIdValid: Boolean = true
) : ViewState