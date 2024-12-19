package com.alexandros.p.gialamas.taxiapp.data.source.remote.api

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistoryResponse
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

interface RideService {

    suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ): RideEstimateResponse

    suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Boolean

    suspend fun getRideHistory(
        customerId: String,
        driverId: Int? = null
    ): RideHistoryResponse

}