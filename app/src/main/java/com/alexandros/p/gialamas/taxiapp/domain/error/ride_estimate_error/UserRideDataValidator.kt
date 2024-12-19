package com.alexandros.p.gialamas.taxiapp.domain.error.ride_estimate_error

import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText

class UserRideDataValidator {

    fun validation(
        customerId: String,
        origin: String,
        destination: String
    ): RideValidationResult {

        return when {
            customerId.isBlank() -> RideValidationResult.Error(UiText.StringResource(R.string.empty_customer_id_field))
            origin.isBlank() -> RideValidationResult.Error(UiText.StringResource(R.string.empty_origin_field))
            destination.isBlank() -> RideValidationResult.Error(UiText.StringResource(R.string.empty_destination_field))
            else -> RideValidationResult.Success
        }
    }
}

sealed class RideValidationResult {
    data object Success : RideValidationResult()
    data class Error(val error: UiText) : RideValidationResult()
}
