package com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history

import com.alexandros.p.gialamas.taxiapp.domain.error.EmptyResult
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import javax.inject.Inject

class ValidateRideHistoryDataUseCase @Inject constructor() {

    fun validation(
        customerId: String,
    ): EmptyResult<RideHistoryError.UserDataValidation> {

        if (customerId.isBlank()) return Result.Error(RideHistoryError.UserDataValidation.INVALID_CUSTOMER_ID)

        return Result.Success(Unit)

    }
}


