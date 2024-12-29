package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_estimate

import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import javax.inject.Inject

class ValidateRideEstimateResponse @Inject constructor() {

    fun validation(
        rideRequest: RideEstimateResponse,
    ): Result<Boolean, RideEstimateError> {

        if (rideRequest.distance <= 0.0) return Result.Error(RideEstimateError.Network.InvalidLocation())

         return Result.Success(true)
    }
}