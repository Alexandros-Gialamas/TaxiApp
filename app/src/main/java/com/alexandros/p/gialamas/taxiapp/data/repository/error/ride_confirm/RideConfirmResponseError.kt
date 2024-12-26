package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_confirm

import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class RideConfirmResponseError @Inject constructor() {

    fun mapError(response: HttpResponse) : RideConfirmError {
        return when (response.status.value) {

            400 -> {
                RideConfirmError.NetWork.INVALID_DATA
            }

            else -> {
                RideConfirmError.NetWork.UNKNOWN_ERROR
            }
        }
    }

}