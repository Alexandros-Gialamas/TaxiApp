package com.alexandros.p.gialamas.taxiapp.data.model


import com.alexandros.p.gialamas.taxiapp.domain.model.RideHistory
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
    data class RideHistoryResponse(
        @SerialName("customer_id")
        val customerId: String,
        @SerialName("rides")
        val rides: List<RideHistory>
    ) 





