package com.alexandros.p.gialamas.taxiapp.domain.error

sealed interface RideEstimateError: ResultError {

    enum class Network: RideEstimateError {
        NETWORK_ERROR
    }

    enum class Local: RideEstimateError {
        LOCAL_ERROR
    }

    enum class Ride: RideEstimateError {
        RIDE_FAILED_TO_CONFIRM
    }

}