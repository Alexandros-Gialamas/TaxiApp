package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_estimate

import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class RideEstimateResponseError @Inject constructor() {

    fun mapError(response: HttpResponse) : RideEstimateError {
        return when (response.status.value) {
            400 -> {
                RideEstimateError.Network.INVALID_DATA
            }
            else -> {
                RideEstimateError.Network.UNKNOWN_ERROR
            }
        }
    }

}