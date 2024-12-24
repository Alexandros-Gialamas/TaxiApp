package com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation

import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError

fun RideHistoryError.asHistoryUiText(): UiText {
    return when (this) {
        RideHistoryError.Network.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        RideHistoryError.Network.INVALID_DATA -> UiText.StringResource(R.string.invalid_data_error)
        RideHistoryError.Network.DRIVER_NOT_FOUND -> UiText.StringResource(R.string.driver_not_found_error)
        RideHistoryError.Network.INVALID_DISTANCE -> UiText.StringResource(R.string.invalid_distance_error)
        RideHistoryError.Network.NO_RIDES_FOUND -> UiText.StringResource(R.string.no_rides_found_error)
        RideHistoryError.Network.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        RideHistoryError.UserDataValidation.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.empty_customer_id_field)
        RideHistoryError.Local.LOCAL_ERROR -> UiText.StringResource(R.string.local_error)
    }
}

fun RideConfirmError.asConfirmUiText(): UiText{
    return when (this){
        RideConfirmError.NetWork.INVALID_DISTANCE -> UiText.StringResource(R.string.invalid_distance_error)
        RideConfirmError.NetWork.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        RideConfirmError.NetWork.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        RideConfirmError.NetWork.RIDE_NOT_CONFIRMED -> UiText.StringResource(R.string.ride_not_confirmed)
        RideConfirmError.Local.FAILED_TO_SAVE_THE_RIDE -> UiText.StringResource(R.string.save_ride_failed)

    }
}

fun RideEstimateError.asEstimateUiText(): UiText {
    return when (this) {
        RideEstimateError.Network.NETWORK_ERROR -> UiText.StringResource(R.string.network_error)
        RideEstimateError.Network.INVALID_DATA -> UiText.StringResource(R.string.invalid_data_error)
        RideEstimateError.Network.DRIVER_NOT_FOUND -> UiText.StringResource(R.string.driver_not_found_error)
        RideEstimateError.Network.INVALID_DISTANCE -> UiText.StringResource(R.string.invalid_distance_error)
        RideEstimateError.Network.NO_RIDES_FOUND -> UiText.StringResource(R.string.no_rides_found_error)
        RideEstimateError.Network.UNKNOWN_ERROR -> UiText.StringResource(R.string.unknown_error)
        RideEstimateError.UserDataValidation.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.empty_customer_id_field)
        RideEstimateError.Ride.RIDE_FAILED_TO_CONFIRM -> UiText.StringResource(R.string.empty_customer_id_field)
        RideEstimateError.UserDataValidation.INVALID_ORIGIN -> UiText.StringResource(R.string.empty_origin_field)
        RideEstimateError.UserDataValidation.INVALID_DESTINATION -> UiText.StringResource(R.string.empty_destination_field)
        RideEstimateError.Local.LOCAL_ERROR -> UiText.StringResource(R.string.local_error)
        RideEstimateError.Network.INVALID_LOCATION -> UiText.StringResource(R.string.invalid_location_error)
        RideEstimateError.Network.INVALID_CUSTOMER_ID -> UiText.StringResource(R.string.invalid_customer_error)
    }
}

fun Result.Error<*, RideHistoryError>.asHistoryUiText(): UiText {
    return error.asHistoryUiText()
}

fun Result.Error<*, RideConfirmError>.asConfirmUiText(): UiText {
    return error.asConfirmUiText()
}

fun Result.Error<*, RideEstimateError>.asEstimateUiText(): UiText {
    return error.asEstimateUiText()
}