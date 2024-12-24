package com.alexandros.p.gialamas.taxiapp.domain.error

import android.util.Log
import com.alexandros.p.gialamas.taxiapp.domain.model.RideOption
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.static_options.Driver
import javax.inject.Inject

class RideConfirmDriverDistanceValidator @Inject constructor() {

    fun validation(
        rideOption: RideOption,
        distance: Double,
    ): Result<Unit, RideConfirmError.NetWork> {

        val drivers = Driver.entries.filter { driver ->
            driver.driverName.equals(rideOption.name, ignoreCase = true ) }


        drivers.forEach { selectedDriver ->
            Log.d("Driver","Selected Driver : ${selectedDriver.driverName}")
            if (selectedDriver.minKm > distance) return Result.Error(RideConfirmError.NetWork.INVALID_DISTANCE)

        }



        return Result.Success(Unit)
    }


}