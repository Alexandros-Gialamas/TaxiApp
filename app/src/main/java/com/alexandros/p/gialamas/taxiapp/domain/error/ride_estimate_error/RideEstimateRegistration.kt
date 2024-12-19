package com.alexandros.p.gialamas.taxiapp.domain.error.ride_estimate_error

import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_estimate.RideEstimateState

interface RideEstimateRegistration {

     fun registerCustomer(customerId: String) : Result<String, RideEstimateError.Registration>

     fun registerOrigin(originState: RideEstimateState) : Result<RideEstimateState, RideEstimateError.Registration>

     fun registerDestination(destinationState: RideEstimateState) : Result<RideEstimateState, RideEstimateError.Registration>

}


