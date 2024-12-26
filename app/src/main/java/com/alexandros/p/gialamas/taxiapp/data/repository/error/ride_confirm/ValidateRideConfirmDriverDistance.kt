package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_confirm

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideConfirmationResult
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Driver
import javax.inject.Inject

class ValidateRideConfirmDriverDistance @Inject constructor() {

    fun validation(
        rideRequest: ConfirmRideRequest,
    ): Result<RideConfirmationResult, RideConfirmError> {


        val customerId = rideRequest.customerId

        val drivers = Driver.entries.filter { driver ->
            driver.driverName.equals(rideRequest.driver.name, ignoreCase = true ) }

        var driver: Driver = Driver.DRIVER_4

        drivers.forEach { selectedDriver ->
            driver = selectedDriver
        }

        val isDriverKmAcceptable = driver.minKm < rideRequest.distance

       return if (isDriverKmAcceptable) Result.Success(RideConfirmationResult(true))
        else Result.Error(RideConfirmError.NetWork.INVALID_DISTANCE)

        }
    }
