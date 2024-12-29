package com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_confirm

import com.alexandros.p.gialamas.taxiapp.data.repository.error.utils.ParseErrorResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import io.ktor.client.statement.HttpResponse
import javax.inject.Inject

class RideConfirmResponseError @Inject constructor(
    private val parseErrorResponse: ParseErrorResponse
) {

    suspend fun mapError(response: HttpResponse) : RideConfirmError {
        return when (response.status.value) {

            400 -> {
                val errorResponse = parseErrorResponse.parse(response)
                RideConfirmError.NetWork.InvalidData(errorResponse)
            }

            else -> {
                val errorResponse = parseErrorResponse.parse(response)
                RideConfirmError.NetWork.UnknownError(errorResponse)
            }
        }
    }

}