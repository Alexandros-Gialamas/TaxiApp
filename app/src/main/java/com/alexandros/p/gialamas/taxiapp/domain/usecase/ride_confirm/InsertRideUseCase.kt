package com.alexandros.p.gialamas.taxiapp.domain.usecase.ride_confirm

import com.alexandros.p.gialamas.taxiapp.data.model.RideEntity
import com.alexandros.p.gialamas.taxiapp.domain.error.EmptyResult
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.repository.RideRepository
import timber.log.Timber
import javax.inject.Inject

class InsertRideUseCase @Inject constructor(
private val rideRepository: RideRepository
) {

    suspend operator fun invoke(ride: RideEntity) : EmptyResult<RideConfirmError> {
        return try {
            rideRepository.insertRide(ride)
            Timber.tag("SaveRideUseCase").d("Ride to save: $ride")
            Result.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Failed saving the ride due to : ${e.message}")
            Result.Error(RideConfirmError.Local.FAILED_TO_SAVE_THE_RIDE)
        }

    }

}