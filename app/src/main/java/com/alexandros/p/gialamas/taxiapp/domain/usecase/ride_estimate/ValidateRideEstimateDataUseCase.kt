package com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_estimate

import com.alexandros.p.gialamas.taxiapp.domain.error.EmptyResult
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import javax.inject.Inject

class ValidateRideEstimateDataUseCase @Inject constructor() {

    fun validation(
        customerId: String,
        origin: String,
        destination: String
    ): EmptyResult<RideEstimateError.UserDataValidation> {

        when {
            customerId.isBlank() -> return Result.Error(RideEstimateError.UserDataValidation.INVALID_CUSTOMER_ID)
            origin.isBlank() -> return Result.Error(RideEstimateError.UserDataValidation.INVALID_ORIGIN)
            destination.isBlank() -> return Result.Error(RideEstimateError.UserDataValidation.INVALID_DESTINATION)
        }
        return Result.Success(Unit)
    }

}


