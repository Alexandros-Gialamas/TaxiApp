package com.alexandros.p.gialamas.taxiapp.data.repository

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.data.source.local.database.RideDao
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.remote.api.RideService
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject


class RideRepositoryImpl @Inject constructor(
    private val rideService: RideService,
    private val rideDao: RideDao
) : RideRepository {


    override suspend fun saveRide(ride: RideEntity) {
        rideDao.insertRide(ride)
        Timber.tag("RideConfirmRepositoryImpl").d("saveRide called with ride: $ride")
    }

    override fun getLocalRideHistory(customerId: String, driverId: Int?): Flow<Result<List<RideEntity>, RideHistoryError>> = flow {
        val result = if (driverId == null) {
            rideDao.getAllRides(customerId = customerId)
        } else {
            rideDao.getLocalRideHistory(customerId = customerId, driverId = driverId)
        }


        try {
            result.collect { rides ->
                emit( Result.Success(rides))
                Timber.tag("RideConfirmRepositoryImpl").d("getLocalRideHistory result: $result")
                Timber.tag("RideConfirmRepositoryImpl").d("getLocalRideHistory rides: $rides")
            }
        } catch (e: Exception) {
            Timber.tag("RideConfirmRepositoryImpl").d("getLocalRideHistory error: $e : ${e.message}")
            emit(Result.Error(RideHistoryError.Local.FAIL_TO_FETCH_LOCAL_RIDES))
        }
//        return result
//            .onEach { rideEntities ->
//                val rides = rideEntities.map { it.toRide() }
//                Timber.tag("RideConfirmRepositoryImpl").d("Local rides emitted: $rides")
//            }.map { rideEntities ->
//                rideEntities.map { it.toRide() }
//            }
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