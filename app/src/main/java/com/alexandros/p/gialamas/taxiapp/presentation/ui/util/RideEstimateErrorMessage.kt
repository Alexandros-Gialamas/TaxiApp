package com.alexandros.p.gialamas.taxiapp.presentation.ui.util

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError

fun RideEstimateError.asUiText(): UiText {
    return when (this) {
        RideEstimateError.Network.NETWORK_ERROR -> UiText.DynamicString(
            "Request failed. Please try again!"
        )
        else -> UiText.DynamicString("An unexpected error occur. Please try again!")
    }

}

fun Result.Error<*, RideEstimateError>.asErrorUiText(): UiText {
    return error.asUiText()
}