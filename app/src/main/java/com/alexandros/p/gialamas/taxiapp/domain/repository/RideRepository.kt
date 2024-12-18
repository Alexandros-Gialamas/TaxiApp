package com.alexandros.p.gialamas.taxiapp.domain.repository

import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistoryResponse
import kotlinx.coroutines.flow.Flow

interface RideRepository {

    suspend fun saveRide(ride: Ride)

   suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ): RideEstimateResponse

    suspend fun getRideHistory(customerId: String, driverId: Int? = null): RideHistoryResponse

    fun getLocalRideHistory(customerId: String, driverId: Int? = null): Flow<List<Ride>>

}