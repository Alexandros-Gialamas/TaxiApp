package com.alexandros.p.gialamas.taxiapp.data.source.remote.api

import android.util.Log
import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.data.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.data.util.Constants
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.remote.api.RideService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import timber.log.Timber
import javax.inject.Inject

class RideServiceImpl @Inject constructor(
    private val httpClient: HttpClient
) : RideService {

    override suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ): Result<RideEstimateResponse, RideEstimateError.Network> {

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

            if (response.status.isSuccess()) {

                val estimateResponse = response.body<RideEstimateResponse.EstimateResponse>()
                Result.Success(
                    data = RideEstimateResponse.EstimateResponse(
                        origin = estimateResponse.origin,
                        destination = estimateResponse.destination,
                        distance = estimateResponse.distance,
                        duration = estimateResponse.duration,
                        options = estimateResponse.options,
                        routeResponse = estimateResponse.routeResponse
                    )
                )
            } else {
                Result.Success(RideEstimateResponse.Error(response))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching ride estimate: ${e.message}")
            Result.Error(RideEstimateError.Network.UNKNOWN_ERROR)
        }
    }


    override suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Result<Boolean, RideConfirmError.NetWork> {
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

            return if (response.status.isSuccess()) {
                Result.Success(true)
            } else {
                Result.Error(RideConfirmError.NetWork.RIDE_NOT_CONFIRMED)
            }

        } catch (e: Exception) {
            Timber.e(e, "Error confirming the ride : ${e.message}")
          return  Result.Error(RideConfirmError.NetWork.UNKNOWN_ERROR)
        }

    }

    override suspend fun getRideHistory(
        customerId: String,
        driverId: Int?
    ): Result<RideHistoryResponse, RideHistoryError.Network> {
        val url = if (driverId == null) {
            "${Constants.API_HISTORY_RIDE_ENDPOINT}${customerId}"
        } else {
            "${Constants.API_HISTORY_RIDE_ENDPOINT}${customerId}?driver_id=$driverId"
        }

        return try {
            val response = httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }

            if (response.status.isSuccess()) {
                val historyResponse = response.body<RideHistoryResponse.HistoryResponse>()
                Result.Success(
                    RideHistoryResponse.HistoryResponse(
                        customerId = historyResponse.customerId,
                        rides = historyResponse.rides
                    )
                )
            } else {
                Result.Success(RideHistoryResponse.Error(response))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching ride history: ${e.message}")
            Result.Error(RideHistoryError.Network.UNKNOWN_ERROR)
        }
    }
}


