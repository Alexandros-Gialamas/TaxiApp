package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import javax.inject.Inject

class SaveRideUseCase @Inject constructor(
private val rideRepository: RideRepository
) {

    suspend operator fun invoke(ride: Ride){
        rideRepository.saveRide(ride)
    }

}