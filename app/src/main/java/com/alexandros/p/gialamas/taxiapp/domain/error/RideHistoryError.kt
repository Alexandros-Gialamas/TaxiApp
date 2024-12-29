package com.alexandros.p.gialamas.taxiapp.domain.error

import com.alexandros.p.gialamas.taxiapp.data.model.ErrorResponseResult

/**
 * Represents errors that can occur when fetching or handling the user's ride history.
 *
 * Errors can be [UserDataValidation] errors related to user input, [Network] errors related to
 * network communication, or [Local] errors related to local storage operations.
 */
sealed interface RideHistoryError : ResultError {

    /**
     * Errors related to the validation of user-provided data for ride history retrieval.
     */
    enum class UserDataValidation : RideHistoryError {
        /**
         * The customer ID provided is invalid or empty.
         */
        INVALID_CUSTOMER_ID
    }

    /**
     * Network-related errors that can occur when fetching ride history.
     */
    sealed class Network : RideHistoryError {
        /**
         * A general network error occurred (e.g., no internet connection, timeout).
         */
        data class NetworkError(val error: ErrorResponseResult? = null) : Network()

        /**
         * The server returned data in an unexpected or invalid format.
         */
        data class InvalidData(val error: ErrorResponseResult? = null) : Network()

        /**
         * The specified driver was not found in the system.
         */
        data class DriverNotFound(val error: ErrorResponseResult? = null) : Network()

        /**
         * The calculated distance for a ride in the history is invalid.
         */
        data class InvalidDistance(val error: ErrorResponseResult? = null) : Network()

        /**
         * No rides were found for the given user.
         */
        data class NoRidesFound(val error: ErrorResponseResult? = null) : Network()


        /**
         * An unknown error occurred on the server side.
         */
        data class UnknownError(val error: ErrorResponseResult? = null) : Network()
    }

    /**
     * Errors related to local storage operations when fetching ride history.
     */
    enum class Local : RideHistoryError {
        /**
         * The app failed to fetch the ride history from local storage.
         */
        FAIL_TO_FETCH_LOCAL_RIDES
    }
}