package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import javax.inject.Inject

class GetRideEstimateUseCase @Inject constructor(
    private val rideRepository: RideRepository
) {

    suspend operator fun invoke(
        customerId: String,
        origin: String,
        destination: String
    ): Result<RideEstimateResponse, RideEstimateError.Network> = rideRepository.getRideEstimate(
            customerId = customerId,
            origin = origin,
            destination = destination
        )

}