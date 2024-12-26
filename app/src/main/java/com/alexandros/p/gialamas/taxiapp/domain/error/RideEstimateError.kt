package com.alexandros.p.gialamas.taxiapp.domain.error

/**
 * Represents errors that can occur during the ride estimation process.
 *
 * Errors are categorized as either [UserDataValidation] errors, indicating problems with the user-provided
 * input, or [Network] errors, representing issues with network communication or server-side data.
 */
sealed interface RideEstimateError : ResultError {

    /**
     * Errors related to the validation of user-provided data for ride estimation.
     */
    enum class UserDataValidation : RideEstimateError {
        /**
         * The customer ID provided is invalid or empty.
         */
        INVALID_CUSTOMER_ID,

        /**
         * The specified origin location is invalid or empty.
         */
        INVALID_ORIGIN,

        /**
         * The specified destination location is invalid or empty.
         */
        INVALID_DESTINATION,
    }

    /**
     * Network-related errors that can occur during ride estimation.
     */
    enum class Network : RideEstimateError {
        /**
         * A general network error occurred (e.g., no internet connection, timeout).
         */
        NETWORK_ERROR,

        /**
         * The server returned data in an unexpected or invalid format.
         */
        INVALID_DATA,

        /**
         * The provided location (origin or destination) is invalid according to the server.
         */
        INVALID_LOCATION,

        /**
         * The customer ID provided is invalid according to the server.
         */
        INVALID_CUSTOMER_ID,

        /**
         * No available rides were found for the given criteria.
         */
        NO_RIDES_FOUND,

        /**
         * An unknown error occurred on the server side.
         */
        UNKNOWN_ERROR
    }
}