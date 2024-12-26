package com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_confirm

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import javax.inject.Inject

class ConfirmRideUseCase @Inject constructor(
    private val rideRepository: RideRepository
) {

    suspend operator fun invoke(confirmRideRequest: ConfirmRideRequest) = rideRepository.confirmRide(confirmRideRequest)

}