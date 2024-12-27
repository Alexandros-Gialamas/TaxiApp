package com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.alexandros.p.gialamas.taxiapp.presentation.ui.screen.ride_history.state.Rides
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.extractDate
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.extractTime
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatDurationTimeToString
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatHistoryDistance
import com.alexandros.p.gialamas.taxiapp.presentation.ui.util.format_values.formatValue

@Composable
fun RideHistoryRideItem(ride: Rides) {

    val textColor = Color.White

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = Color.Gray, shape = RoundedCornerShape(16.dp))
            .fillMaxWidth(0.9f)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (ride is Rides.Local) {
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            text = "Customer ID: ${ride.rideEntity.customerId}",
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                            textAlign = TextAlign.Center
                        )
                    }

                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        text = "Ride ID: ${
                            when (ride) {
                                is Rides.Local -> ride.rideEntity.id
                                is Rides.Network -> ride.rideHistory.id
                            }
                        }",
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Date: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                when (ride) {
                    is Rides.Local -> ride.rideEntity.date?.let { extractDate(it) }
                    is Rides.Network -> ride.rideHistory.date?.let { extractDate(it) }
                }?.let {
                    Text(
                        modifier = Modifier
                            .padding(start = 6.dp),
                        text = it,
                        color = textColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Time: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                when (ride) {
                    is Rides.Local -> ride.rideEntity.date?.let { extractTime(it) }
                    is Rides.Network -> ride.rideHistory.date?.let { extractTime(it) }
                }?.let {
                    Text(
                        modifier = Modifier
                            .padding(start = 6.dp),
                        text = it,
                        color = textColor
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Driver: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = when (ride) {
                        is Rides.Local -> ride.rideEntity.driverName
                        is Rides.Network -> ride.rideHistory.driver.name
                    },
                    color = textColor
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Origin",
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = when (ride) {
                        is Rides.Local -> ride.rideEntity.origin
                        is Rides.Network -> ride.rideHistory.origin
                    },
                    textAlign = TextAlign.Justify,
                    softWrap = true,
                    color = textColor
                )
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = "Destination",
                fontWeight = FontWeight.SemiBold,
                color = textColor
            )

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    modifier = Modifier
                        .fillMaxWidth(),
                    text = when (ride) {
                        is Rides.Local -> ride.rideEntity.destination
                        is Rides.Network -> ride.rideHistory.destination
                    },
                    textAlign = TextAlign.Justify,
                    softWrap = true,
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Distance: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = when (ride) {
                        is Rides.Local -> formatHistoryDistance(ride.rideEntity.distance)
                        is Rides.Network -> formatHistoryDistance(ride.rideHistory.distance)
                    },
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Duration: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )

                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = when (ride) {
                        is Rides.Local -> formatDurationTimeToString(ride.rideEntity.duration)
                        is Rides.Network -> formatDurationTimeToString(ride.rideHistory.duration)
                    },
                    color = textColor
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    modifier = Modifier
                        .padding(end = 6.dp),
                    text = "Value: ",
                    fontWeight = FontWeight.SemiBold,
                    color = textColor
                )
                Text(
                    modifier = Modifier
                        .padding(start = 6.dp),
                    text = "$${
                        when (ride) {
                            is Rides.Local -> formatValue(ride.rideEntity.value)
                            is Rides.Network -> formatValue(ride.rideHistory.value)
                        }
                    }",
                    color = textColor
                )
            }
        }
    }
}