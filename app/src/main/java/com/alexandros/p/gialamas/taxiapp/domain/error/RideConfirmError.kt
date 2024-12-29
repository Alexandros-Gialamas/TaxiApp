package com.alexandros.p.gialamas.taxiapp.domain.error

import com.alexandros.p.gialamas.taxiapp.data.model.ErrorResponseResult


/**
 * Represents errors that can occur during the ride confirmation process.
 *
 * These errors are categorized as either [NetWork] errors, indicating issues with network communication or
 * server-side data, or [Local] errors, representing problems with local data storage or validation.
 */
sealed interface RideConfirmError : ResultError {

    /**
     * Network-related errors that can occur during ride confirmation.
     */
    sealed class NetWork : RideConfirmError {
        /**
         * The server returned data in an unexpected or invalid format.
         */
        data class InvalidData(val error: ErrorResponseResult? = null) : NetWork()

        /**
         * The calculated distance for the ride is invalid or unreasonable.
         */
        data class InvalidDistance(val error: ErrorResponseResult? = null) : NetWork()

        /**
         * The ride was not confirmed by the server.
         */
        data class RideNotConfirmed(val error: ErrorResponseResult? = null) : NetWork()

        /**
         * A general network error occurred (e.g., no internet connection, timeout).
         */
        data class NetworkError(val error: ErrorResponseResult? = null) : NetWork()

        /**
         * An unknown error occurred on the server side.
         */
        data class UnknownError(val error: ErrorResponseResult? = null) : NetWork()
    }

    /**
     * Errors related to local data handling during ride confirmation.
     */
    enum class Local : RideConfirmError {
        /**
         * The app failed to save the ride details locally.
         */
        FAILED_TO_SAVE_THE_RIDE,

        /**
         * The local data representing the ride's state is invalid or inconsistent.
         */
        INVALID_STATE_DATA
    }

}