package com.alexandros.p.gialamas.taxiapp.domain.error

sealed interface RideHistoryError: ResultError {

    enum class Network: RideHistoryError {
        NETWORK_ERROR
    }

    enum class Local: RideHistoryError {
        LOCAL_ERROR
    }


}