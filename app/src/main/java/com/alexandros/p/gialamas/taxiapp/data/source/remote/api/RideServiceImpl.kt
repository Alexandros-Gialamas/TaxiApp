package com.alexandros.p.gialamas.taxiapp.data.source.remote.api

import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.util.Constants
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import javax.inject.Inject

class RideServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : RideService {

    override suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ): RideEstimateResponse {

        val response = httpClient.post(Constants.API_RIDE_ESTIMATE_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(
                RideEstimateRequest(
                    customerId = customerId,
                    origin = origin,
                    destination = destination
                )
            )
        }
        return try {
            response.body()
        } catch (e: Exception) {
            throw e
        }
    }


    override suspend fun confirmRide(ride: Ride): Boolean {
        val response =
            httpClient.patch(Constants.API_CONFIRM_RIDE_ENDPOINT) {
                contentType(ContentType.Application.Json)
                setBody(ride)
            }
        return response.status.value == 200
    }

    override suspend fun getRideHistory(customerId: String, driverId: Int?): RideHistoryResponse {
        return httpClient.get(Constants.API_HISTORY_RIDE_ENDPOINT + customerId) {
            driverId?.let {
                parameter("driver_id", it)
            }
        }.body()
    }
}
