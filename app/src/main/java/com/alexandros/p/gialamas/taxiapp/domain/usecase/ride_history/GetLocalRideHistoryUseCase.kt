package com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history

import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class GetLocalRideHistoryUseCase @Inject constructor(
    private val rideRepository: RideRepository
) {

     operator fun invoke(
        customerId: String,
        driverId: Int?
    ): Flow<Result<List<RideEntity>, RideHistoryError>> {
        Timber.tag("fetchRides")
            .d("invoke called with customerId: $customerId, driverId: $driverId")
        return rideRepository.getLocalRideHistory(customerId, driverId)
    }

}