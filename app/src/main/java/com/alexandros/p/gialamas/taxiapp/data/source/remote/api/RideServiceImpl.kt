package com.alexandros.p.gialamas.taxiapp.data.source.remote.api

import android.util.Log
import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.data.util.Constants
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistoryResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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


    override suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Boolean {
        try {
            val response =
                httpClient.patch(Constants.API_CONFIRM_RIDE_ENDPOINT) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        ConfirmRideRequest(
                            customerId = confirmRideRequest.customerId,
                            origin = confirmRideRequest.origin,
                            destination = confirmRideRequest.destination,
                            distance = confirmRideRequest.distance,
                            duration = confirmRideRequest.duration,
                            driver = confirmRideRequest.driver,
                            value = confirmRideRequest.value
                        )
                    )
                }
            val responseBody = response.body<String>()
            Log.d("RideConfirmServiceImpl", "confirmRide response: $responseBody")
            return response.status.value == 200
        } catch (e: Exception) {
            Log.e("RideConfirmServiceImpl", "confirmRide: Error = ${e.message}")
            throw e
        }

    }

    override suspend fun getRideHistory(customerId: String, driverId: Int?): RideHistoryResponse {
        val url = if (driverId == null) {
            "${Constants.API_HISTORY_RIDE_ENDPOINT}${customerId}"
        } else {
            "${Constants.API_HISTORY_RIDE_ENDPOINT}${customerId}?driver_id=$driverId"
        }

        try {
            val response = httpClient.get(url) {
                Log.d(
                    "RideServiceImpl",
                    "getRideHistory: URL = $url, customerId = $customerId, driverId = $driverId"
                )
                contentType(ContentType.Application.Json)
            }
            val responseBody = response.body<String>()
            Log.d("RideServiceImpl", "getRideHistory: Raw Response = $responseBody")
            return response.body()
        } catch (e: Exception) {
            Log.e("RideServiceImpl", "getRideHistory: Error = ${e.message}")
            throw e
        }
    }

}
