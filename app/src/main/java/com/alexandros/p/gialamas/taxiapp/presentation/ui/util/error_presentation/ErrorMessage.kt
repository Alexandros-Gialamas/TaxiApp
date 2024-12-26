package com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation

import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError


/**
 * Converts a [RideHistoryError] into a user-friendly [UiText] for display in the UI.
 *
 * @return A [UiText] representation of the error message.
 */
fun RideHistoryError.asHistoryUiText(): UiText {
    return when (this) {
        RideHistoryError.Network.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        RideHistoryError.Network.INVALID_DATA -> UiText.StringResource(R.string.invalid_data_error)
        RideHistoryError.Network.DRIVER_NOT_FOUND -> UiText.StringResource(R.string.driver_not_found_error)
        RideHistoryError.Network.INVALID_DISTANCE -> UiText.StringResource(R.string.invalid_distance_error)
        RideHistoryError.Network.NO_RIDES_FOUND -> UiText.StringResource(R.string.no_rides_found_error)
        RideHistoryError.Network.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        RideHistoryError.UserDataValidation.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.empty_customer_id_field_error)
        RideHistoryError.Local.FAIL_TO_FETCH_LOCAL_RIDES -> UiText.StringResource(R.string.fail_to_fetch_local_rides)
    }
}

/**
 * Converts a [RideConfirmError] into a user-friendly [UiText] for display in the UI.
 *
 * @return A [UiText] representation of the error message.
 */
fun RideConfirmError.asConfirmUiText(): UiText{
    return when (this){
        RideConfirmError.NetWork.INVALID_DATA -> UiText.StringResource(R.string.invalid_data_error)
        RideConfirmError.NetWork.INVALID_DISTANCE -> UiText.StringResource(R.string.invalid_distance_error)
        RideConfirmError.NetWork.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        RideConfirmError.NetWork.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        RideConfirmError.NetWork.RIDE_NOT_CONFIRMED -> UiText.StringResource(R.string.ride_not_confirmed_error)
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
        RideEstimateError.Network.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        RideEstimateError.Network.INVALID_DATA -> UiText.StringResource(R.string.invalid_data_error)
        RideEstimateError.Network.NO_RIDES_FOUND -> UiText.StringResource(R.string.no_rides_found_error)
        RideEstimateError.Network.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        RideEstimateError.UserDataValidation.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.empty_customer_id_field_error)
        RideEstimateError.UserDataValidation.INVALID_ORIGIN -> UiText.StringResource(R.string.empty_origin_field_error)
        RideEstimateError.UserDataValidation.INVALID_DESTINATION -> UiText.StringResource(R.string.empty_destination_field_error)
        RideEstimateError.Network.INVALID_LOCATION -> UiText.StringResource(R.string.invalid_location_error)
        RideEstimateError.Network.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.invalid_customer_error)
    }
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