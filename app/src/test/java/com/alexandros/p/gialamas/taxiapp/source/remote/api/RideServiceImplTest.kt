package com.alexandros.p.gialamas.taxiapp.source.remote.api

import android.util.Log
import com.alexandros.p.gialamas.taxiapp.data.model.RideEstimateRequest
import com.alexandros.p.gialamas.taxiapp.domain.remote.api.RideService
import com.alexandros.p.gialamas.taxiapp.data.source.remote.api.RideServiceImpl
import com.alexandros.p.gialamas.taxiapp.domain.model.Ride
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
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
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }
        mockStatic(Log::class.java)
        `when`(Log.d(anyString(), anyString())).then { }

        httpClient = HttpClient(mockEngine) {
            install(ContentNegotiation) {
                json(json)
            }
//            install(Logging) {
//                logger = object : Logger {
//                    override fun log(message: String) {
//                         println(message)
//                    }
//                }
//                level = LogLevel.ALL
//            }
        }
        rideService = RideServiceImpl(httpClient)
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
        val response = rideService.getRideEstimate(request.customerId, request.origin, request.destination)

        assertEquals(3.2, response.distance, 0.001)
        assertEquals("12 mins", response.duration)
    }

    @Test
    fun `confirmRide returns true`() = runBlocking {
        val ride = Ride(
            origin = "Origin A",
            destination = "Destination B",
            distance = 5.0,
            duration = "20 mins",
            driver = com.alexandros.p.gialamas.taxiapp.domain.model.Driver(1, "Driver 1"),
            value = 25.0
        )
        val response = rideService.confirmRide(ride)

        assertEquals(true, response)
    }

    @Test
    fun `getRideHistory returns list of rides`() = runBlocking {
        val response = rideService.getRideHistory("CT01")

        assertEquals(1, response.rides.size)
        assertEquals("CT01", response.customerId)
    }




}