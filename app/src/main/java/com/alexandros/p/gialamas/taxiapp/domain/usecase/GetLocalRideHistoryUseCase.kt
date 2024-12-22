package com.alexandros.p.gialamas.taxiapp.domain.usecase

import android.util.Log
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalRideHistoryUseCase @Inject constructor(
    private val rideRepository: RideRepository
) {

    operator fun invoke(customerId: String, driverId: Int?): Flow<List<Ride>> {
        Log.d("fetchRides", "invoke called with customerId: $customerId, driverId: $driverId")
        return rideRepository.getLocalRideHistory(customerId, driverId)
    }

}