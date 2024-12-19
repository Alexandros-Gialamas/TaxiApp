package com.alexandros.p.gialamas.taxiapp.data.repository

import android.util.Log
import com.alexandros.p.gialamas.taxiapp.data.mapper.toEntity
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRide
import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.source.local.database.RideDao
import com.alexandros.p.gialamas.taxiapp.data.source.remote.api.RideService
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject


class RideRepositoryImpl @Inject constructor(
    private val rideService: RideService,
    private val rideDao: RideDao
) : RideRepository {

    override suspend fun saveRide(ride: Ride) {
        Log.d("RideConfirmRepositoryImpl", "saveRide called with ride: $ride")
        rideDao.insertRide(ride.toEntity())
    }

    override fun getLocalRideHistory(customerId: String, driverId: Int?): Flow<List<Ride>> {
        val result = if (driverId == null) {
            rideDao.getAllRides(customerId = customerId)
        } else {
            rideDao.getLocalRideHistory(customerId = customerId, driverId = driverId)
        }
        Log.d("fetchRides", "getLocalRideHistory result: $result")
        return result
            .onEach { rideEntities ->
                val rides = rideEntities.map { it.toRide() }
                Log.d("fetchRides", "Local rides emitted: $rides")
            }.map { rideEntities ->
                rideEntities.map { it.toRide() }
            }
    }


    override suspend fun getRideHistory(
        customerId: String,
        driverId: Int?
    ) = rideService.getRideHistory(
        customerId = customerId,
        driverId = driverId
    )


    override suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ) = rideService.getRideEstimate(
        customerId = customerId,
        origin = origin,
        destination = destination
    )

    override suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest) =
        rideService.confirmRide(confirmRideRequest)


}