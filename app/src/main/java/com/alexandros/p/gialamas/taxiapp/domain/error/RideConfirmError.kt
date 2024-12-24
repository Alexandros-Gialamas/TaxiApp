package com.alexandros.p.gialamas.taxiapp.domain.error

sealed interface RideConfirmError : ResultError {

    enum class NetWork: RideConfirmError {
        INVALID_DISTANCE,
        RIDE_NOT_CONFIRMED,
        NETWORK_ERROR,
        UNKNOWN_ERROR
    }

    enum class Local: RideConfirmError {
        FAILED_TO_SAVE_THE_RIDE
    }

}