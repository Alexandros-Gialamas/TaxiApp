package com.alexandros.p.gialamas.taxiapp.data.source.remote.api

import com.alexandros.p.gialamas.taxiapp.data.model.ConfirmRideRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideConfirmationResult
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateRequest
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.data.model.RideHistoryResponse
import com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_confirm.RideConfirmResponseError
import com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_confirm.ValidateRideConfirmDriverDistance
import com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_estimate.RideEstimateResponseError
import com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_estimate.ValidateRideEstimateResponse
import com.alexandros.p.gialamas.taxiapp.data.repository.error.ride_history.RideHistoryResponseError
import com.alexandros.p.gialamas.taxiapp.data.util.Constants
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideHistoryError
import com.alexandros.p.gialamas.taxiapp.domain.error.onErrorReturn
import com.alexandros.p.gialamas.taxiapp.domain.remote.api.RideService
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import timber.log.Timber
import javax.inject.Inject


/**
 * Implementation of the [RideService] interface that communicates with a remote API to handle ride-related operations.
 *
 * @property httpClient The Ktor HTTP client used for making network requests.
 * @property rideEstimateResponseError Mapper for handling errors in ride estimate responses.
 * @property validateRideEstimateResponse Validator for ride estimate responses.
 * @property rideConfirmResponseError Mapper for handling errors in ride confirmation responses.
 * @property rideHistoryResponseError Mapper for handling errors in ride history responses.
 * @property validateRideConfirmDriverDistance Validates the driver distance on ride confirmation.
 */
class RideServiceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val rideEstimateResponseError: RideEstimateResponseError,
    private val validateRideEstimateResponse: ValidateRideEstimateResponse,
    private val rideConfirmResponseError: RideConfirmResponseError,
    private val rideHistoryResponseError: RideHistoryResponseError,
    private val validateRideConfirmDriverDistance: ValidateRideConfirmDriverDistance

) : RideService {


    /**
     * Retrieves a ride estimate from the server.
     *
     * @param customerId The ID of the customer requesting the estimate.
     * @param origin The starting location of the ride.
     * @param destination The destination of the ride.
     * @return A [Result] that encapsulates either a [RideEstimateResponse] on success or a [RideEstimateError] on failure.
     */
    override suspend fun getRideEstimate(
        customerId: String,
        origin: String,
        destination: String
    ): Result<RideEstimateResponse, RideEstimateError> {

        // Construct the ride estimate request object.
        val rideRequest = RideEstimateRequest(
            customerId = customerId,
            origin = origin,
            destination = destination
        )

        // Make a POST request to the ride estimate endpoint.
        val response = httpClient.post(Constants.API_RIDE_ESTIMATE_ENDPOINT) {
            contentType(ContentType.Application.Json)
            setBody(
                rideRequest
            )
        }
        // Handle the response within a try-catch block to catch potential network or parsing errors.
        return try {
            // Check if the response status code indicates success.
            if (response.status.isSuccess()) {
                // Deserialize the response body to a RideEstimateResponse object.
                val estimateResponse = response.body<RideEstimateResponse>()

                // Validate the received RideEstimateResponse using the injected validator.
                when (validateRideEstimateResponse.validation(estimateResponse)) {
                    // If validation fails, map it to an INVALID_LOCATION error.
                    is Result.Error -> Result.Error(RideEstimateError.Network.INVALID_LOCATION)
                    // If validation succeeds, return a Success result with the validated data.
                    is Result.Success -> {
                        Result.Success(
                            data = RideEstimateResponse(
                                origin = estimateResponse.origin,
                                destination = estimateResponse.destination,
                                distance = estimateResponse.distance,
                                duration = estimateResponse.duration,
                                options = estimateResponse.options,
                                routeResponse = estimateResponse.routeResponse
                            )
                        )
                    }

                    Result.Idle -> Result.Idle
                }


            } else {
                // If the response status code is not successful, map the error using the injected error mapper.
                Result.Error(rideEstimateResponseError.mapError(response))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching ride estimate: ${e.message}")
            Result.Error(RideEstimateError.Network.UNKNOWN_ERROR)
        }
    }


    /**
     * Confirms a ride with the server.
     *
     * @param confirmRideRequest The request containing ride confirmation details.
     * @return A [Result] that encapsulates either a [RideConfirmationResult] on success or a [RideConfirmError] on failure.
     */
    override suspend fun confirmRide(confirmRideRequest: ConfirmRideRequest): Result<RideConfirmationResult, RideConfirmError> {
        // Construct the ride confirmation request object.
        val rideRequest = ConfirmRideRequest(
            customerId = confirmRideRequest.customerId,
            origin = confirmRideRequest.origin,
            destination = confirmRideRequest.destination,
            distance = confirmRideRequest.distance,
            duration = confirmRideRequest.duration,
            driver = confirmRideRequest.driver,
            value = confirmRideRequest.value
        )
        // Use a try-catch block to handle potential exceptions during the network call.
        try {
            // Make a PATCH request to the ride confirmation endpoint.
            val response =
                httpClient.patch(Constants.API_CONFIRM_RIDE_ENDPOINT) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        rideRequest
                    )
                }
            // Check if the response status code indicates success.
            return if (response.status.isSuccess()) {
                // Deserialize the response body to a RideConfirmationResult object.
                val responseBody = response.body<RideConfirmationResult>()
                // Validate the confirmation request, particularly the driver's distance.
                when (val result = validateRideConfirmDriverDistance.validation(rideRequest)) {
                    // If validation fails, return an Error result with the specific error.
                    is Result.Error -> Result.Error(result.error)
                    // If validation succeeds, return a Success result with the confirmation result.
                    is Result.Success -> Result.Success(RideConfirmationResult(responseBody.success))

                    Result.Idle -> Result.Idle
                }
            } else {
                // If the response status code is not successful, map the error using the injected error mapper.
                Result.Error(rideConfirmResponseError.mapError(response))
            }

        } catch (e: Exception) {
            Timber.e(e, "Error confirming the ride : ${e.message}")
            return Result.Error(RideConfirmError.NetWork.UNKNOWN_ERROR)
        }

    }


    /**
     * Retrieves the ride history for a given customer from the server.
     *
     * @param customerId The ID of the customer whose ride history is being requested.
     * @param driverId Optional driver ID to filter the ride history.
     * @return A [Result] that encapsulates either a [RideHistoryResponse] on success or a [RideHistoryError] on failure.
     */
    override suspend fun getRideHistory(
        customerId: String,
        driverId: Int?
    ): Result<RideHistoryResponse, RideHistoryError> {
        // Construct the URL for the ride history request, including an optional driver ID query parameter.
        val url = if (driverId == null) {
            "${Constants.API_HISTORY_RIDE_ENDPOINT}${customerId}"
        } else {
            "${Constants.API_HISTORY_RIDE_ENDPOINT}${customerId}?driver_id=$driverId"
        }
        // Use a try-catch block to handle potential exceptions.
        return try {
            // Make a GET request to the constructed URL.
            val response = httpClient.get(url) {
                contentType(ContentType.Application.Json)
            }
            // Check if the response status code indicates success.
            if (response.status.isSuccess()) {
                // Deserialize the response body to a RideHistoryResponse object.
                val historyResponse = response.body<RideHistoryResponse>()
                // Return a Success result with the ride history.
                Result.Success(
                    RideHistoryResponse(
                        customerId = historyResponse.customerId,
                        rides = historyResponse.rides
                    )
                )
            } else {
                // If the response status code is not successful, map the error using the injected error mapper.
                Result.Error(rideHistoryResponseError.mapError(response))
            }
        } catch (e: Exception) {
            Timber.e(e, "Error fetching ride history: ${e.message}")
            // In case of an exception, return an Error result with an UNKNOWN_ERROR,
            // and then transform it into a Success result with an empty RideHistoryResponse using onErrorReturn.
            Result.Error(RideHistoryError.Network.UNKNOWN_ERROR).onErrorReturn {
                RideHistoryResponse(
                    customerId = "",
                    rides = emptyList()
                )
            }
        }
    }
}


