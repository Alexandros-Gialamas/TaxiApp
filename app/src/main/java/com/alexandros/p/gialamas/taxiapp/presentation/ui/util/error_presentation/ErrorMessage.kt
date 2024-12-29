package com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation

import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.data.model.ErrorResponseResult
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError


/**
 * Converts a [RideHistoryError] into a user-friendly [UiText] for display in the UI.
 *
 * @return A [UiText] representation of the error message.
 */
fun RideHistoryError.asHistoryUiText(): UiText {
    return when (this) {

        RideHistoryError.UserDataValidation.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.empty_customer_id_field_error)

        RideHistoryError.Local.FAIL_TO_FETCH_LOCAL_RIDES -> UiText.StringResource(R.string.fail_to_fetch_local_rides)

        is RideHistoryError.Network.DriverNotFound -> {
            if (this.error != null) {
                UiText.DynamicString(formatNetworkApiMessage(this.error))
            } else {
                UiText.StringResource(R.string.driver_not_found_error)
            }
        }
        is RideHistoryError.Network.InvalidData -> {
            if (this.error != null) {
                UiText.DynamicString(formatNetworkApiMessage(this.error))
            } else {
                UiText.StringResource(R.string.invalid_data_error)
            }
        }
        is RideHistoryError.Network.InvalidDistance -> {
            if (this.error != null) {
                UiText.DynamicString(formatNetworkApiMessage(this.error) )
            } else {
                UiText.StringResource(R.string.invalid_distance_error)
            }
        }
        is RideHistoryError.Network.NetworkError -> UiText.StringResource(R.string.network_error)

        is RideHistoryError.Network.NoRidesFound -> {
            if (this.error != null) {
                UiText.DynamicString(formatNetworkApiMessage(this.error) )
            } else {
                UiText.StringResource(R.string.no_rides_found_error)
            }
        }

        is RideHistoryError.Network.UnknownError -> UiText.StringResource(R.string.unknown_error)

    }
}

/**
 * Converts a [RideConfirmError] into a user-friendly [UiText] for display in the UI.
 *
 * @return A [UiText] representation of the error message.
 */
fun RideConfirmError.asConfirmUiText(): UiText{
    return when (this){
        is RideConfirmError.NetWork.InvalidData -> {
            if (this.error != null) {
                UiText.DynamicString(formatNetworkApiMessage(this.error))
            } else {
                UiText.StringResource(R.string.invalid_data_error)
            }
        }
        is RideConfirmError.NetWork.InvalidDistance -> UiText.StringResource(R.string.invalid_distance_error)
        is RideConfirmError.NetWork.NetworkError -> UiText.StringResource(R.string.network_error)
        is RideConfirmError.NetWork.UnknownError -> UiText.StringResource(R.string.unknown_error)
        is RideConfirmError.NetWork.RideNotConfirmed -> UiText.StringResource(R.string.ride_not_confirmed_error)
        RideConfirmError.Local.FAILED_TO_SAVE_THE_RIDE -> UiText.StringResource(R.string.save_ride_failed_error)
        RideConfirmError.Local.INVALID_STATE_DATA -> UiText.StringResource(R.string.invalid_state_data_error)
    }
}

/**
 * Converts a [RideEstimateError] into a user-friendly [UiText] for display in the UI.
 *
 * @return A [UiText] representation of the error message.
 */
fun RideEstimateError.asEstimateUiText(): UiText {
    return when (this) {
        is RideEstimateError.Network.NetworkError -> UiText.StringResource(R.string.network_error)
        is RideEstimateError.Network.InvalidData -> {
            if (this.error != null) {
                UiText.DynamicString(formatNetworkApiMessage(this.error))
            } else {
                UiText.StringResource(R.string.invalid_data_error)
            }
        }
        is RideEstimateError.Network.NoRidesFound -> UiText.StringResource(R.string.no_rides_found_error)
        is RideEstimateError.Network.UnknownError -> UiText.StringResource(R.string.unknown_error)
        RideEstimateError.UserDataValidation.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.empty_customer_id_field_error)
        RideEstimateError.UserDataValidation.INVALID_ORIGIN -> UiText.StringResource(R.string.empty_origin_field_error)
        RideEstimateError.UserDataValidation.INVALID_DESTINATION -> UiText.StringResource(R.string.empty_destination_field_error)
        is RideEstimateError.Network.InvalidLocation -> UiText.StringResource(R.string.invalid_location_error)
        is RideEstimateError.Network.InvalidCustomerId -> UiText.StringResource(R.string.invalid_customer_error)
    }
}

private fun formatNetworkApiMessage(error: ErrorResponseResult) : String {
    return StringBuilder().apply {
        appendLine(error.errorCode)
        appendLine()
        appendLine("  ${error.errorDescription}")
    }.toString()
}


/**
 * A convenience function to directly get a [UiText] representation of a [RideHistoryError] from a [Result.Error].
 *
 * @return A [UiText] representation of the error message.
 */
fun Result.Error<RideHistoryError>.asHistoryUiText(): UiText {
    return error.asHistoryUiText()
}


/**
 * A convenience function to directly get a [UiText] representation of a [RideConfirmError] from a [Result.Error].
 *
 * @return A [UiText] representation of the error message.
 */
fun Result.Error<RideConfirmError>.asConfirmUiText(): UiText {
    return error.asConfirmUiText()
}


/**
 * A convenience function to directly get a [UiText] representation of a [RideEstimateError] from a [Result.Error].
 *
 * @return A [UiText] representation of the error message.
 */
fun Result.Error<RideEstimateError>.asEstimateUiText(): UiText {
    return error.asEstimateUiText()
}