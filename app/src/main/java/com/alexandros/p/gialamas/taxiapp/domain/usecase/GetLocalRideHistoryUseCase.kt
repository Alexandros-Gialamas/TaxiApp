package com.alexandros.p.gialamas.taxiapp.domain.usecase

import android.util.Log
import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalRideHistoryUseCase @Inject constructor(
    private val rideRepositoryImpl: RideRepositoryImpl
) {

    operator fun invoke(customerId: String, driverId: Int?): Flow<List<Ride>> {
        Log.d("fetchRides", "invoke called with customerId: $customerId, driverId: $driverId")
        return rideRepositoryImpl.getLocalRideHistory(customerId, driverId)
    }

}