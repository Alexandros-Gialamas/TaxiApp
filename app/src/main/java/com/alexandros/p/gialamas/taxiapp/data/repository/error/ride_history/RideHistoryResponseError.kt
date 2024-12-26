package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_history

import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class RideHistoryResponseError @Inject constructor(){

    fun mapError(response: HttpResponse) : RideHistoryError {
        return when (response.status.value) {
            400 -> {
                RideHistoryError.Network.DRIVER_NOT_FOUND
            }
            404 -> {
                RideHistoryError.Network.INVALID_DATA
            }
            406 -> {
                RideHistoryError.Network.INVALID_DISTANCE
            }
            else -> {
                RideHistoryError.Network.UNKNOWN_ERROR
            }
        }
    }
}