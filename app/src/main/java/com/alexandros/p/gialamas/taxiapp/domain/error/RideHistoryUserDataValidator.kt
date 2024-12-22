package com.alexandros.p.gialamas.taxiapp.domain.error

import javax.inject.Inject

class RideHistoryUserDataValidator @Inject constructor() {

    fun validation(
        customerId: String,
    ): Result<Unit, RideHistoryError.UserDataValidation> {

        if (customerId.isBlank()) return Result.Error(RideHistoryError.UserDataValidation.INVALID_CUSTOMER_ID)

        return Result.Success(Unit)

    }
}


