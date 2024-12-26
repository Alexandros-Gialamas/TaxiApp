package com.alexandros.p.gialamas.taxiapp.domain.remote.api

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideConfirmationResult
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.data.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError

interface RideService {

    suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ): Result<RideEstimateResponse, RideEstimateError>

    suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Result<RideConfirmationResult, RideConfirmError>

    suspend fun getRideHistory(
        customerId: String,
        driverId: Int? = null
    ): Result<RideHistoryResponse, RideHistoryError>

}