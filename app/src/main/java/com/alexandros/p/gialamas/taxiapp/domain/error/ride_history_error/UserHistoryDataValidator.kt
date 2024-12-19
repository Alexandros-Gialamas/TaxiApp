package com.alexandros.p.gialamas.taxiapp.domain.error.ride_history_error

import com.alexandros.p.gialamas.taxiapp.R
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.UiText

class UserHistoryDataValidator {

    fun validation(
        customerId: String,
    ): HistoryValidationResult {

        return when {
            customerId.isBlank() -> HistoryValidationResult.Error(UiText.StringResource(R.string.empty_customer_id_field))
            else -> HistoryValidationResult.Success
        }
    }
}

sealed class HistoryValidationResult {
    data object Success : HistoryValidationResult()
    data class Error(val error: UiText) : HistoryValidationResult()
}
