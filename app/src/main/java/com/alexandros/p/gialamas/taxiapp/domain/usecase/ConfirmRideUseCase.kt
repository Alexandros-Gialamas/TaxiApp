package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.data.repository.RideRepositoryImpl
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import javax.inject.Inject

class ConfirmRideUseCase @Inject constructor(
    private val rideRepositoryImpl: RideRepositoryImpl
) {

    suspend operator fun invoke(ride: Ride) = rideRepositoryImpl.confirmRide(ride = ride)

}