package com.alexandros.p.gialamas.taxiapp.source.remote.api

import android.util.Log
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
import com.alexandros.p.gialamas.taxiapp.domain.remote.api.RideService
import com.alexandros.p.gialamas.taxiapp.data.source.remote.api.RideServiceImpl
import com.alexandros.p.gialamas.taxiapp.domain.error.Result
import com.alexandros.p.gialamas.taxiapp.domain.error.RideConfirmError
import com.alexandros.p.gialamas.taxiapp.domain.error.RideEstimateError
import com.alexandros.p.gialamas.taxiapp.domain.model.Driver
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.error_presentation.asEstimateUiText
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import junit.framework.Assert.fail
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mockStatic
import org.mockito.Mockito.`when`

class RideServiceImplTest {

    private lateinit var mockEngine: MockEngine
    private lateinit var httpClient: HttpClient
    private lateinit var rideService: RideService
    private val json = Json {
        prettyPrint = true
        isLenient = true
        ignoreUnknownKeys = true
    }

    @Before
    fun setup() {
        mockEngine = MockEngine { request ->
            respond(
                content = dealWithRequest(request.url.encodedPath),
                status = HttpStatusCode.OK,
                headers = headersOf(
                    HttpHeaders.ContentType,
                    ContentType.Application.Json.toString()
                )
            )
        }
        mockStatic(Log::class.java)
        `when`(Log.d(anyString(), anyString())).then { }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
        }
        rideService = RideServiceImpl(
            httpClient,
            rideEstimateResponseError = RideEstimateResponseError(),
            validateRideEstimateResponse = ValidateRideEstimateResponse(),
            rideConfirmResponseError = RideConfirmResponseError(),
            rideHistoryResponseError = RideHistoryResponseError(),
            validateRideConfirmDriverDistance = ValidateRideConfirmDriverDistance(),
        )
    }

    private fun dealWithRequest(encodedPath: String): String {
        return when {
            encodedPath.contains("/ride/estimate") -> {
                """
                {
                  "origin": {
                    "latitude": -23.55052,
                    "longitude": -46.633309
                  },
                  "destination": {
                    "latitude": -23.562374,
                    "longitude": -46.654331
                  },
                  "distance": 3.2,
                  "duration": "12 mins",
                  "options": [
                    {
                      "id": 1,
                      "name": "John Doe",
                      "description": "Good driver",
                      "vehicle": "Toyota Corolla",
                      "review": {
                        "rating": 4.5,
                        "comment": "Great service!"
                      },
                      "value": 15.5
                    }
                  ],
                  "routeResponse": {
                    "routes": []
                  }
                }
                """
            }

            encodedPath.contains("/ride/confirm") -> {
                "{\"success\": true}"
            }

            encodedPath.contains("/ride/CT01") -> {
                """
                {
                  "customer_id": "CT01",
                  "rides": [
                    {
                      "id": 1,
                      "date": "2024-12-11T10:00:00",
                      "origin": "Origin A",
                      "destination": "Destination B",
                      "distance": 5.0,
                      "duration": "20 mins",
                      "driver": {
                        "id": 1,
                        "name": "Driver 1"
                      },
                      "value": 25.0
                    }
                  ]
                }
                """
            }

            else -> {
                "{}"
            }
        }
    }

    @Test
    fun `getRideEstimate returns RideEstimateResponse`() = runBlocking {
        val request = RideEstimateRequest(
            customerId = "CT01",
            origin = "Origin A",
            destination = "Destination B"
        )
        val result: Result<RideEstimateResponse, RideEstimateError> =
            rideService.getRideEstimate(request.customerId, request.origin, request.destination)

        if (result is Result.Success) {
            val estimateResponse = result.data
            assertEquals(3.2, estimateResponse.distance, 0.001)
            assertEquals("12 mins", estimateResponse.duration)
        }
    }

    @Test
    fun `confirmRide returns true`(): Unit = runBlocking {
        val ride = ConfirmRideRequest(
            origin = "Origin A",
            destination = "Destination B",
            distance = 5.0,
            duration = "20 mins",
            driver = Driver(3, "Driver 3"),
            value = 25.0,
            customerId = "CT01",

            )
        val response : Result<RideConfirmationResult, RideConfirmError> =
            rideService.confirmRide(ride)

        when (response) {
            is Result.Error -> {
                assertEquals(RideConfirmError.NetWork.INVALID_DISTANCE, response.error)
            }
            is Result.Success -> {
                assertEquals(true, response.data)
            }
            Result.Idle -> Result.Idle
        }
    }

    @Test
    fun `getRideHistory returns list of rides`() = runBlocking {

        val result = rideService.getRideHistory("CT01")

        if (result is Result.Success) {
            val historyResponse = result.data
            assertEquals(1, historyResponse.rides.size)
            assertEquals("CT01", historyResponse.customerId)
        }

    }


}