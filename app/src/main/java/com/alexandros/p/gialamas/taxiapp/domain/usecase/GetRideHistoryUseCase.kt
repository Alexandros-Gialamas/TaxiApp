package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRideHistoryUseCase @Inject constructor(
    private val rideRepositoryImpl: RideRepositoryImpl
) {

     operator fun invoke(customerId: String, driverId: Int): Flow<List<Ride>> {
        return rideRepositoryImpl.getRideHistory(customerId, driverId)
    }

}