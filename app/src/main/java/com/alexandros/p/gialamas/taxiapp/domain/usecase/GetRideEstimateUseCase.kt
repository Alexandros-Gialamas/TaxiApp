package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.data.mapper.toRideEstimate
import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import com.alexandros.p.gialamas.taxiapp.domain.model.RideEstimate
import javax.inject.Inject

class GetRideEstimateUseCase @Inject constructor(
    private val rideRepositoryImpl: RideRepositoryImpl
) {

    suspend operator fun invoke(
        customerId: String,
        origin: String,
        destination: String
    ) : RideEstimate =
        rideRepositoryImpl.getRideEstimate(
            customerId = customerId,
            origin = origin,
            destination = destination
        ).toRideEstimate()

}