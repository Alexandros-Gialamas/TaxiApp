package com.alexandros.p.gialamas.taxiapp.domain.error

import com.alexandros.p.gialamas.taxiapp.data.model.ErrorResponseResult

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
    sealed class Network : RideEstimateError {
        /**
         * A general network error occurred (e.g., no internet connection, timeout).
         */
        data class NetworkError(val error: ErrorResponseResult? = null) : Network()

        /**
         * The server returned data in an unexpected or invalid format.
         */
        data class InvalidData(val error: ErrorResponseResult? = null) : Network()

        /**
         * The provided location (origin or destination) is invalid according to the server.
         */
        data class InvalidLocation(val error: ErrorResponseResult? = null) : Network()

        /**
         * The customer ID provided is invalid according to the server.
         */
        data class InvalidCustomerId(val error: ErrorResponseResult? = null) : Network()

        /**
         * No available rides were found for the given criteria.
         */
        data class NoRidesFound(val error: ErrorResponseResult? = null) : Network()

        /**
         * An unknown error occurred on the server side.
         */
        data class UnknownError(val error: ErrorResponseResult? = null) : Network()
    }
}