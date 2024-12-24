package com.alexandros.p.gialamas.taxiapp.domain.usecase

import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import javax.inject.Inject

class SaveRideUseCase @Inject constructor(
private val rideRepository: RideRepository
) {

    suspend operator fun invoke(ride: RideEntity){
        rideRepository.saveRide(ride)
    }

}