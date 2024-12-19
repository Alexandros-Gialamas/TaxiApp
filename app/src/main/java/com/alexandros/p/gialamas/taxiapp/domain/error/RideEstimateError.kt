package com.alexandros.p.gialamas.taxiapp.domain.error

sealed interface RideEstimateError: ResultError {

    enum class Network: RideEstimateError {
        NETWORK_CONNECTIVITY,
        NETWORK_ERROR
    }

    enum class Registration: RideEstimateError {
        EMPTY_CUSTOMER_FIELD,
        EMPTY_ORIGIN_FIELD,
        EMPTY_DESTINATION_FIELD
    }

    enum class Ride: RideEstimateError {
        RIDE_FAILED_TO_CONFIRM
    }

}