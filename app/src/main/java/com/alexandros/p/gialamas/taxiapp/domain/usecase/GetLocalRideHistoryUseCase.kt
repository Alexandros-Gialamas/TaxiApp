package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalRideHistoryUseCase @Inject constructor(
    private val rideRepositoryImpl: RideRepositoryImpl
) {

    operator fun invoke(customerId: String, driverId: Int? = null): Flow<List<Ride>> {
        return rideRepositoryImpl.getLocalRideHistory(customerId, driverId)
    }

}