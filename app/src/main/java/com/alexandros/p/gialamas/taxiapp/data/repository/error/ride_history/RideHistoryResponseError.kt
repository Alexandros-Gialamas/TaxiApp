package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_history

import com.alexandros.p.gialamas.taxiapp.data.repository.error.utils.ParseErrorResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class RideHistoryResponseError @Inject constructor(
    private val parseErrorResponse: ParseErrorResponse
) {

     suspend fun mapError(response: HttpResponse): RideHistoryError {
        return when (response.status.value) {
            400 -> {
                val errorResponse = parseErrorResponse.parse(response)
                RideHistoryError.Network.DriverNotFound(errorResponse)
            }

            404 -> {
                val errorResponse = parseErrorResponse.parse(response)
                RideHistoryError.Network.NoRidesFound(errorResponse)
            }

            406 -> {
                val errorResponse = parseErrorResponse.parse(response)
                RideHistoryError.Network.InvalidDistance(errorResponse)
            }

            else -> {
                RideHistoryError.Network.UnknownError()
            }
        }
    }
}

