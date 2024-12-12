package com.alexandros.p.gialamas.taxiapp.domain.repository

import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import kotlinx.coroutines.flow.Flow

interface RideRepository {

    suspend fun saveRide(ride: Ride)

    fun getRideHistory(customerId: String, driverId: Int): Flow<List<Ride>>

}