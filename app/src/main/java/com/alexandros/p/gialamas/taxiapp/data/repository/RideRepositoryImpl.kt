package com.alexandros.p.gialamas.taxiapp.data.repository

import com.alexandros.p.gialamas.taxiapp.data.mapper.toEntity
import com.alexandros.p.gialamas.taxiapp.data.mapper.toRide
import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.data.source.local.database.RideDao
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.remote.api.RideService
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject


class RideRepositoryImpl @Inject constructor(
    private val rideService: RideService,
    private val rideDao: RideDao
) : RideRepository {


    override suspend fun saveRide(ride: RideEntity) {
        try {
            rideDao.insertRide(ride)
            Timber.tag("RideConfirmRepositoryImpl").d("saveRide called with ride: $ride")

        } catch (e: Exception) {
            Timber.e(e, "Failed to save the ride because: ${e.message}")
            RideConfirmError.Local.FAILED_TO_SAVE_THE_RIDE
        }

    }

    override fun getLocalRideHistory(customerId: String, driverId: Int?): Flow<List<Ride>> {
        val result = if (driverId == null) {
            rideDao.getAllRides(customerId = customerId)
        } else {
            rideDao.getLocalRideHistory(customerId = customerId, driverId = driverId)
        }
        Timber.tag("RideConfirmRepositoryImpl").d("getLocalRideHistory result: $result")
        return result
            .onEach { rideEntities ->
                val rides = rideEntities.map { it.toRide() }
                Timber.tag("RideConfirmRepositoryImpl").d("Local rides emitted: $rides")
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