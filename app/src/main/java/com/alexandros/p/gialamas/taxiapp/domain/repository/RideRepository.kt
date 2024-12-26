package com.alexandros.p.gialamas.taxiapp.domain.repository

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideConfirmationResult
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.data.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import kotlinx.coroutines.flow.Flow

interface RideRepository {



    suspend fun saveRide(ride: RideEntity)

   suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ): Result<RideEstimateResponse, RideEstimateError>


    suspend fun getRideHistory(
        customerId: String,
        driverId: Int? = null
    ): Result<RideHistoryResponse, RideHistoryError>

     fun getLocalRideHistory(
        customerId: String,
        driverId: Int?
    ): Flow<Result<List<RideEntity>, RideHistoryError>>

    suspend fun confirmRide(
        confirmRideRequest: ConfirmRideRequest
    ) : Result<RideConfirmationResult, RideConfirmError>

}