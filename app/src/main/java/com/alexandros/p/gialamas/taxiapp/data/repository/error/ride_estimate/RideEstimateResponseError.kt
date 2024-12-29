package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_estimate

import com.alexandros.p.gialamas.taxiapp.data.repository.error.utils.ParseErrorResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class RideEstimateResponseError @Inject constructor(
    private val parseErrorResponse: ParseErrorResponse
) {

    suspend fun mapError(response: HttpResponse) : RideEstimateError {
        return when (response.status.value) {
            400 -> {
                val errorResponse = parseErrorResponse.parse(response)
                RideEstimateError.Network.InvalidData(errorResponse)
            }
            else -> {
                val errorResponse = parseErrorResponse.parse(response)
                RideEstimateError.Network.UnknownError(errorResponse)
            }
        }
    }

}