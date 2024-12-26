package com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_history


import com.alexandros.p.gialamas.taxiapp.data.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import javax.inject.Inject

class GetRideHistoryUseCase @Inject constructor(
    private val rideRepository: RideRepository
) {

     suspend operator fun invoke(customerId: String, driverId: Int? = null): Result<RideHistoryResponse, RideHistoryError> {
        return rideRepository.getRideHistory(customerId, driverId)
    }

}