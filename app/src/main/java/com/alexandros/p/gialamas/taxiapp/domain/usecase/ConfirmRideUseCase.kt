package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import javax.inject.Inject

class ConfirmRideUseCase @Inject constructor(
    private val rideRepositoryImpl: RideRepositoryImpl
) {

    suspend operator fun invoke(confirmRideRequest: ConfirmRideRequest) = rideRepositoryImpl.confirmRide(confirmRideRequest)

}