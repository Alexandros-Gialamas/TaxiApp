package com.alexandros.p.gialamas.taxiapp.data.repository

import com.alexandros.p.gialamas.taxiapp.data.mapper.toEntity
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRide
import com.alexandros.p.gialamas.taxiapp.data.source.local.database.RideDao
import com.alexandros.p.gialamas.taxiapp.data.source.remote.api.RideService
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class RideRepositoryImpl @Inject constructor(
    private val rideService: RideService,
    private val rideDao: RideDao
) : RideRepository {

    override suspend fun saveRide(ride: Ride) {
        rideDao.insertRide(ride.toEntity())
    }

    override fun getRideHistory(customerId: String, driverId: Int): Flow<List<Ride>> {
        return rideDao.getRideHistory(driverId).map { rideEntities ->
            rideEntities.map { it.toRide() }
        }
    }

    suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ) = rideService.getRideEstimate(
        customerId = customerId,
        origin = origin,
        destination = destination
    )


    suspend fun confirmRide(ride: Ride) = rideService.confirmRide(ride = ride)

    suspend fun getRideHistory(customerId: String, driverId: Int?): RideHistoryResponse {
        return rideService.getRideHistory(customerId, driverId)
    }

}