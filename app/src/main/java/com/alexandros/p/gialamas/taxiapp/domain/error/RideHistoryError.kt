package com.alexandros.p.gialamas.taxiapp.domain.error

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
    enum class Network : RideHistoryError {
        /**
         * A general network error occurred (e.g., no internet connection, timeout).
         */
        NETWORK_ERROR,

        /**
         * The server returned data in an unexpected or invalid format.
         */
        INVALID_DATA,

        /**
         * The specified driver was not found in the system.
         */
        DRIVER_NOT_FOUND,

        /**
         * The calculated distance for a ride in the history is invalid.
         */
        INVALID_DISTANCE,

        /**
         * No rides were found for the given user.
         */
        NO_RIDES_FOUND,

        /**
         * An unknown error occurred on the server side.
         */
        UNKNOWN_ERROR
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