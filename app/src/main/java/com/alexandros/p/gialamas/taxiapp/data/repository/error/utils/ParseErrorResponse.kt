package com.alexandros.p.gialamas.taxiapp.data.repository.error.utils

import com.alexandros.p.gialamas.taxiapp.data.model.ErrorResponseResult
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.json.Json
import javax.inject.Inject

class ParseErrorResponse @Inject constructor(private val json: Json) {

    suspend fun parse(response: HttpResponse): ErrorResponseResult {
        val errorBody = response.bodyAsText()
        return try {
            json.decodeFromString(ErrorResponseResult.serializer(), errorBody)
        } catch (e: Exception) {
            ErrorResponseResult("UNKNOWN_ERROR", "An unexpected error occurred.")
        }
    }
}