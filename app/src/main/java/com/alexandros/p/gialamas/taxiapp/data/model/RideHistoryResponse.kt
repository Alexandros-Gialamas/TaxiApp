package com.alexandros.p.gialamas.taxiapp.data.model


import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

sealed class RideHistoryResponse {

    @Serializable
    data class HistoryResponse(
        @SerialName("customer_id")
        val customerId: String,
        @SerialName("rides")
        val rides: List<RideHistory>
    ) : RideHistoryResponse()

    data class Error(val response: HttpResponse) : RideHistoryResponse()

}

