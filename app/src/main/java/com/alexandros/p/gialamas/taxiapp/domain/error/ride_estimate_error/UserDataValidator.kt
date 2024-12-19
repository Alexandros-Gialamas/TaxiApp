package com.alexandros.p.gialamas.taxiapp.domain.error.ride_estimate_error

import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText

class UserDataValidator {

    fun validation(
        customerId: String,
        origin: String,
        destination: String
    ): ValidationResult {

        return when {
            customerId.isBlank() -> ValidationResult.Error(UiText.StringResource(R.string.empty_customer_id_field))
            origin.isBlank() -> ValidationResult.Error(UiText.StringResource(R.string.empty_origin_field))
            destination.isBlank() -> ValidationResult.Error(UiText.StringResource(R.string.empty_destination_field))
            else -> ValidationResult.Success
        }

//        when {
//            customerId.isBlank() -> {
//                eventChannel.trySend(UserEvent.Error())
//                return false
//            }
//
//            origin.isBlank() -> {
//                eventChannel.trySend(UserEvent.Error(UiText.StringResource(R.string.empty_origin_field)))
//                return false
//            }
//
//            destination.isBlank() -> {
//                eventChannel.trySend(UserEvent.Error(UiText.StringResource(R.string.empty_destination_field)))
//                return false
//            }
//
//            else -> return true
//        }
    }
}

sealed class ValidationResult {
    data object Success : ValidationResult()
    data class Error(val error: UiText) : ValidationResult()
}



//class UserDataValidator: RideEstimateRegistration {
//
//    override fun registerCustomer(customerId: String): Result<String, RideEstimateError.Registration> {
//        return if (customerId.isBlank()) Result.Error(RideEstimateError.Registration.EMPTY_CUSTOMER_FIELD)
//        else Result.Success(customerId)
//    }
//
//    override fun registerOrigin(originState: RideEstimateState): Result<RideEstimateState, RideEstimateError.Registration> {
//        return if (originState.origin.isBlank()) Result.Error(RideEstimateError.Registration.EMPTY_ORIGIN_FIELD)
//        else Result.Success(originState)
//    }
//
//    override fun registerDestination(destinationState: RideEstimateState): Result<RideEstimateState, RideEstimateError.Registration> {
//        return if (destinationState.destination.isBlank()) Result.Error(RideEstimateError.Registration.EMPTY_DESTINATION_FIELD)
//        else Result.Success(destinationState)
//    }
//
//}