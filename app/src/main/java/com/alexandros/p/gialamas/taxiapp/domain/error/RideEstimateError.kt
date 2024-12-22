package com.alexandros.p.gialamas.taxiapp.domain.error

sealed interface RideEstimateError: ResultError {

    enum class UserDataValidation: RideEstimateError {
        INVALID_CUSTOMER_ID,
        INVALID_ORIGIN,
        INVALID_DESTINATION,
    }

    enum class Network: RideEstimateError {
        NETWORK_ERROR,
        INVALID_DATA,
        INVALID_LOCATION,
        INVALID_CUSTOMER_ID,
        DRIVER_NOT_FOUND,
        INVALID_DISTANCE,
        NO_RIDES_FOUND,
        UNKNOWN_ERROR
    }


    enum class Ride: RideEstimateError {
        RIDE_FAILED_TO_CONFIRM
    }

    enum class Local: RideEstimateError {
        LOCAL_ERROR
    }

}