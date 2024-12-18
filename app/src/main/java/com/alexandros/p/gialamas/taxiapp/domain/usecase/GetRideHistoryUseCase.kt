package com.alexandros.p.gialamas.taxiapp.domain.usecase


import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistoryResponse
import javax.inject.Inject

class GetRideHistoryUseCase @Inject constructor(
    private val rideRepositoryImpl: RideRepositoryImpl
) {

     suspend operator fun invoke(customerId: String, driverId: Int? = null): RideHistoryResponse {
        return rideRepositoryImpl.getRideHistory(customerId, driverId)
    }

}