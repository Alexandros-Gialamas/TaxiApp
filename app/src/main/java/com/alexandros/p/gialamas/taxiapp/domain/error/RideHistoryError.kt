package com.alexandros.p.gialamas.taxiapp.domain.error

sealed interface RideHistoryError: ResultError {


    enum class UserDataValidation: RideHistoryError {
        INVALID_CUSTOMER_ID
    }

    enum class Network: RideHistoryError {
        NETWORK_ERROR,
        INVALID_DATA,
        DRIVER_NOT_FOUND,
        INVALID_DISTANCE,
        NO_RIDES_FOUND,
        UNKNOWN_ERROR
    }

    enum class Local: RideHistoryError {
        LOCAL_ERROR
    }


}