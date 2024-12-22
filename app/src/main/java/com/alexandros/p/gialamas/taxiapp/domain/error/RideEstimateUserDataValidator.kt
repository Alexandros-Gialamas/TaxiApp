package com.alexandros.p.gialamas.taxiapp.domain.error

import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.UiText
import javax.inject.Inject

class RideEstimateUserDataValidator @Inject constructor() {

    fun validation(
        customerId: String,
        origin: String,
        destination: String
    ): Result<Unit, RideEstimateError.UserDataValidation> {

        when {
            customerId.isBlank() -> return Result.Error(RideEstimateError.UserDataValidation.INVALID_CUSTOMER_ID)
            origin.isBlank() -> return Result.Error(RideEstimateError.UserDataValidation.INVALID_ORIGIN)
            destination.isBlank() -> return Result.Error(RideEstimateError.UserDataValidation.INVALID_DESTINATION)
        }
        return Result.Success(Unit)
    }

}


